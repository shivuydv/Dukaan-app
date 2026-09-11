package com.example.service

import com.example.BuildConfig
import com.example.data.model.OrderItem
import com.example.data.model.ShopItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ParseResult(
  val items: List<OrderItem>,
  val totalAmount: Double,
  val confirmationMessage: String,
  val parsedBy: String // "Gemini AI" or "Smart Kirana NLP"
)

class VoiceOrderParser {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

  suspend fun parseVoiceOrder(
    transcript: String,
    catalog: List<ShopItem>
  ): ParseResult = withContext(Dispatchers.IO) {
    val cleanTranscript = transcript.trim()
    if (cleanTranscript.isBlank()) {
      return@withContext ParseResult(
        items = emptyList(),
        totalAmount = 0.0,
        confirmationMessage = "Koi item samajh nahi aaya. Kripya dobara voice note bhejein.",
        parsedBy = "System"
      )
    }

    // Try Gemini AI if valid API key is present
    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        val geminiResult = callGeminiForOrderParsing(cleanTranscript, catalog, apiKey)
        if (geminiResult != null && geminiResult.items.isNotEmpty()) {
          return@withContext geminiResult
        }
      } catch (e: Exception) {
        e.printStackTrace()
        // Fallback to local Kirana NLP engine below
      }
    }

    // High precision local Hinglish & Hindi Kirana Rule Parser
    return@withContext parseWithLocalKiranaNlp(cleanTranscript, catalog)
  }

  private fun parseWithLocalKiranaNlp(
    text: String,
    catalog: List<ShopItem>
  ): ParseResult {
    val lower = text.lowercase()
    val detectedItems = mutableListOf<OrderItem>()

    // Word boundary clean
    val normalized = lower
      .replace(",", " aur ")
      .replace("+", " aur ")
      .replace("&", " aur ")
      .replace("bhej do", "")
      .replace("chahiye", "")
      .replace("de do", "")
      .replace("dena", "")
      .replace("bhaiya", "")
      .replace("namaste", "")
      .replace("urgent", "")
      .replace("urgently", "")

    val clauses = normalized.split(Regex("\\baur\\b|\\band\\b|\\bplus\\b|\\b,\\b|\\n"))

    for (clause in clauses) {
      val trimmedClause = clause.trim()
      if (trimmedClause.length < 2) continue

      // Quantity extraction
      val (qty, unit) = extractQuantityAndUnit(trimmedClause)

      // Find matching catalog item
      val matchedItem = findBestCatalogMatch(trimmedClause, catalog)
      if (matchedItem != null) {
        // Prevent duplicate items in same order; combine quantity
        val existingIndex = detectedItems.indexOfFirst {
          (it.itemId != null && it.itemId != 0L && it.itemId == matchedItem.id) ||
            it.name.equals(matchedItem.name, ignoreCase = true)
        }
        if (existingIndex >= 0) {
          val current = detectedItems[existingIndex]
          val newQty = current.quantity + qty
          val newTotal = newQty * matchedItem.price
          detectedItems[existingIndex] = current.copy(
            quantity = newQty,
            totalPrice = newTotal
          )
        } else {
          val finalUnit = if (unit.isNotBlank()) unit else matchedItem.unit
          val total = qty * matchedItem.price
          detectedItems.add(
            OrderItem(
              itemId = matchedItem.id,
              name = matchedItem.name,
              quantity = qty,
              unit = finalUnit,
              pricePerUnit = matchedItem.price,
              totalPrice = total,
              emoji = matchedItem.emoji
            )
          )
        }
      }
    }

    // If clauses didn't split well, do whole-text keyword scan across catalog
    if (detectedItems.isEmpty()) {
      for (shopItem in catalog) {
        val keywords = (shopItem.name + " " + shopItem.hindiName + " " + shopItem.searchKeywords).lowercase().split(" ")
        for (kw in keywords) {
          if (kw.length > 2 && lower.contains(kw)) {
            val (qty, unit) = extractQuantityAndUnit(lower)
            detectedItems.add(
              OrderItem(
                itemId = shopItem.id,
                name = shopItem.name,
                quantity = qty,
                unit = if (unit.isNotBlank()) unit else shopItem.unit,
                pricePerUnit = shopItem.price,
                totalPrice = qty * shopItem.price,
                emoji = shopItem.emoji
              )
            )
            break
          }
        }
      }
    }

    val totalAmount = detectedItems.sumOf { it.totalPrice }
    val confirmationMsg = buildConfirmationMessage(detectedItems, totalAmount)

    return ParseResult(
      items = detectedItems,
      totalAmount = totalAmount,
      confirmationMessage = confirmationMsg,
      parsedBy = "Smart Kirana NLP"
    )
  }

  private fun extractQuantityAndUnit(text: String): Pair<Double, String> {
    var quantity = 1.0
    var unit = ""

    // Hindi fraction terms (Hinglish + Devanagari)
    when {
      text.contains("aadha kilo") || text.contains("adhe kilo") || text.contains("aadha kg") || text.contains("half kg") || text.contains("आधा किलो") || text.contains("आधा") -> {
        return Pair(0.5, "kg")
      }
      text.contains("dedh kilo") || text.contains("dedh kg") || text.contains("डेढ़ किलो") || text.contains("डेढ़") -> {
        return Pair(1.5, "kg")
      }
      text.contains("dhai kilo") || text.contains("dhai kg") || text.contains("ढाई किलो") || text.contains("ढाई") -> {
        return Pair(2.5, "kg")
      }
      text.contains("paav kilo") || text.contains("250 gm") || text.contains("250g") || text.contains("पाव") -> {
        return Pair(0.25, "kg")
      }
    }

    // Number matching (digits or words)
    val digitMatch = Regex("([0-9०-९]+(?:\\.[0-9०-९]+)?)\\s*(kilo|kg|packet|pkt|litre|liter|l|gram|gm|g|piece|pc|bottles?|किलो|पैकेट|लीटर|ग्राम|पीस)?").find(text)
    if (digitMatch != null) {
      val rawNum = digitMatch.groupValues[1]
        .replace('०', '0').replace('१', '1').replace('२', '2').replace('३', '3').replace('४', '4')
        .replace('५', '5').replace('६', '6').replace('७', '7').replace('८', '8').replace('९', '9')
      quantity = rawNum.toDoubleOrNull() ?: 1.0
      val rawUnit = digitMatch.groupValues.getOrNull(2) ?: ""
      unit = normalizeUnit(rawUnit)
      return Pair(quantity, unit)
    }

    // Hindi number words (Hinglish & Devanagari)
    val hindiNumbers = mapOf(
      "ek" to 1.0, "do" to 2.0, "teen" to 3.0, "chaar" to 4.0, "char" to 4.0,
      "paanch" to 5.0, "panch" to 5.0, "chheh" to 6.0, "saat" to 7.0,
      "aath" to 8.0, "nau" to 9.0, "das" to 10.0, "one" to 1.0, "two" to 2.0,
      "एक" to 1.0, "दो" to 2.0, "तीन" to 3.0, "चार" to 4.0, "पाँच" to 5.0, "पांच" to 5.0,
      "छह" to 6.0, "सात" to 7.0, "आठ" to 8.0, "नौ" to 9.0, "दस" to 10.0
    )

    for ((word, value) in hindiNumbers) {
      if (text.contains(word)) {
        quantity = value
        break
      }
    }

    // Unit detection
    when {
      text.contains("kilo") || text.contains("kg") || text.contains("किलो") -> unit = "kg"
      text.contains("packet") || text.contains("pkt") || text.contains("पैकेट") -> unit = "packet"
      text.contains("litre") || text.contains("liter") || text.contains("lit") || text.contains("लीटर") -> unit = "litre"
      text.contains("gram") || text.contains("gm") || text.contains("ग्राम") -> unit = "gram"
      text.contains("piece") || text.contains("pc") || text.contains("पीस") -> unit = "piece"
    }

    return Pair(quantity, unit)
  }

  private fun normalizeUnit(unitStr: String): String {
    return when (unitStr.lowercase().trim()) {
      "kilo", "kg", "किलो" -> "kg"
      "packet", "pkt", "पैकेट" -> "packet"
      "litre", "liter", "l", "लीटर" -> "litre"
      "gram", "gm", "g", "ग्राम" -> "gram"
      "piece", "pc", "bottle", "पीस" -> "piece"
      else -> ""
    }
  }

  private fun findBestCatalogMatch(clause: String, catalog: List<ShopItem>): ShopItem? {
    var bestMatch: ShopItem? = null
    var maxScore = 0

    for (item in catalog) {
      var score = 0
      val keywords = (item.name + " " + item.hindiName + " " + item.searchKeywords)
        .lowercase()
        .split(Regex("[\\s,()\\-]+"))
        .filter { it.length > 2 }

      for (kw in keywords) {
        if (clause.contains(kw)) {
          score += 3
        }
      }

      if (score > maxScore) {
        maxScore = score
        bestMatch = item
      }
    }

    return if (maxScore >= 3) bestMatch else null
  }

  private suspend fun callGeminiForOrderParsing(
    transcript: String,
    catalog: List<ShopItem>,
    apiKey: String
  ): ParseResult? {
    val catalogItemsJson = catalog.joinToString(separator = ", ") {
      "{\"id\": ${it.id}, \"name\": \"${it.name}\", \"price\": ${it.price}, \"unit\": \"${it.unit}\"}"
    }

    val prompt = """
      You are an AI order assistant for an Indian Kirana store on WhatsApp.
      Customer voice message: "$transcript"
      Available shop catalog items: [$catalogItemsJson]

      Match the items mentioned in the voice note with the shop catalog items.
      Understand Hinglish numbers (ek=1, do=2, aadha kilo=0.5, etc.) and item nicknames (aalu, pyaaz, doodh, etc.).
      Return ONLY a valid JSON object matching this schema:
      {
        "items": [
          {
            "itemId": 1,
            "name": "Fresh Aalu (Potato)",
            "quantity": 2.0,
            "unit": "kg",
            "pricePerUnit": 30.0,
            "totalPrice": 60.0,
            "emoji": "🥔"
          }
        ],
        "totalAmount": 60.0
      }
    """.trimIndent()

    val requestJson = JSONObject().apply {
      val contents = JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply { put("text", prompt) })
          })
        })
      }
      put("contents", contents)
      put("generationConfig", JSONObject().apply {
        put("responseMimeType", "application/json")
        put("temperature", 0.1)
      })
    }

    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
    val body = requestJson.toString().toRequestBody("application/json".toMediaType())
    val request = Request.Builder().url(url).post(body).build()

    val response = okHttpClient.newCall(request).execute()
    if (!response.isSuccessful) return null

    val responseBody = response.body?.string() ?: return null
    val rootJson = JSONObject(responseBody)
    val candidateText = rootJson.optJSONArray("candidates")
      ?.optJSONObject(0)
      ?.optJSONObject("content")
      ?.optJSONArray("parts")
      ?.optJSONObject(0)
      ?.optString("text") ?: return null

    val parsedJson = JSONObject(candidateText)
    val itemsArray = parsedJson.optJSONArray("items") ?: return null
    val itemsList = mutableListOf<OrderItem>()

    for (i in 0 until itemsArray.length()) {
      val obj = itemsArray.getJSONObject(i)
      val matchedShopItem = catalog.find { it.id == obj.optLong("itemId") }
      itemsList.add(
        OrderItem(
          itemId = obj.optLong("itemId"),
          name = obj.optString("name"),
          quantity = obj.optDouble("quantity", 1.0),
          unit = obj.optString("unit", matchedShopItem?.unit ?: "packet"),
          pricePerUnit = obj.optDouble("pricePerUnit", matchedShopItem?.price ?: 0.0),
          totalPrice = obj.optDouble("totalPrice", (obj.optDouble("quantity", 1.0) * (matchedShopItem?.price ?: 0.0))),
          emoji = matchedShopItem?.emoji ?: "🛒"
        )
      )
    }

    val totalAmount = parsedJson.optDouble("totalAmount", itemsList.sumOf { it.totalPrice })
    return ParseResult(
      items = itemsList,
      totalAmount = totalAmount,
      confirmationMessage = buildConfirmationMessage(itemsList, totalAmount),
      parsedBy = "Gemini AI"
    )
  }

  fun buildConfirmationMessage(items: List<OrderItem>, total: Double): String {
    if (items.isEmpty()) {
      return "Maaf kijiye, order me koi item pehchan nahi paya. Kripya item ka naam aur quantity saaf bole ya Catalog se choose karein."
    }

    val sb = StringBuilder()
    sb.append("Aapka order note ho gaya hai! 🙏🛒\n\n")
    for (item in items) {
      val qtyFormatted = if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString()
      sb.append("• ${item.emoji} $qtyFormatted ${item.unit} ${item.name} = ₹${item.totalPrice.toInt()}\n")
    }
    sb.append("\n💰 *Total: ₹${total.toInt()}*\n")
    sb.append("📍 *Delivery:* Ghar par free delivery (30 mins)\n\n")
    sb.append("Order bhejne ke liye *'YES'* kahein ya neeche button dabayein.")
    return sb.toString()
  }
}

package com.example.service

import com.example.BuildConfig
import com.example.data.model.CustomerOrder
import com.example.data.model.ShopItem
import com.example.data.model.ShopSettings
import com.example.data.model.SiyaCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SiyaReply(
  val replyText: String,
  val category: SiyaCategory,
  val matchedItems: List<ShopItem>,
  val followUpPrompts: List<String>,
  val source: String // "Gemini 3.5 Flash" or "Siya Offline Core"
)

class SiyaAiService {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  suspend fun askSiya(
    userQuery: String,
    catalog: List<ShopItem>,
    settings: ShopSettings,
    orders: List<CustomerOrder>
  ): SiyaReply = withContext(Dispatchers.IO) {
    val cleanQuery = userQuery.trim()
    if (cleanQuery.isBlank()) {
      return@withContext SiyaReply(
        replyText = "Namaste! Main Siya (सिया) hoon, aapki 24/7 AI Kirana Sahayak. Main dukan ke saman, recipe, prices ya vendor business me kya madad kar sakti hoon?",
        category = SiyaCategory.GENERAL,
        matchedItems = emptyList(),
        followUpPrompts = listOf("📦 Check Stock", "🍲 Aaj kya banayein?", "🕒 Dukan timings", "📊 Aaj ki bikri"),
        source = "Siya System"
      )
    }

    // Try Gemini API if key is configured
    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        val geminiReply = callGeminiForSiya(cleanQuery, catalog, settings, orders, apiKey)
        if (geminiReply != null && geminiReply.replyText.isNotBlank()) {
          return@withContext geminiReply
        }
      } catch (e: Exception) {
        e.printStackTrace()
        // Fallback to local intelligent Siya core
      }
    }

    // Fallback to local offline Siya engine
    return@withContext answerWithLocalSiya(cleanQuery, catalog, settings, orders)
  }

  private suspend fun callGeminiForSiya(
    query: String,
    catalog: List<ShopItem>,
    settings: ShopSettings,
    orders: List<CustomerOrder>,
    apiKey: String
  ): SiyaReply? {
    val catalogSummary = catalog.joinToString("\n") {
      "- ${it.name} (${it.hindiName}): ₹${it.price.toInt()}/${it.unit} [${if (it.inStock) "In Stock" else "Out of Stock"}]"
    }

    val todaySales = orders.sumOf { it.totalAmount }
    val pendingOrders = orders.count { it.status == "NEW" || it.status == "ACCEPTED" }

    val systemInstructionText = """
      You are 'Siya' (सिया), the 24/7 Smart AI Kirana Agent and Retail Copilot for '${settings.shopName}'.
      Owner: ${settings.ownerName}, WhatsApp: ${settings.whatsappNumber}, UPI: ${settings.upiId}, Address: ${settings.shopAddress}.
      Current Store Stats: Total Orders Today: ${orders.size}, Total Sales Today: ₹${todaySales.toInt()}, Pending Packing: $pendingOrders.
      
      Store Catalog:
      $catalogSummary
      
      Instructions:
      1. You are available 24/7 to handle EVERYTHING: customer inquiries, stock checking, recipe suggestions using store ingredients, delivery policies, vendor analytics, festival WhatsApp broadcast drafts, and payment reminders.
      2. Speak warmly and naturally in Hinglish (Hindi + English) with polite Indian Kirana charm ("नमस्ते! जी बिल्कुल...").
      3. When the user asks about recipes, list a short tasty recipe and specifically highlight which ingredients are available right here in the store with their prices!
      4. If the shopkeeper asks to draft a message (WhatsApp deal, polite payment reminder), write a ready-to-copy WhatsApp message formatted with emojis.
      5. Keep replies structured, concise, and helpful.
    """.trimIndent()

    val userPrompt = """
      User Query: "$query"
      
      Respond directly to the user as Siya.
    """.trimIndent()

    val requestJson = JSONObject().apply {
      put("systemInstruction", JSONObject().apply {
        put("parts", JSONArray().apply {
          put(JSONObject().apply { put("text", systemInstructionText) })
        })
      })
      put("contents", JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply { put("text", userPrompt) })
          })
        })
      })
      put("generationConfig", JSONObject().apply {
        put("temperature", 0.4)
        put("topP", 0.9)
      })
    }

    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
    val body = requestJson.toString().toRequestBody("application/json".toMediaType())
    val request = Request.Builder().url(url).post(body).build()

    val response = okHttpClient.newCall(request).execute()
    if (!response.isSuccessful) return null

    val responseBody = response.body?.string() ?: return null
    val rootJson = JSONObject(responseBody)
    val candidates = rootJson.optJSONArray("candidates") ?: return null
    val firstCandidate = candidates.optJSONObject(0) ?: return null
    val content = firstCandidate.optJSONObject("content") ?: return null
    val parts = content.optJSONArray("parts") ?: return null
    val text = parts.optJSONObject(0)?.optString("text") ?: return null

    // Detect matched items from catalog
    val matched = findRelevantCatalogItems(query + " " + text, catalog)
    val category = detectCategory(query)

    return SiyaReply(
      replyText = text.trim(),
      category = category,
      matchedItems = matched,
      followUpPrompts = generateFollowUpPrompts(category),
      source = "Gemini 3.5 Flash"
    )
  }

  private fun answerWithLocalSiya(
    query: String,
    catalog: List<ShopItem>,
    settings: ShopSettings,
    orders: List<CustomerOrder>
  ): SiyaReply {
    val q = query.lowercase()
    val category = detectCategory(query)
    val matched = findRelevantCatalogItems(q, catalog)

    val reply = when {
      // Stock / Price queries
      q.contains("price") || q.contains("kitne") || q.contains("rate") || q.contains("stock") || q.contains("hai kya") || q.contains("available") -> {
        if (matched.isNotEmpty()) {
          val itemList = matched.joinToString("\n") {
            "• ${it.emoji} *${it.name}* (${it.hindiName}): ₹${it.price.toInt()} per ${it.unit} (${if (it.inStock) "✅ Available in stock" else "❌ Filhal out of stock"})"
          }
          "📦 *Stock Status & Rates:*\nJi bilkul! ${settings.shopName} me aapke liye yeh items available hain:\n\n$itemList\n\nAap niche diye gaye button par click karke inhe seedhe Cart me add kar sakte hain!"
        } else {
          "Humare paas fresh rashan, dudh-dahi, sabziyan aur daily needs ka pura stock 24/7 available hai! Jaise Aashirvaad Atta (₹380), Amul Butter (₹56), Taaza Doodh (₹32), aur Fresh Aalu (₹30/kg). Aap kisi specific item ka naam batayein?"
        }
      }

      // Recipe / Cooking assistant
      q.contains("recipe") || q.contains("banaye") || q.contains("sabzi") || q.contains("khana") || q.contains("maggi") || q.contains("paneer") || q.contains("chai") || q.contains("daal") -> {
        val recipeText = when {
          q.contains("maggi") -> {
            "🍜 *Siya's Special Butter Tadka Maggi:*\n1. Kadhai me 1 chammach Amul Butter garam karein.\n2. Baarik kata pyaaz aur tamatar fry karein.\n3. Maggi masala aur 1.5 cup paani daalkar 2 minute ubaalein.\n\n🛒 *Store Ingredients:*\n• Maggi 2-Minute Noodles (₹14)\n• Amul Butter (₹56)\n• Desi Tamatar & Pyaaz"
          }
          q.contains("chai") -> {
            "☕ *Khadak Masala Chai:*\n1. Paani me adrak aur elaichi crush karke ubaalein.\n2. Wagh Bakri Chai patti daalkar rang aane dein.\n3. Taaza Amul Doodh daal kar 2 khol aane tak pakaayein.\n\n🛒 *Store Ingredients:*\n• Wagh Bakri Tea (₹140)\n• Amul Taaza Milk (₹32)"
          }
          else -> {
            "🍲 *Desi Aalu Tamatar Ki Rasedaar Sabzi:*\n1. Kadhai me tel garam karein, jeera aur hing ka tadka lagayein.\n2. Desi tamatar aur haldi-mirch bhunein.\n3. Uble huye aalu tod kar daalein aur 5 min sim par pakaayein.\n\n🛒 *Store Ingredients:*\n• Fresh Aalu (Potato): ₹30/kg\n• Fresh Desi Tamatar: ₹40/kg\n• Tata Salt & Spices"
          }
        }
        recipeText
      }

      // Store Timings, Delivery & Policies
      q.contains("time") || q.contains("timing") || q.contains("khuli") || q.contains("delivery") || q.contains("address") || q.contains("upi") || q.contains("payment") -> {
        "🏪 *${settings.shopName} - 24/7 Store Info:*\n" +
          "• *Dukaan Timings:* 7:00 AM se 11:00 PM (WhatsApp AI orders 24/7 active)\n" +
          "• *Free Home Delivery:* ₹${settings.minOrderAmount.toInt()} se upar ke order par FREE delivery!\n" +
          "• *Payment Methods:* Google Pay, PhonePe, Paytm (UPI: ${settings.upiId}) aur Cash on Delivery (COD).\n" +
          "• *Address:* ${settings.shopAddress}\n" +
          "• *Helpline:* ${settings.whatsappNumber}"
      }

      // Vendor Copilot: Sales, Analytics, and Messages
      q.contains("bikri") || q.contains("sales") || q.contains("order") || q.contains("earning") || q.contains("dashboard") -> {
        val totalSales = orders.sumOf { it.totalAmount }
        val newOrders = orders.count { it.status == "NEW" }
        "📊 *Dukaan 24/7 Live Analytics:*\n" +
          "• Aaj ki Total Bikri: *₹${totalSales.toInt()}*\n" +
          "• Total Orders: *${orders.size}*\n" +
          "• Naye Pending Orders: *${newOrders}* (Action required)\n" +
          "• Voice Order AI Share: *75%*\n\nAap 'Vendor Dashboard' tab me jakar packing list check kar sakte hain!"
      }

      // Marketing / WhatsApp message draft
      q.contains("message") || q.contains("draft") || q.contains("offer") || q.contains("discount") || q.contains("festival") || q.contains("udhaar") || q.contains("reminder") -> {
        if (q.contains("udhaar") || q.contains("reminder")) {
          "📝 *Polite Payment Reminder Draft (WhatsApp):*\n\n" +
            "\"Namaste Ji 🙏 ${settings.shopName} se anurodh hai ki aapka pichla rashan bill (₹450) baaki hai. Kripya samay milte hi UPI (${settings.upiId}) par bhej dein. Dhanyawad!\"\n\n(Aap ise copy karke customer ko bhej sakte hain)."
        } else {
          "🎉 *Special Weekend Offer Broadcast Draft:*\n\n" +
            "\"🌟 *${settings.shopName} - Weekend Maha Bachat!* 🌟\n" +
            "Aashirvaad Atta, Amul Dairy aur Fresh Sabziyon par paayein vishesh chhoot!\n" +
            "✅ Ghar baithe WhatsApp par bolkar order karein.\n" +
            "🚚 30 Minute me FREE Delivery (Orders above ₹${settings.minOrderAmount.toInt()}).\n" +
            "📞 Abhi WhatsApp karein: ${settings.whatsappNumber}\n" +
            "Dhanyawad!\""
        }
      }

      // Greetings / Anything else
      else -> {
        "Namaste! Main Siya hoon, aapki 24/7 AI Kirana Copilot 😊\n\nMain aapko store ke kisi bhi item ka price aur stock bata sakti hoon, tasty recipes ke ingredients recommend kar sakti hoon, aur dukaandar ke liye sales report ya WhatsApp offers draft kar sakti hoon. Batayein, main kya karoon?"
      }
    }

    return SiyaReply(
      replyText = reply,
      category = category,
      matchedItems = matched,
      followUpPrompts = generateFollowUpPrompts(category),
      source = "Siya 24/7 Core"
    )
  }

  private fun detectCategory(query: String): SiyaCategory {
    val q = query.lowercase()
    return when {
      q.contains("price") || q.contains("kitne") || q.contains("rate") || q.contains("stock") || q.contains("hai kya") || q.contains("available") || q.contains("mil jayega") -> SiyaCategory.STOCK_INQUIRY
      q.contains("recipe") || q.contains("banaye") || q.contains("vidhi") || q.contains("kaise") || q.contains("cook") -> SiyaCategory.RECIPE_ASSISTANT
      q.contains("time") || q.contains("timing") || q.contains("delivery") || q.contains("address") || q.contains("upi") -> SiyaCategory.STORE_POLICIES
      q.contains("bikri") || q.contains("sales") || q.contains("order") || q.contains("earning") -> SiyaCategory.VENDOR_COPILOT
      q.contains("message") || q.contains("draft") || q.contains("offer") || q.contains("reminder") -> SiyaCategory.MARKETING_DRAFT
      else -> SiyaCategory.GENERAL
    }
  }

  private fun findRelevantCatalogItems(text: String, catalog: List<ShopItem>): List<ShopItem> {
    val lower = text.lowercase()
    return catalog.filter { item ->
      lower.contains(item.name.lowercase()) ||
        lower.contains(item.hindiName.lowercase()) ||
        (item.name.contains("Aalu", ignoreCase = true) && (lower.contains("aalu") || lower.contains("potato") || lower.contains("aloo"))) ||
        (item.name.contains("Pyaaz", ignoreCase = true) && (lower.contains("pyaaz") || lower.contains("onion"))) ||
        (item.name.contains("Tamatar", ignoreCase = true) && (lower.contains("tamatar") || lower.contains("tomato"))) ||
        (item.name.contains("Butter", ignoreCase = true) && (lower.contains("butter") || lower.contains("makkhan"))) ||
        (item.name.contains("Milk", ignoreCase = true) && (lower.contains("doodh") || lower.contains("milk"))) ||
        (item.name.contains("Atta", ignoreCase = true) && (lower.contains("atta") || lower.contains("flour"))) ||
        (item.name.contains("Maggi", ignoreCase = true) && lower.contains("maggi")) ||
        (item.name.contains("Surf", ignoreCase = true) && (lower.contains("surf") || lower.contains("detergent")))
    }.take(4)
  }

  private fun generateFollowUpPrompts(category: SiyaCategory): List<String> {
    return when (category) {
      SiyaCategory.STOCK_INQUIRY -> listOf("🛒 Add items to Cart", "🍲 Suggest a recipe", "🚚 Delivery timings", "💰 Payment UPI details")
      SiyaCategory.RECIPE_ASSISTANT -> listOf("🛒 Add all ingredients to cart", "☕ Khadak Chai recipe", "🍜 Quick Maggi recipe", "🕒 Store delivery status")
      SiyaCategory.STORE_POLICIES -> listOf("📦 Check item stock", "💰 Minimum order limit", "📍 Store exact address", "💬 WhatsApp order now")
      SiyaCategory.VENDOR_COPILOT -> listOf("🎉 Generate WhatsApp Offer", "🔔 Pending Orders List", "📝 Payment reminder draft", "📦 Low stock items")
      SiyaCategory.MARKETING_DRAFT -> listOf("📱 Copy to WhatsApp", "📊 Check Today's Sales", "📦 View full catalog", "🍲 Suggest daily recipe")
      SiyaCategory.GENERAL -> listOf("📦 Check Potato & Onion stock", "🍲 What to cook today?", "🕒 Dukan timings", "📊 Today's sales")
    }
  }
}

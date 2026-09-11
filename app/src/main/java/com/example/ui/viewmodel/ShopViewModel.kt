package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.CustomerOrder
import com.example.data.model.MessageSender
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.ShopItem
import com.example.data.model.ShopSettings
import com.example.data.model.SiyaCategory
import com.example.data.model.SiyaMessage
import com.example.data.repository.ShopRepository
import com.example.service.ParseResult
import com.example.service.SiyaAiService
import com.example.service.SiyaReply
import com.example.service.SpeechManager
import com.example.service.VoiceOrderParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavigationTab {
  WHATSAPP_SIMULATOR,
  SIYA_AI,
  VENDOR_DASHBOARD,
  INTERACTIVE_CATALOG,
  MARKETING_STAND
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ShopRepository
  private val voiceParser = VoiceOrderParser()
  private val speechManager = SpeechManager(application)
  private val siyaService = SiyaAiService()

  // Siya 24/7 AI Agent State
  private val _siyaMessages = MutableStateFlow<List<SiyaMessage>>(
    listOf(
      SiyaMessage(
        isUser = false,
        text = "Namaste! 🙏 Main Siya (सिया) hoon, aapki 24/7 AI Kirana Sahayak.\n\nAap mujhse dukan ka stock, items ke prices, recipes, store policies, aaj ki bikri ya WhatsApp offer messages pooch sakte hain!",
        suggestedPrompts = listOf(
          "📦 Check Aalu & Pyaaz stock",
          "🍲 Butter Maggi Recipe",
          "🕒 Dukan kab tak khuli hai?",
          "📊 Aaj ki total bikri",
          "🎉 Weekend offer draft"
        ),
        actionCategory = SiyaCategory.GENERAL
      )
    )
  )
  val siyaMessages: StateFlow<List<SiyaMessage>> = _siyaMessages.asStateFlow()

  private val _isSiyaThinking = MutableStateFlow(false)
  val isSiyaThinking: StateFlow<Boolean> = _isSiyaThinking.asStateFlow()

  private val _siyaVoiceOutputEnabled = MutableStateFlow(true)
  val siyaVoiceOutputEnabled: StateFlow<Boolean> = _siyaVoiceOutputEnabled.asStateFlow()

  fun toggleSiyaVoiceOutput() {
    _siyaVoiceOutputEnabled.value = !_siyaVoiceOutputEnabled.value
    if (!_siyaVoiceOutputEnabled.value) {
      speechManager.stop()
    }
  }

  fun sendSiyaQuery(query: String, isVoice: Boolean = false) {
    val clean = query.trim()
    if (clean.isBlank()) return

    viewModelScope.launch {
      val userMsg = SiyaMessage(
        isUser = true,
        text = clean,
        isVoice = isVoice
      )
      _siyaMessages.value = _siyaMessages.value + userMsg
      _isSiyaThinking.value = true

      try {
        val reply = siyaService.askSiya(
          userQuery = clean,
          catalog = allItems.value,
          settings = _shopSettings.value,
          orders = allOrders.value
        )
        val aiMsg = SiyaMessage(
          isUser = false,
          text = reply.replyText,
          recommendedItems = reply.matchedItems,
          suggestedPrompts = reply.followUpPrompts,
          actionCategory = reply.category
        )
        _siyaMessages.value = _siyaMessages.value + aiMsg

        if ((isVoice || _siyaVoiceOutputEnabled.value) && _shopSettings.value.voiceAssistantEnabled) {
          val cleanSpoken = reply.replyText
            .replace("*", "")
            .replace("#", "")
            .replace("•", ",")
          speechManager.speak(cleanSpoken)
        }
      } catch (e: Exception) {
        val errorMsg = SiyaMessage(
          isUser = false,
          text = "Maaf kijiye, abhi network me thoda issue hai. Kripya thodi der me dobara poochein!",
          suggestedPrompts = listOf("📦 Check Stock", "🕒 Dukan Timings", "🍲 Recipe")
        )
        _siyaMessages.value = _siyaMessages.value + errorMsg
      } finally {
        _isSiyaThinking.value = false
      }
    }
  }

  fun clearSiyaHistory() {
    speechManager.stop()
    _siyaMessages.value = listOf(
      SiyaMessage(
        isUser = false,
        text = "Namaste! 🙏 Naya session shuru ho gaya hai. Siya 24/7 aapki seva me hazir hai. Poochiye kuch bhi!",
        suggestedPrompts = listOf(
          "📦 Check Aalu & Pyaaz stock",
          "🍲 Tasty Khadak Chai Recipe",
          "🕒 Store Delivery & UPI Info",
          "📊 Today's Total Sales"
        ),
        actionCategory = SiyaCategory.GENERAL
      )
    )
  }

  fun addAllSiyaItemsToCart(items: List<ShopItem>) {
    items.forEach { item ->
      addToCart(item.id)
    }
  }

  init {
    val db = AppDatabase.getDatabase(application, viewModelScope)
    repository = ShopRepository(db.shopItemDao(), db.orderDao())
    viewModelScope.launch(Dispatchers.IO) {
      repository.ensureInitialDataSeeded()
    }
  }

  fun reloadAllDemoData() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.resetAllDemoData()
    }
  }

  fun reloadCatalogDemoData() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.resetCatalogDemoData()
    }
  }

  fun reloadOrdersDemoData() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.resetOrdersDemoData()
    }
  }

  // Invoice / Bill Receipt state
  private val _selectedOrderForInvoice = MutableStateFlow<CustomerOrder?>(null)
  val selectedOrderForInvoice: StateFlow<CustomerOrder?> = _selectedOrderForInvoice.asStateFlow()

  fun viewInvoice(order: CustomerOrder) {
    _selectedOrderForInvoice.value = order
  }

  fun dismissInvoice() {
    _selectedOrderForInvoice.value = null
  }

  // Shop Settings Dialog state
  private val _showSettingsDialog = MutableStateFlow(false)
  val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

  fun openSettings() {
    _showSettingsDialog.value = true
  }

  fun closeSettings() {
    _showSettingsDialog.value = false
  }

  // TTS / Dukaan Soundbox methods
  fun speakText(text: String) {
    speechManager.speak(text)
  }

  fun announceOrder(order: CustomerOrder) {
    val items = deserializeItems(order.itemsJson)
    val itemSummary = items.take(3).joinToString(", ") { "${it.quantity.toInt()} ${it.name}" }
    val speech = "दुकान वॉइस पर नया आर्डर! ग्राहक ${order.customerName}. कुल ${order.totalAmount.toInt()} रुपये. सामान: $itemSummary"
    speechManager.speak(speech)
  }

  // Navigation state
  private val _activeTab = MutableStateFlow(AppNavigationTab.WHATSAPP_SIMULATOR)
  val activeTab: StateFlow<AppNavigationTab> = _activeTab.asStateFlow()

  fun setTab(tab: AppNavigationTab) {
    _activeTab.value = tab
  }

  // Reactive Database flows
  val allItems: StateFlow<List<ShopItem>> = repository.allItems.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val allOrders: StateFlow<List<CustomerOrder>> = repository.allOrders.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val newOrdersCount: StateFlow<Int> = repository.newOrdersCount.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0
  )

  val totalRevenue: StateFlow<Double?> = repository.totalRevenue.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0.0
  )

  // Shop Settings
  private val _shopSettings = MutableStateFlow(ShopSettings())
  val shopSettings: StateFlow<ShopSettings> = _shopSettings.asStateFlow()

  fun updateSettings(settings: ShopSettings) {
    _shopSettings.value = settings
  }

  // Vendor order list filter
  private val _orderFilter = MutableStateFlow<String?>("ALL")
  val orderFilter: StateFlow<String?> = _orderFilter.asStateFlow()

  fun setOrderFilter(status: String?) {
    _orderFilter.value = status
  }

  // Catalog cart for interactive customer ordering
  private val _catalogCart = MutableStateFlow<Map<Long, Int>>(emptyMap())
  val catalogCart: StateFlow<Map<Long, Int>> = _catalogCart.asStateFlow()

  fun addToCart(itemId: Long) {
    val current = _catalogCart.value.toMutableMap()
    current[itemId] = (current[itemId] ?: 0) + 1
    _catalogCart.value = current
  }

  fun removeFromCart(itemId: Long) {
    val current = _catalogCart.value.toMutableMap()
    val count = current[itemId] ?: 0
    if (count > 1) {
      current[itemId] = count - 1
    } else {
      current.remove(itemId)
    }
    _catalogCart.value = current
  }

  fun clearCart() {
    _catalogCart.value = emptyMap()
  }

  // WhatsApp Chat Simulator state
  private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        sender = MessageSender.STORE_BOT,
        text = "Namaste! 🙏 *Shree Ganesh Kirana Store* me aapka swagat hai.\n\nAap WhatsApp par **voice note** bhej kar ya text likh kar order de sakte hain. Try karein: *\"Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do\"*",
        quickActions = listOf(
          "🎤 Voice Note Bhejo",
          "📖 Shop Catalog Dekho",
          "⚡ Instant 2kg Aalu + Butter"
        )
      )
    )
  )
  val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

  private val _isAiProcessing = MutableStateFlow(false)
  val isAiProcessing: StateFlow<Boolean> = _isAiProcessing.asStateFlow()

  private val _pendingParsedResult = MutableStateFlow<ParseResult?>(null)
  val pendingParsedResult: StateFlow<ParseResult?> = _pendingParsedResult.asStateFlow()

  // New order alert banner for vendor
  private val _recentVendorNotification = MutableStateFlow<String?>(null)
  val recentVendorNotification: StateFlow<String?> = _recentVendorNotification.asStateFlow()

  fun dismissNotification() {
    _recentVendorNotification.value = null
  }

  // Send a voice note or message in WhatsApp
  fun sendCustomerVoiceNote(
    transcript: String,
    durationSec: Int = 4
  ) {
    viewModelScope.launch {
      // 1. Add customer voice note bubble
      val customerMsg = ChatMessage(
        sender = MessageSender.CUSTOMER,
        text = transcript,
        isVoiceNote = true,
        voiceDurationSec = durationSec
      )
      _chatMessages.value = _chatMessages.value + customerMsg
      _isAiProcessing.value = true

      delay(1200) // Simulated network & transcription latency

      // 2. Parse using AI engine
      val catalog = allItems.value
      val result = voiceParser.parseVoiceOrder(transcript, catalog)
      _pendingParsedResult.value = result
      _isAiProcessing.value = false

      // 3. Add bot confirmation message
      val botReply = ChatMessage(
        sender = MessageSender.STORE_BOT,
        text = result.confirmationMessage,
        quickActions = if (result.items.isNotEmpty()) {
          listOf("✅ Haan, Order Confirm Karo", "✏️ Change Karna Hai", "❌ Cancel")
        } else {
          listOf("📖 Catalog se Chuniye", "🎤 Dobara Boliye")
        }
      )
      _chatMessages.value = _chatMessages.value + botReply
    }
  }

  // Customer confirms order
  fun confirmPendingOrder(
    customerName: String = "Rahul Sharma",
    phone: String = "+91 98765 43210",
    address: String = "Flat 204, Radha Krishna Apts, Sector 4"
  ) {
    val parseResult = _pendingParsedResult.value ?: return
    if (parseResult.items.isEmpty()) return

    viewModelScope.launch {
      val orderNumber = "ORD-" + (1000 + (100..999).random())
      val itemsJson = repository.serializeOrderItems(parseResult.items)

      val newOrder = CustomerOrder(
        orderNumber = orderNumber,
        customerName = customerName,
        customerPhone = phone,
        isVoiceOrder = true,
        rawVoiceTranscript = parseResult.items.joinToString(", ") { "${it.quantity} ${it.unit} ${it.name}" },
        itemsJson = itemsJson,
        totalAmount = parseResult.totalAmount,
        status = OrderStatus.NEW.name,
        orderType = "DELIVERY",
        deliveryAddress = address,
        timestamp = System.currentTimeMillis()
      )

      val orderId = repository.insertOrder(newOrder)
      _pendingParsedResult.value = null

      // Bot confirmation response
      val successMsg = ChatMessage(
        sender = MessageSender.STORE_BOT,
        text = "🎉 *Order Confirm Ho Gaya!*\nOrder ID: *$orderNumber*\nTotal: *₹${parseResult.totalAmount.toInt()}*\n\nDukaandar ko notification chala gaya hai. Wo jaldi pack karke delivery ke liye bhej rahe hain! 🛵",
        orderReferenceId = orderId,
        quickActions = listOf("📦 Order Status Check Karein", "🧾 Bill / Invoice Dekho")
      )
      _chatMessages.value = _chatMessages.value + successMsg

      // Trigger vendor notification
      _recentVendorNotification.value = "🔔 Naya Voice Order! $customerName (₹${parseResult.totalAmount.toInt()})"

      if (_shopSettings.value.voiceAssistantEnabled) {
        speechManager.speak("दुकान वॉइस पर नया आर्डर! ग्राहक $customerName का ${parseResult.totalAmount.toInt()} रुपये का आर्डर आया है.")
      }
    }
  }

  // Create order from Interactive Catalog cart
  fun checkoutCatalogCart(
    customerName: String = "Pooja Verma",
    phone: String = "+91 99887 76655",
    address: String = "B-12, Sector 15"
  ) {
    val cart = _catalogCart.value
    if (cart.isEmpty()) return

    viewModelScope.launch {
      val catalog = allItems.value.associateBy { it.id }
      val orderItems = cart.mapNotNull { (itemId, qty) ->
        val shopItem = catalog[itemId] ?: return@mapNotNull null
        OrderItem(
          itemId = shopItem.id,
          name = shopItem.name,
          quantity = qty.toDouble(),
          unit = shopItem.unit,
          pricePerUnit = shopItem.price,
          totalPrice = qty * shopItem.price,
          emoji = shopItem.emoji
        )
      }

      val total = orderItems.sumOf { it.totalPrice }
      val orderNumber = "CAT-" + (2000 + (100..999).random())
      val itemsJson = repository.serializeOrderItems(orderItems)

      val order = CustomerOrder(
        orderNumber = orderNumber,
        customerName = customerName,
        customerPhone = phone,
        isVoiceOrder = false,
        rawVoiceTranscript = "Catalog click-to-order",
        itemsJson = itemsJson,
        totalAmount = total,
        status = OrderStatus.NEW.name,
        orderType = "DELIVERY",
        deliveryAddress = address,
        timestamp = System.currentTimeMillis()
      )

      val id = repository.insertOrder(order)
      clearCart()

      // Add to WhatsApp chat as catalog shared order
      val summaryText = orderItems.joinToString("\n") { "• ${it.emoji} ${it.quantity.toInt()} ${it.unit} ${it.name} (₹${it.totalPrice.toInt()})" }
      val cartMsg = ChatMessage(
        sender = MessageSender.CUSTOMER,
        text = "🛒 *Catalog Order Placed:*\n$summaryText\n\nTotal: ₹${total.toInt()}"
      )
      val confirmMsg = ChatMessage(
        sender = MessageSender.STORE_BOT,
        text = "Dhanyawad! Order *$orderNumber* confirm ho gaya hai! Dukaandar abhi pack kar rahe hain.",
        orderReferenceId = id
      )
      _chatMessages.value = _chatMessages.value + cartMsg + confirmMsg
      _recentVendorNotification.value = "🔔 Naya Catalog Order! $customerName (₹${total.toInt()})"
      _activeTab.value = AppNavigationTab.WHATSAPP_SIMULATOR
    }
  }

  // Vendor actions
  fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
    viewModelScope.launch {
      repository.updateOrderStatus(orderId, newStatus)

      // Auto send WhatsApp update to customer simulation
      val updateMsg = when (newStatus) {
        OrderStatus.ACCEPTED -> "✅ *Dukaandar ne order accept kar liya hai!* Packing shuru ho rahi hai."
        OrderStatus.PACKED -> "📦 *Aapka order pack ho gaya hai!* Abhi delivery boy ko assign kar rahe hain."
        OrderStatus.OUT_FOR_DELIVERY -> "🛵 *Order raste me hai!* Agle 10-15 minute me aapke ghar pahunch jayega."
        OrderStatus.COMPLETED -> "✨ *Order deliver ho gaya hai!* Dukaandar ki taraf se dhanyawad! Dobara zaroor order karein."
        OrderStatus.REJECTED -> "❌ *Maaf kijiye, kuch items out of stock hone ki wajah se order cancel hua hai.*"
        OrderStatus.NEW -> "Order pending review."
      }

      val systemMsg = ChatMessage(
        sender = MessageSender.SYSTEM,
        text = updateMsg,
        orderReferenceId = orderId
      )
      _chatMessages.value = _chatMessages.value + systemMsg
    }
  }

  fun updateItemStock(itemId: Long, inStock: Boolean) {
    viewModelScope.launch {
      repository.updateStock(itemId, inStock)
    }
  }

  fun updateItemPrice(itemId: Long, newPrice: Double) {
    viewModelScope.launch {
      repository.updatePrice(itemId, newPrice)
    }
  }

  fun addNewItem(
    name: String,
    hindiName: String,
    category: String,
    price: Double,
    unit: String,
    emoji: String
  ) {
    viewModelScope.launch {
      val newItem = ShopItem(
        name = name,
        hindiName = hindiName,
        category = category,
        price = price,
        unit = unit,
        emoji = emoji,
        searchKeywords = "$name $hindiName $category".lowercase()
      )
      repository.insertItem(newItem)
    }
  }

  fun deleteItem(item: ShopItem) {
    viewModelScope.launch {
      repository.deleteItem(item)
    }
  }

  fun deserializeItems(json: String): List<OrderItem> {
    return repository.deserializeOrderItems(json)
  }

  override fun onCleared() {
    super.onCleared()
    speechManager.shutdown()
  }
}

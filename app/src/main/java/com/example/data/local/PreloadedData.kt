package com.example.data.local

import com.example.data.model.CustomerOrder
import com.example.data.model.OrderStatus
import com.example.data.model.ShopItem

object PreloadedData {
  val initialShopItems = listOf(
    // Sabzi & Fruits
    ShopItem(
      id = 1L,
      name = "Fresh Aalu (Potato)",
      hindiName = "आलू",
      category = "Sabzi & Fresh",
      price = 30.0,
      unit = "kg",
      inStock = true,
      emoji = "🥔",
      searchKeywords = "aalu potato aloo alu sabzi vegetable"
    ),
    ShopItem(
      id = 2L,
      name = "Pyaaz (Onion)",
      hindiName = "प्याज",
      category = "Sabzi & Fresh",
      price = 35.0,
      unit = "kg",
      inStock = true,
      emoji = "🧅",
      searchKeywords = "pyaaz onion pyaj kanda vegetable"
    ),
    ShopItem(
      id = 3L,
      name = "Desi Tamatar (Tomato)",
      hindiName = "टमाटर",
      category = "Sabzi & Fresh",
      price = 40.0,
      unit = "kg",
      inStock = true,
      emoji = "🍅",
      searchKeywords = "tamatar tomato tamatar sabzi"
    ),
    ShopItem(
      id = 4L,
      name = "Nimbu (Lemon)",
      hindiName = "नींबू",
      category = "Sabzi & Fresh",
      price = 5.0,
      unit = "piece",
      inStock = true,
      emoji = "🍋",
      searchKeywords = "nimbu lemon lime"
    ),

    // Dairy & Bakery
    ShopItem(
      id = 5L,
      name = "Amul Butter 100g",
      hindiName = "अमूल मक्खन",
      category = "Dairy & Bakery",
      price = 56.0,
      unit = "packet",
      inStock = true,
      emoji = "🧈",
      searchKeywords = "amul butter makhan maska dairy"
    ),
    ShopItem(
      id = 6L,
      name = "Amul Taaza Doodh 500ml",
      hindiName = "अमूल ताजा दूध",
      category = "Dairy & Bakery",
      price = 27.0,
      unit = "packet",
      inStock = true,
      emoji = "🥛",
      searchKeywords = "amul taaza doodh milk pouch"
    ),
    ShopItem(
      id = 7L,
      name = "Britannia Brown Bread 400g",
      hindiName = "ब्राउन ब्रेड",
      category = "Dairy & Bakery",
      price = 50.0,
      unit = "packet",
      inStock = true,
      emoji = "🍞",
      searchKeywords = "bread brown britannia pav"
    ),
    ShopItem(
      id = 8L,
      name = "Mother Dairy Dahi 400g",
      hindiName = "दही",
      category = "Dairy & Bakery",
      price = 35.0,
      unit = "packet",
      inStock = true,
      emoji = "🥣",
      searchKeywords = "dahi curd yogurt mother dairy"
    ),

    // Kirana & Staples
    ShopItem(
      id = 9L,
      name = "Aashirvaad Shudh Chakki Atta 5kg",
      hindiName = "आशीर्वाद आटा",
      category = "Kirana & Staples",
      price = 245.0,
      unit = "packet",
      inStock = true,
      emoji = "🌾",
      searchKeywords = "aashirvaad shudh chakki atta flour gehu"
    ),
    ShopItem(
      id = 10L,
      name = "Tata Salt (Namak) 1kg",
      hindiName = "टाटा नमक",
      category = "Kirana & Staples",
      price = 28.0,
      unit = "packet",
      inStock = true,
      emoji = "🧂",
      searchKeywords = "tata salt namak iodine staple"
    ),
    ShopItem(
      id = 11L,
      name = "Madhur Pure Sugar (Cheeni) 1kg",
      hindiName = "चीनी (शक्कर)",
      category = "Kirana & Staples",
      price = 46.0,
      unit = "kg",
      inStock = true,
      emoji = "🍬",
      searchKeywords = "cheeni sugar chini madhur shukkar"
    ),
    ShopItem(
      id = 12L,
      name = "Fortune Kachi Ghani Mustard Oil 1L",
      hindiName = "सरसों तेल",
      category = "Kirana & Staples",
      price = 145.0,
      unit = "litre",
      inStock = true,
      emoji = "🫒",
      searchKeywords = "fortune mustard oil sarson tel sarso tel kachi ghani"
    ),
    ShopItem(
      id = 13L,
      name = "Tata Tea Gold 250g",
      hindiName = "टाटा चाय",
      category = "Kirana & Staples",
      price = 150.0,
      unit = "packet",
      inStock = true,
      emoji = "☕",
      searchKeywords = "tata tea chai chai patti gold tea"
    ),

    // Cleaning & Household
    ShopItem(
      id = 14L,
      name = "Surf Excel Easy Wash 500g",
      hindiName = "सर्फ एक्सेल",
      category = "Cleaning & Home",
      price = 75.0,
      unit = "packet",
      inStock = true,
      emoji = "🧼",
      searchKeywords = "surf excel detergent powder washing powder kapde dhone wala"
    ),
    ShopItem(
      id = 15L,
      name = "Vim Dishwash Bar 300g",
      hindiName = "विम साबुन",
      category = "Cleaning & Home",
      price = 20.0,
      unit = "piece",
      inStock = true,
      emoji = "🧼",
      searchKeywords = "vim bar bartan soap dishwash"
    ),
    ShopItem(
      id = 16L,
      name = "Dettol Original Soap 75g",
      hindiName = "डेटॉल साबुन",
      category = "Cleaning & Home",
      price = 38.0,
      unit = "piece",
      inStock = true,
      emoji = "🫧",
      searchKeywords = "dettol soap bathing sabun"
    ),

    // Snacks & Instant
    ShopItem(
      id = 17L,
      name = "Maggi 2-Minute Masala Noodles",
      hindiName = "मैगी नूडल्स",
      category = "Snacks & Instant",
      price = 14.0,
      unit = "packet",
      inStock = true,
      emoji = "🍜",
      searchKeywords = "maggi noodles masala 2 minute snacks"
    ),
    ShopItem(
      id = 18L,
      name = "Parle-G Gold Biscuits 1kg",
      hindiName = "पारले-जी बिस्कुट",
      category = "Snacks & Instant",
      price = 90.0,
      unit = "packet",
      inStock = true,
      emoji = "🍪",
      searchKeywords = "parle g biscuit chai biscuit glucose"
    )
  )

  val sampleInitialOrders = listOf(
    CustomerOrder(
      orderNumber = "ORD-2041",
      customerName = "Ramesh Sharma",
      customerPhone = "+91 98210 54321",
      isVoiceOrder = true,
      rawVoiceTranscript = "Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do.",
      itemsJson = """[{"name":"Fresh Aalu (Potato)","quantity":2.0,"unit":"kg","pricePerUnit":30.0,"totalPrice":60.0,"emoji":"🥔"},{"name":"Amul Butter 100g","quantity":1.0,"unit":"packet","pricePerUnit":56.0,"totalPrice":56.0,"emoji":"🧈"},{"name":"Surf Excel Easy Wash 500g","quantity":1.0,"unit":"packet","pricePerUnit":75.0,"totalPrice":75.0,"emoji":"🧼"}]""",
      totalAmount = 191.0,
      status = OrderStatus.NEW.name,
      orderType = "DELIVERY",
      deliveryAddress = "Flat 302, Green Valley Apts, Sector 9",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
      paymentMethod = "Cash on Delivery (COD)"
    ),
    CustomerOrder(
      orderNumber = "ORD-2040",
      customerName = "Sunita Verma",
      customerPhone = "+91 99102 33445",
      isVoiceOrder = true,
      rawVoiceTranscript = "Namaste bhaiya, do packet Amul Taaza doodh aur ek brown bread bhej dena sham ko.",
      itemsJson = """[{"name":"Amul Taaza Doodh 500ml","quantity":2.0,"unit":"packet","pricePerUnit":27.0,"totalPrice":54.0,"emoji":"🥛"},{"name":"Britannia Brown Bread 400g","quantity":1.0,"unit":"packet","pricePerUnit":50.0,"totalPrice":50.0,"emoji":"🍞"}]""",
      totalAmount = 104.0,
      status = OrderStatus.PACKED.name,
      orderType = "DELIVERY",
      deliveryAddress = "House #84, Lane 2, Sector 12",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
      paymentMethod = "UPI on Delivery"
    ),
    CustomerOrder(
      orderNumber = "ORD-2039",
      customerName = "Amitabh Gupta",
      customerPhone = "+91 97118 78901",
      isVoiceOrder = false,
      rawVoiceTranscript = "Catalog order via WhatsApp Web link",
      itemsJson = """[{"name":"Aashirvaad Shudh Chakki Atta 5kg","quantity":1.0,"unit":"packet","pricePerUnit":245.0,"totalPrice":245.0,"emoji":"🌾"},{"name":"Tata Salt (Namak) 1kg","quantity":1.0,"unit":"packet","pricePerUnit":28.0,"totalPrice":28.0,"emoji":"🧂"},{"name":"Parle-G Gold Biscuits 1kg","quantity":1.0,"unit":"packet","pricePerUnit":90.0,"totalPrice":90.0,"emoji":"🍪"}]""",
      totalAmount = 363.0,
      status = OrderStatus.COMPLETED.name,
      orderType = "PICKUP",
      deliveryAddress = "Store Pickup (15 mins)",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
      paymentMethod = "Paid via WhatsApp UPI"
    )
  )
}

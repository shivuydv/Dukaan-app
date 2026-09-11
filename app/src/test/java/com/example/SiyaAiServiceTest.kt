package com.example

import com.example.data.model.CustomerOrder
import com.example.data.model.ShopItem
import com.example.data.model.ShopSettings
import com.example.data.model.SiyaCategory
import com.example.service.SiyaAiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SiyaAiServiceTest {

  private val siyaService = SiyaAiService()

  private val testCatalog = listOf(
    ShopItem(id = 1, name = "Aashirvaad Shudh Chakki Atta", hindiName = "आशीर्वाद आटा", category = "Grains", price = 340.0, unit = "5kg", emoji = "🌾", inStock = true),
    ShopItem(id = 2, name = "Amul Butter Pasteurised", hindiName = "अमूल बटर", category = "Dairy", price = 56.0, unit = "100g", emoji = "🧈", inStock = true),
    ShopItem(id = 3, name = "Maggi 2-Minute Masala Noodles", hindiName = "मैगी नूडल्स", category = "Snacks", price = 14.0, unit = "70g", emoji = "🍜", inStock = true),
    ShopItem(id = 4, name = "Wagh Bakri Premium Tea", hindiName = "वाघ बकरी चाय", category = "Beverages", price = 140.0, unit = "250g", emoji = "☕", inStock = true)
  )

  private val testSettings = ShopSettings(
    shopName = "Shree Ganesh Kirana",
    ownerName = "Ramesh Gupta",
    whatsappNumber = "+91 98765 43210",
    upiId = "ganeshkirana@upi"
  )

  @Test
  fun testStockQueryFallback() = runBlocking {
    val reply = siyaService.askSiya(
      userQuery = "Kya Amul butter aur Maggi stock me hai?",
      catalog = testCatalog,
      settings = testSettings,
      orders = emptyList()
    )

    assertNotNull(reply)
    assertEquals(SiyaCategory.STOCK_INQUIRY, reply.category)
    assertTrue(reply.replyText.contains("Stock") || reply.replyText.contains("available"))
    assertTrue(reply.matchedItems.any { it.name.contains("Butter") || it.name.contains("Maggi") })
  }

  @Test
  fun testRecipeQueryFallback() = runBlocking {
    val reply = siyaService.askSiya(
      userQuery = "Butter Maggi kaise banayein recipe batao",
      catalog = testCatalog,
      settings = testSettings,
      orders = emptyList()
    )

    assertNotNull(reply)
    assertEquals(SiyaCategory.RECIPE_ASSISTANT, reply.category)
    assertTrue(reply.replyText.contains("Butter") || reply.replyText.contains("Recipe"))
    assertTrue(reply.matchedItems.isNotEmpty())
  }

  @Test
  fun testStorePolicyFallback() = runBlocking {
    val reply = siyaService.askSiya(
      userQuery = "Dukan kab tak khuli hai aur delivery info",
      catalog = testCatalog,
      settings = testSettings,
      orders = emptyList()
    )

    assertNotNull(reply)
    assertEquals(SiyaCategory.STORE_POLICIES, reply.category)
    assertTrue(reply.replyText.contains("7:00 AM") || reply.replyText.contains("Delivery"))
  }

  @Test
  fun testVendorAnalyticsFallback() = runBlocking {
    val orders = listOf(
      CustomerOrder(id = 1, orderNumber = "ORD-101", customerName = "Sunil", customerPhone = "999", itemsJson = "[]", totalAmount = 550.0, status = "NEW", orderType = "DELIVERY", deliveryAddress = "Home")
    )

    val reply = siyaService.askSiya(
      userQuery = "Aaj ki total bikri kitni hui?",
      catalog = testCatalog,
      settings = testSettings,
      orders = orders
    )

    assertNotNull(reply)
    assertEquals(SiyaCategory.VENDOR_COPILOT, reply.category)
    assertTrue(reply.replyText.contains("550"))
  }
}

package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.local.ShopItemDao
import com.example.data.model.CustomerOrder
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.ShopItem
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class ShopRepository(
  private val shopItemDao: ShopItemDao,
  private val orderDao: OrderDao
) {
  val allItems: Flow<List<ShopItem>> = shopItemDao.getAllItems()
  val allOrders: Flow<List<CustomerOrder>> = orderDao.getAllOrders()
  val newOrdersCount: Flow<Int> = orderDao.getNewOrdersCount()
  val totalRevenue: Flow<Double?> = orderDao.getTotalRevenue()
  val allCategories: Flow<List<String>> = shopItemDao.getAllCategories()

  suspend fun getItemsSnapshot(): List<ShopItem> {
    return shopItemDao.getAllItemsSnapshot()
  }

  suspend fun ensureInitialDataSeeded() {
    try {
      if (shopItemDao.getItemCount() == 0) {
        shopItemDao.insertAll(com.example.data.local.PreloadedData.initialShopItems)
      }
      if (orderDao.getOrderCount() == 0) {
        orderDao.insertAll(com.example.data.local.PreloadedData.sampleInitialOrders)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  suspend fun resetAllDemoData() {
    try {
      shopItemDao.insertAll(com.example.data.local.PreloadedData.initialShopItems)
      orderDao.insertAll(com.example.data.local.PreloadedData.sampleInitialOrders)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  suspend fun resetCatalogDemoData() {
    try {
      shopItemDao.insertAll(com.example.data.local.PreloadedData.initialShopItems)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  suspend fun resetOrdersDemoData() {
    try {
      orderDao.insertAll(com.example.data.local.PreloadedData.sampleInitialOrders)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun searchItems(query: String): Flow<List<ShopItem>> {
    return shopItemDao.searchItems(query)
  }

  suspend fun insertItem(item: ShopItem): Long {
    return shopItemDao.insertItem(item)
  }

  suspend fun updateStock(id: Long, inStock: Boolean) {
    shopItemDao.updateStock(id, inStock)
  }

  suspend fun updatePrice(id: Long, newPrice: Double) {
    shopItemDao.updatePrice(id, newPrice)
  }

  suspend fun deleteItem(item: ShopItem) {
    shopItemDao.deleteItem(item)
  }

  suspend fun insertOrder(order: CustomerOrder): Long {
    return orderDao.insertOrder(order)
  }

  suspend fun updateOrderStatus(id: Long, status: OrderStatus) {
    orderDao.updateOrderStatus(id, status.name)
  }

  suspend fun deleteOrder(order: CustomerOrder) {
    orderDao.deleteOrder(order)
  }

  // Utility to serialize list of OrderItem to JSON
  fun serializeOrderItems(items: List<OrderItem>): String {
    val jsonArray = JSONArray()
    for (item in items) {
      val obj = JSONObject().apply {
        put("itemId", item.itemId ?: 0L)
        put("name", item.name)
        put("quantity", item.quantity)
        put("unit", item.unit)
        put("pricePerUnit", item.pricePerUnit)
        put("totalPrice", item.totalPrice)
        put("emoji", item.emoji)
      }
      jsonArray.put(obj)
    }
    return jsonArray.toString()
  }

  // Utility to deserialize JSON to list of OrderItem
  fun deserializeOrderItems(jsonStr: String): List<OrderItem> {
    if (jsonStr.isBlank()) return emptyList()
    val list = mutableListOf<OrderItem>()
    try {
      val jsonArray = JSONArray(jsonStr)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          OrderItem(
            itemId = if (obj.has("itemId")) obj.getLong("itemId") else null,
            name = obj.optString("name", "Item"),
            quantity = obj.optDouble("quantity", 1.0),
            unit = obj.optString("unit", "packet"),
            pricePerUnit = obj.optDouble("pricePerUnit", 0.0),
            totalPrice = obj.optDouble("totalPrice", 0.0),
            emoji = obj.optString("emoji", "🛒")
          )
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return list
  }
}

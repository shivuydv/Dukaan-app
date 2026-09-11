package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val title: String, val hindiTitle: String) {
  NEW("New Order", "नया ऑर्डर"),
  ACCEPTED("Accepted", "स्वीकार किया"),
  PACKED("Packed", "पैक हो गया"),
  OUT_FOR_DELIVERY("Out for Delivery", "रास्ते में है"),
  COMPLETED("Completed", "डिलीवर हो गया"),
  REJECTED("Cancelled", "रद्द किया")
}

@Entity(tableName = "customer_orders")
data class CustomerOrder(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val orderNumber: String,
  val customerName: String,
  val customerPhone: String,
  val isVoiceOrder: Boolean = true,
  val rawVoiceTranscript: String = "",
  val itemsJson: String = "[]", // Serialized list of OrderItem
  val totalAmount: Double = 0.0,
  val status: String = OrderStatus.NEW.name,
  val orderType: String = "DELIVERY", // "DELIVERY" or "PICKUP"
  val deliveryAddress: String = "Flat 204, Sunrise Heights",
  val timestamp: Long = System.currentTimeMillis(),
  val paymentMethod: String = "Cash on Delivery (COD)"
)

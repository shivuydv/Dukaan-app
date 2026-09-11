package com.example.data.model

data class OrderItem(
  val itemId: Long? = null,
  val name: String,
  val quantity: Double,
  val unit: String,
  val pricePerUnit: Double,
  val totalPrice: Double,
  val emoji: String = "🛒"
)

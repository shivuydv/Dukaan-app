package com.example.data.model

data class ShopSettings(
  val shopName: String = "Shree Ganesh Kirana Store",
  val ownerName: String = "Rajesh Gupta",
  val whatsappNumber: String = "+91 98765 43210",
  val upiId: String = "shreeganesh@upi",
  val shopAddress: String = "Shop #4, Main Market, Sector 12",
  val deliveryRadiusKm: Double = 3.5,
  val minOrderAmount: Double = 100.0,
  val voiceAssistantEnabled: Boolean = true,
  val autoConfirmVoiceOrders: Boolean = false
)

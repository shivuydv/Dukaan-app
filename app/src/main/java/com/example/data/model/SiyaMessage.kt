package com.example.data.model

data class SiyaMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val isUser: Boolean,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val recommendedItems: List<ShopItem> = emptyList(),
  val suggestedPrompts: List<String> = emptyList(),
  val isVoice: Boolean = false,
  val actionCategory: SiyaCategory = SiyaCategory.GENERAL
)

enum class SiyaCategory {
  GENERAL,
  STOCK_INQUIRY,
  RECIPE_ASSISTANT,
  STORE_POLICIES,
  VENDOR_COPILOT,
  MARKETING_DRAFT
}

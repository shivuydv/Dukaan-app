package com.example.data.model

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: MessageSender,
  val text: String,
  val isVoiceNote: Boolean = false,
  val voiceDurationSec: Int = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val orderReferenceId: Long? = null,
  val quickActions: List<String> = emptyList()
)

enum class MessageSender {
  CUSTOMER,
  STORE_BOT,
  SYSTEM
}

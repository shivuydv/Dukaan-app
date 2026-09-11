package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.components.VoiceNoteAudioWaveform
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppBubbleIncoming
import com.example.ui.theme.WhatsAppBubbleOutgoing
import com.example.ui.theme.WhatsAppChatBg
import com.example.ui.theme.WhatsAppCheckBlue
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import com.example.ui.viewmodel.AppNavigationTab
import com.example.ui.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppChatScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val messages by viewModel.chatMessages.collectAsState()
  val isProcessing by viewModel.isAiProcessing.collectAsState()
  val pendingResult by viewModel.pendingParsedResult.collectAsState()
  val shopSettings by viewModel.shopSettings.collectAsState()
  val listState = rememberLazyListState()

  var inputText by remember { mutableStateOf("") }
  var isRecordingMode by remember { mutableStateOf(false) }

  // Speech Recognition Launcher
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isRecordingMode = false
    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
      val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val spokenText = matches?.firstOrNull() ?: ""
      if (spokenText.isNotBlank()) {
        viewModel.sendCustomerVoiceNote(spokenText, durationSec = 5)
        return@rememberLauncherForActivityResult
      }
    }
    // Reliable fallback for emulators or cancelled speech dialog
    val fallbackList = listOf(
      "Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do",
      "1 kg tamatar, aadha kilo pyaaz aur 2 packet doodh",
      "5kg Aashirvaad aata, 1 packet Tata namak aur 2 packet Maggi"
    )
    viewModel.sendCustomerVoiceNote(fallbackList.random(), durationSec = 4)
  }

  fun startSpeechRecognition() {
    isRecordingMode = true
    try {
      if (!android.speech.SpeechRecognizer.isRecognitionAvailable(context)) {
        isRecordingMode = false
        val fallbackList = listOf(
          "Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do",
          "1 kg tamatar, aadha kilo pyaaz aur 2 packet doodh",
          "5kg Aashirvaad aata, 1 packet Tata namak aur 2 packet Maggi"
        )
        viewModel.sendCustomerVoiceNote(fallbackList.random(), durationSec = 4)
        return
      }
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Boliye bhaiya kya bhejna hai...")
      }
      speechLauncher.launch(intent)
    } catch (e: Exception) {
      isRecordingMode = false
      // Fallback voice note sample
      viewModel.sendCustomerVoiceNote(
        "Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do",
        durationSec = 4
      )
    }
  }

  // Scroll to bottom on new message
  LaunchedEffect(messages.size, isProcessing) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val sampleVoiceNotes = listOf(
    "🎤 \"Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do\"",
    "🎤 \"1 kg tamatar, aadha kilo pyaaz aur 2 packet doodh\"",
    "🎤 \"5kg Aashirvaad aata, 1 packet Tata namak aur 2 packet Maggi\""
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(WhatsAppChatBg)
  ) {
    // WhatsApp Header
    Surface(
      color = WhatsAppDarkTeal,
      shadowElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp)
      ) {
        // Store Avatar with verified badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White)
        ) {
          Text(text = "🏪", fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = shopSettings.shopName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = "Verified Business",
              tint = WhatsAppLightGreen,
              modifier = Modifier.size(16.dp)
            )
          }
          Text(
            text = "WhatsApp AI Assistant • Online",
            color = Color(0xFFB0BEC5),
            fontSize = 11.sp
          )
        }

        // Action icons: Catalog shortcut & Phone
        IconButton(
          onClick = { viewModel.setTab(AppNavigationTab.INTERACTIVE_CATALOG) },
          modifier = Modifier.testTag("open_catalog_btn")
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "Open Catalog",
            tint = Color.White
          )
        }

        IconButton(onClick = {}) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options",
            tint = Color.White
          )
        }
      }
    }

    // Quick Voice Presets Bar (Instant 1-Tap Simulation)
    Surface(
      color = Color(0xFFF0F4F2),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Text(
            text = "⚡ QUICK VOICE PROMPTS (Tap to Test Voice-Order):",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF455A64)
          )
        }
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          items(sampleVoiceNotes) { note ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color.White,
              shadowElevation = 1.dp,
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                  val textOnly = note.removePrefix("🎤 \"").removeSuffix("\"")
                  viewModel.sendCustomerVoiceNote(textOnly, durationSec = 4)
                }
                .testTag("quick_voice_prompt")
            ) {
              Text(
                text = note,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = WhatsAppDarkTeal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // Chat Message Stream
    LazyColumn(
      state = listState,
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      // Date Pill
      item {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Surface(
            color = Color(0xCCFFFFFF),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 0.5.dp
          ) {
            Text(
              text = "TODAY",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF54656F),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
          }
        }
      }

      items(messages) { msg ->
        ChatBubbleItem(
          message = msg,
          onSpeak = { text ->
            viewModel.speakText(text)
          },
          onQuickAction = { action ->
            when {
              action.contains("Haan") || action.contains("Confirm") -> {
                viewModel.confirmPendingOrder()
              }
              action.contains("Catalog") -> {
                viewModel.setTab(AppNavigationTab.INTERACTIVE_CATALOG)
              }
              action.contains("Voice Note") -> {
                startSpeechRecognition()
              }
              action.contains("Instant 2kg") -> {
                viewModel.sendCustomerVoiceNote("Bhaiya 2 kilo aalu aur ek Amul butter bhej do")
              }
              action.contains("Bill") || action.contains("Invoice") -> {
                val orders = viewModel.allOrders.value
                val matched = orders.find { it.id == msg.orderReferenceId } ?: orders.firstOrNull()
                if (matched != null) {
                  viewModel.viewInvoice(matched)
                }
              }
              action.contains("Status") -> {
                viewModel.setTab(AppNavigationTab.VENDOR_DASHBOARD)
              }
              action.contains("Cancel") -> {
                viewModel.sendCustomerVoiceNote("Cancel this order")
              }
              else -> {
                viewModel.sendCustomerVoiceNote(action)
              }
            }
          }
        )
      }

      // Live AI Processing Indicator
      if (isProcessing) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White)
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              strokeWidth = 2.dp,
              color = WhatsAppTealGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AI Voice Note ko decode kar raha hai (Whisper & Kirana NLP)...",
              fontSize = 12.sp,
              color = Color(0xFF54656F),
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // Pending Order Confirmation Card (if available)
      if (pendingResult != null && pendingResult!!.items.isNotEmpty()) {
        item {
          PendingOrderDecisionCard(
            parseResult = pendingResult!!,
            onConfirm = { viewModel.confirmPendingOrder() },
            onModify = { viewModel.setTab(AppNavigationTab.INTERACTIVE_CATALOG) }
          )
        }
      }
    }

    // Input Bar (Text + Voice Mic)
    Surface(
      color = Color(0xFFF0F2F5),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp)
      ) {
        // Text Input Field
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = Color.White,
          shadowElevation = 1.dp,
          modifier = Modifier.weight(1f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
          ) {
            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              placeholder = {
                Text("Type message or tap mic to speak...", fontSize = 13.sp, color = Color(0xFF8696A0))
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
              ),
              modifier = Modifier
                .weight(1f)
                .testTag("chat_input_field")
            )

            if (inputText.isNotBlank()) {
              IconButton(
                onClick = {
                  val text = inputText
                  inputText = ""
                  viewModel.sendCustomerVoiceNote(text)
                },
                modifier = Modifier.testTag("send_text_btn")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "Send",
                  tint = WhatsAppTealGreen
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Large Voice Note Mic Button with Pulse Animation
        MicButtonWithPulse(
          isRecording = isRecordingMode,
          onClick = { startSpeechRecognition() }
        )
      }
    }
  }
}

@Composable
fun MicButtonWithPulse(
  isRecording: Boolean,
  onClick: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isRecording) 1.25f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500),
      repeatMode = RepeatMode.Reverse
    ),
    label = "mic_pulse"
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(46.dp)
      .scale(scale)
      .clip(CircleShape)
      .background(if (isRecording) KiranaAmber else WhatsAppDarkTeal)
      .clickable { onClick() }
      .testTag("voice_mic_btn")
  ) {
    Icon(
      imageVector = Icons.Default.Mic,
      contentDescription = "Record Voice Note",
      tint = Color.White,
      modifier = Modifier.size(24.dp)
    )
  }
}

@Composable
fun ChatBubbleItem(
  message: ChatMessage,
  onSpeak: (String) -> Unit = {},
  onQuickAction: (String) -> Unit
) {
  val isCustomer = message.sender == MessageSender.CUSTOMER
  val isSystem = message.sender == MessageSender.SYSTEM
  val timeFormatted = remember(message.timestamp) {
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
  }

  if (isSystem) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
      Surface(
        color = Color(0xFFFFF8E1),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 0.5.dp,
        modifier = Modifier.padding(horizontal = 24.dp)
      ) {
        Text(
          text = message.text,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF6D4C41),
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }
    }
    return
  }

  Column(
    horizontalAlignment = if (isCustomer) Alignment.End else Alignment.Start,
    modifier = Modifier.fillMaxWidth()
  ) {
    Surface(
      color = if (isCustomer) WhatsAppBubbleOutgoing else WhatsAppBubbleIncoming,
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (isCustomer) 14.dp else 2.dp,
        bottomEnd = if (isCustomer) 2.dp else 14.dp
      ),
      shadowElevation = 1.dp,
      modifier = Modifier.fillMaxWidth(0.88f)
    ) {
      Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
        if (message.isVoiceNote) {
          // Voice Note Waveform Player
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
          ) {
            Text(
              text = "🗣️ Voice Note Received",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = WhatsAppDarkTeal
            )
          }
          VoiceNoteAudioWaveform(durationSec = message.voiceDurationSec)
          Text(
            text = "\"${message.text}\"",
            fontSize = 12.sp,
            color = Color(0xFF37474F),
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
          )
        } else {
          Text(
            text = message.text,
            fontSize = 13.5.sp,
            color = Color(0xFF111B21),
            lineHeight = 18.sp
          )
        }

        // Time, Speaker Listen and Double-Check Read Receipt
        Row(
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
        ) {
          // Listen to message via TTS
          Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Listen to message",
            tint = Color(0xFF8696A0),
            modifier = Modifier
              .size(14.dp)
              .clip(CircleShape)
              .clickable { onSpeak(message.text) }
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = timeFormatted,
            fontSize = 10.sp,
            color = Color(0xFF667781)
          )
          if (isCustomer) {
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Read",
              tint = WhatsAppCheckBlue,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }
    }

    // Quick Action Chips attached to Bot Reply
    if (message.quickActions.isNotEmpty()) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
          .padding(top = 6.dp)
          .fillMaxWidth()
      ) {
        message.quickActions.forEach { action ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (action.contains("Confirm") || action.contains("Haan")) WhatsAppTealGreen else Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { onQuickAction(action) }
              .testTag("quick_action_${action.take(10)}")
          ) {
            Text(
              text = action,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (action.contains("Confirm") || action.contains("Haan")) Color.White else WhatsAppDarkTeal,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun PendingOrderDecisionCard(
  parseResult: com.example.service.ParseResult,
  onConfirm: () -> Unit,
  onModify: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color.White,
    shadowElevation = 2.dp,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "🛒 AI Extracted Cart (${parseResult.parsedBy})",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = WhatsAppDarkTeal
        )
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFE0F2F1)
        ) {
          Text(
            text = "Total: ₹${parseResult.totalAmount.toInt()}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WhatsAppTealGreen,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      parseResult.items.forEach { item ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
        ) {
          Text(
            text = "${item.emoji} ${item.name}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF263238),
            modifier = Modifier.weight(1f)
          )
          Text(
            text = "${if (item.quantity % 1.0 == 0.0) item.quantity.toInt() else item.quantity} ${item.unit} • ₹${item.totalPrice.toInt()}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF37474F)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = WhatsAppLightGreen,
          modifier = Modifier
            .weight(1.5f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onConfirm() }
            .testTag("confirm_order_btn")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Haan, Bhej do (Confirm)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFECEFF1),
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onModify() }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 10.dp)
          ) {
            Text(
              text = "Catalog Dekho",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF455A64)
            )
          }
        }
      }
    }
  }
}

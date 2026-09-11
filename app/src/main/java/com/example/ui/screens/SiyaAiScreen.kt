package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShopItem
import com.example.data.model.SiyaCategory
import com.example.data.model.SiyaMessage
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import com.example.ui.viewmodel.AppNavigationTab
import com.example.ui.viewmodel.ShopViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiyaAiScreen(viewModel: ShopViewModel) {
  val messages by viewModel.siyaMessages.collectAsState()
  val isThinking by viewModel.isSiyaThinking.collectAsState()
  val voiceOutputEnabled by viewModel.siyaVoiceOutputEnabled.collectAsState()
  val shopSettings by viewModel.shopSettings.collectAsState()
  val catalogCart by viewModel.catalogCart.collectAsState()

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  // Auto scroll to bottom when new messages arrive
  LaunchedEffect(messages.size, isThinking) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF0F4F4))
      .imePadding()
  ) {
    // Top Bar with Siya Avatar, 24/7 Status, and Sound Controls
    Surface(
      color = WhatsAppDarkTeal,
      shadowElevation = 4.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Siya Avatar with glowing pulsing halo
          val transition = rememberInfiniteTransition(label = "pulse")
          val pulseScale by transition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
              animation = tween(1200, easing = FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
          )

          Box(
            modifier = Modifier
              .size(46.dp)
              .scale(pulseScale)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), WhatsAppTealGreen)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "🤖",
              fontSize = 24.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "सिया AI Agent",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0x33FFFFFF),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(
                  text = "24/7 Active",
                  fontSize = 10.sp,
                  color = WhatsAppLightGreen,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "${shopSettings.shopName} • Smart Kirana Assistant",
              fontSize = 11.sp,
              color = Color(0xFFB2DFDB),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Actions: Toggle TTS Voice & Reset
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.toggleSiyaVoiceOutput() },
            modifier = Modifier.testTag("siya_voice_toggle")
          ) {
            Icon(
              imageVector = if (voiceOutputEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
              contentDescription = "Toggle Siya Voice Response",
              tint = if (voiceOutputEnabled) WhatsAppLightGreen else Color.LightGray
            )
          }

          IconButton(
            onClick = { viewModel.clearSiyaHistory() },
            modifier = Modifier.testTag("siya_clear_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Clear Chat",
              tint = Color.White
            )
          }
        }
      }
    }

    // Fast Prompt Category Chips Row
    val quickPrompts = listOf(
      "📦 Check Aalu & Pyaaz Stock" to "Kya Aalu aur Pyaaz stock me hain aur unka rate kya hai?",
      "🍜 Butter Maggi Recipe" to "Butter Tadka Maggi ki recipe aur uske ingredients dukan me hain?",
      "☕ Khadak Chai" to "Masala Chai kaise banayein aur kya chai patti aur doodh mil jayega?",
      "🕒 Dukan Timings" to "Dukan kab tak khuli hai aur home delivery kab tak hoti hai?",
      "📊 Aaj Ki Bikri" to "Vendor Report: Aaj kitni bikri hui aur kitne orders aaye?",
      "🎉 Weekend Offer Draft" to "Weekend Maha Bachat discount ka WhatsApp message draft karo",
      "📝 Udhaar Reminder" to "Customer ke liye polite WhatsApp udhaar payment reminder draft karo"
    )

    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFE0ECEB))
        .padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(quickPrompts) { (label, prompt) ->
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          shadowElevation = 1.dp,
          modifier = Modifier
            .clickable {
              viewModel.sendSiyaQuery(prompt, isVoice = false)
            }
            .testTag("quick_prompt_$label")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = WhatsAppDarkTeal
            )
          }
        }
      }
    }

    // Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        SiyaChatBubble(
          message = msg,
          cart = catalogCart,
          onAddToCart = { item -> viewModel.addToCart(item.id) },
          onAddAllToCart = { items -> viewModel.addAllSiyaItemsToCart(items) },
          onPromptClick = { prompt -> viewModel.sendSiyaQuery(prompt, isVoice = false) },
          onSpeak = { text ->
            val clean = text.replace("*", "").replace("#", "")
            viewModel.speakText(clean)
          }
        )
      }

      if (isThinking) {
        item {
          SiyaThinkingBubble()
        }
      }
    }

    // Bottom Input Bar with Voice & Text
    Surface(
      color = Color.White,
      shadowElevation = 8.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mic Quick Trigger
          Surface(
            shape = CircleShape,
            color = Color(0xFFFFF3E0),
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .clickable {
                // Pre-selected voice query simulation
                val voiceSamples = listOf(
                  "Bhaiya kya Amul milk aur Desi Tamatar available hai?",
                  "Siya, aaj shaam ke liye koi tasty snack recipe batao store ke saman ke saath",
                  "Aaj total kitne order aaye hain aur kitna munafa hua?",
                  "WhatsApp par 10 percent discount ka poster message likho"
                )
                val selected = voiceSamples.random()
                viewModel.sendSiyaQuery(selected, isVoice = true)
              }
              .testTag("siya_mic_btn")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Speak to Siya",
                tint = KiranaAmber,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Text Field
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = {
              Text(
                text = "Poochiye Siya se kuch bhi (24/7)...",
                fontSize = 13.sp,
                color = Color.Gray
              )
            },
            modifier = Modifier
              .weight(1f)
              .testTag("siya_input_field"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = WhatsAppTealGreen,
              unfocusedBorderColor = Color(0xFFCFD8DC),
              focusedContainerColor = Color(0xFFF7FBFB),
              unfocusedContainerColor = Color(0xFFF7FBFB)
            ),
            maxLines = 3
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Send Button
          Surface(
            shape = CircleShape,
            color = if (inputText.isNotBlank()) WhatsAppTealGreen else Color(0xFFCFD8DC),
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .clickable(enabled = inputText.isNotBlank()) {
                val query = inputText
                inputText = ""
                viewModel.sendSiyaQuery(query, isVoice = false)
              }
              .testTag("siya_send_btn")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SiyaChatBubble(
  message: SiyaMessage,
  cart: Map<Long, Int>,
  onAddToCart: (ShopItem) -> Unit,
  onAddAllToCart: (List<ShopItem>) -> Unit,
  onPromptClick: (String) -> Unit,
  onSpeak: (String) -> Unit
) {
  val timeFormatted = remember(message.timestamp) {
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
  }

  if (message.isUser) {
    // User message bubble (right side)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      Card(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCF8C6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.widthIn(max = 300.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          if (message.isVoice) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(bottom = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = KiranaAmber,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Voice Query",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = KiranaAmber
              )
            }
          }
          Text(
            text = message.text,
            fontSize = 14.sp,
            color = Color(0xFF111B21),
            lineHeight = 19.sp
          )
          Text(
            text = timeFormatted,
            fontSize = 10.sp,
            color = Color(0xFF667781),
            modifier = Modifier
              .align(Alignment.End)
              .padding(top = 4.dp)
          )
        }
      }
    }
  } else {
    // Siya AI message bubble (left side)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Start
    ) {
      // Small avatar on left
      Box(
        modifier = Modifier
          .padding(top = 4.dp)
          .size(32.dp)
          .clip(CircleShape)
          .background(WhatsAppDarkTeal),
        contentAlignment = Alignment.Center
      ) {
        Text("🤖", fontSize = 16.sp)
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.widthIn(max = 330.dp)) {
        Card(
          shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            // Header with Category Chip & Speak Button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "सिया (Siya AI)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = WhatsAppTealGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                CategoryBadge(message.actionCategory)
              }

              IconButton(
                onClick = { onSpeak(message.text) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                  contentDescription = "Listen Aloud",
                  tint = WhatsAppTealGreen,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Answer Text
            Text(
              text = message.text,
              fontSize = 13.5.sp,
              color = Color(0xFF111B21),
              lineHeight = 19.sp
            )

            // Recommended Items in store
            if (message.recommendedItems.isNotEmpty()) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🛒 Store Items (${message.recommendedItems.size}):",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = KiranaAmber
                )
                Text(
                  text = "Add All",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = WhatsAppDarkTeal,
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE0F2F1))
                    .clickable { onAddAllToCart(message.recommendedItems) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                message.recommendedItems.forEach { item ->
                  val inCartCount = cart[item.id] ?: 0
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF6F8F7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                      ) {
                        Text(item.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                          Text(
                            text = item.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                          Text(
                            text = "₹${item.price.toInt()} / ${item.unit}",
                            fontSize = 11.sp,
                            color = WhatsAppDarkTeal,
                            fontWeight = FontWeight.SemiBold
                          )
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (inCartCount > 0) Color(0xFFE8F5E9) else WhatsAppTealGreen,
                        modifier = Modifier
                          .clip(RoundedCornerShape(8.dp))
                          .clickable { onAddToCart(item) }
                          .padding(horizontal = 8.dp, vertical = 4.dp)
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          if (inCartCount > 0) {
                            Icon(
                              imageVector = Icons.Default.Check,
                              contentDescription = null,
                              tint = Color(0xFF2E7D32),
                              modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                              text = "In Cart ($inCartCount)",
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF2E7D32)
                            )
                          } else {
                            Icon(
                              imageVector = Icons.Default.AddShoppingCart,
                              contentDescription = null,
                              tint = Color.White,
                              modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                              text = "+ Add",
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold,
                              color = Color.White
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            Text(
              text = timeFormatted,
              fontSize = 10.sp,
              color = Color(0xFF8696A0),
              modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
            )
          }
        }

        // Suggested Follow-Up Prompts Chips
        if (message.suggestedPrompts.isNotEmpty()) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(message.suggestedPrompts) { prompt ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Color.White,
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB2DFDB)),
                  modifier = Modifier
                    .clickable { onPromptClick(prompt) }
                    .padding(vertical = 2.dp)
                ) {
                  Text(
                    text = prompt,
                    fontSize = 11.sp,
                    color = WhatsAppDarkTeal,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CategoryBadge(category: SiyaCategory) {
  val (label, bg, fg) = when (category) {
    SiyaCategory.STOCK_INQUIRY -> Triple("Stock", Color(0xFFE8F5E9), Color(0xFF2E7D32))
    SiyaCategory.RECIPE_ASSISTANT -> Triple("Recipe", Color(0xFFFFF3E0), Color(0xFFE65100))
    SiyaCategory.STORE_POLICIES -> Triple("Info", Color(0xFFE3F2FD), Color(0xFF1565C0))
    SiyaCategory.VENDOR_COPILOT -> Triple("Analytics", Color(0xFFEDE7F6), Color(0xFF512DA8))
    SiyaCategory.MARKETING_DRAFT -> Triple("Marketing", Color(0xFFFCE4EC), Color(0xFFC2185B))
    SiyaCategory.GENERAL -> Triple("24/7 AI", Color(0xFFE0F2F1), WhatsAppDarkTeal)
  }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = bg
  ) {
    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = fg,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}

@Composable
fun SiyaThinkingBubble() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(WhatsAppDarkTeal),
      contentAlignment = Alignment.Center
    ) {
      Text("🤖", fontSize = 16.sp)
    }

    Spacer(modifier = Modifier.width(8.dp))

    Card(
      shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(16.dp),
          color = WhatsAppTealGreen,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Siya soch rahi hai aur catalog check kar rahi hai... ✨",
          fontSize = 12.sp,
          color = Color.Gray,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
      }
    }
  }
}

package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen

@Composable
fun OrderStatusBadge(status: String, modifier: Modifier = Modifier) {
  val (bgColor, textColor, label) = when (status) {
    OrderStatus.NEW.name -> Triple(Color(0xFFFFF3E0), KiranaAmber, "Naya Order")
    OrderStatus.ACCEPTED.name -> Triple(Color(0xFFE0F2F1), WhatsAppTealGreen, "Accepted")
    OrderStatus.PACKED.name -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Packed")
    OrderStatus.OUT_FOR_DELIVERY.name -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "On the Way")
    OrderStatus.COMPLETED.name -> Triple(Color(0xFFE8F5E9), Color(0xFF1B5E20), "Delivered")
    OrderStatus.REJECTED.name -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Cancelled")
    else -> Triple(Color(0xFFEEEEEE), Color.DarkGray, status)
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(12.dp),
    modifier = modifier
  ) {
    Text(
      text = label,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}

@Composable
fun VoiceNoteAudioWaveform(
  durationSec: Int = 4,
  isPlaying: Boolean = false,
  onPlayClick: () -> Unit = {}
) {
  val infiniteTransition = rememberInfiniteTransition(label = "wave")
  val waveAnim by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave_scale"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    // Play button
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(WhatsAppDarkTeal)
        .clickable { onPlayClick() }
        .testTag("play_voice_note_btn")
    ) {
      Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Play voice note",
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }

    // Audio Bars
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(3.dp),
      modifier = Modifier.weight(1f)
    ) {
      val barHeights = listOf(14, 22, 10, 26, 18, 24, 12, 28, 16, 20, 24, 12, 18, 22, 14, 20)
      barHeights.forEachIndexed { index, baseHeight ->
        val heightMultiplier = if (isPlaying) {
          if (index % 2 == 0) waveAnim else (1.3f - waveAnim)
        } else 0.8f

        Box(
          modifier = Modifier
            .width(3.dp)
            .height((baseHeight * heightMultiplier).dp.coerceIn(4.dp, 30.dp))
            .clip(RoundedCornerShape(2.dp))
            .background(if (isPlaying) WhatsAppTealGreen else Color(0xFF90A4AE))
        )
      }
    }

    // Duration label & Mic icon
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "0:0$durationSec",
        fontSize = 11.sp,
        color = Color(0xFF667781),
        fontWeight = FontWeight.Medium
      )
      Icon(
        imageVector = Icons.Default.Mic,
        contentDescription = "Voice note icon",
        tint = WhatsAppTealGreen,
        modifier = Modifier
          .size(16.dp)
          .padding(start = 2.dp)
      )
    }
  }
}

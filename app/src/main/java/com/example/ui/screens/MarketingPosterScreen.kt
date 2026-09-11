package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun MarketingPosterScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val settings by viewModel.shopSettings.collectAsState()
  var customShopName by remember { mutableStateOf(settings.shopName) }
  var customPhone by remember { mutableStateOf(settings.whatsappNumber) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFECEFF1))
      .verticalScroll(rememberScrollState())
  ) {
    // Header
    Surface(
      color = WhatsAppDarkTeal,
      shadowElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Text(
          text = "Counter Poster & QR Marketing",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Dukaan ke counter par lagane ke liye WhatsApp Voice Poster",
          fontSize = 12.sp,
          color = Color(0xFFB0BEC5)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Printable Standee Poster Preview Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, WhatsAppDarkTeal, RoundedCornerShape(16.dp))
          .testTag("counter_standee_card")
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          // Poster Top Header
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = WhatsAppDarkTeal,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
            ) {
              Text(
                text = "⚡ AB LINE ME MAT KHADE RAHO!",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD54F),
                letterSpacing = 1.sp
              )
              Text(
                text = customShopName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Headline in Hindi/Hinglish
          Text(
            text = "Ghar Baithe WhatsApp par\nVoice Message Bhejo aur Samaan Paao! 🛵",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1B5E20),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // QR Code Simulator
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
              .size(170.dp)
              .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(12.dp))
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.padding(10.dp)
            ) {
              DrawSimulatedQrCode()
              // Centered WhatsApp Mic Logo inside QR
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(WhatsAppTealGreen)
                  .border(2.dp, Color.White, CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Scan karke WhatsApp par Voice Note bolein:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF455A64)
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE8F5E9),
            modifier = Modifier.padding(top = 4.dp)
          ) {
            Text(
              text = "\"Bhaiya, 2 kilo aalu aur Amul butter bhej do\"",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = WhatsAppDarkTeal,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // WhatsApp Contact Pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFFE0F2F1))
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(text = "💬 WhatsApp: ", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
              text = customPhone,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = WhatsAppDarkTeal
            )
          }
        }
      }

      // Share & Print Actions
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = {
            val shareText = "Ab ghar baithe WhatsApp par order karein! Scan karein ya message bhejein $customShopName ko: wa.me/${customPhone.filter { it.isDigit() }}"
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share WhatsApp Poster"))
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTealGreen),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f).testTag("share_poster_btn")
        ) {
          Icon(imageVector = Icons.Default.Share, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share Poster")
        }

        OutlinedButton(
          onClick = {
            val shareText = "https://wa.me/${customPhone.filter { it.isDigit() }}"
            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("WhatsApp Link", shareText)
            clipboard.setPrimaryClip(clip)
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy Link")
        }
      }

      // Micro-SaaS Business Model Card
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = KiranaAmber,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Micro-SaaS Vendor Plan (Active)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF263238)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Growth Plan: ₹999/month (14 Days Free Trial Active)",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = WhatsAppDarkTeal
          )
          Text(
            text = "• AI Voice-Ordering Assistant (Unlimited Orders)\n• Interactive WhatsApp Web Catalog\n• Super-Simple Shopkeeper Dashboard\n• Hyperlocal Foot-on-Street QR Support",
            fontSize = 12.sp,
            color = Color(0xFF54656F),
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp)
          )

          HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color(0xFFECEFF1)
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Refer 1 Dukaan = 1 Month FREE!",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = KiranaAmber
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFFECB3)
            ) {
              Text(
                text = "CODE: DUKAANFREE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun DrawSimulatedQrCode() {
  Canvas(modifier = Modifier.size(150.dp)) {
    val canvasSize = size.width
    val cellSize = canvasSize / 21f

    // Background white
    drawRect(color = Color.White, size = size)

    // Corner position markers (Top-left, Top-right, Bottom-left)
    fun drawCornerSquare(x: Float, y: Float) {
      // Outer 7x7
      drawRect(
        color = Color(0xFF111B21),
        topLeft = Offset(x * cellSize, y * cellSize),
        size = Size(7 * cellSize, 7 * cellSize)
      )
      // Inner 5x5 white
      drawRect(
        color = Color.White,
        topLeft = Offset((x + 1) * cellSize, (y + 1) * cellSize),
        size = Size(5 * cellSize, 5 * cellSize)
      )
      // Core 3x3 black
      drawRect(
        color = Color(0xFF111B21),
        topLeft = Offset((x + 2) * cellSize, (y + 2) * cellSize),
        size = Size(3 * cellSize, 3 * cellSize)
      )
    }

    drawCornerSquare(0f, 0f)
    drawCornerSquare(14f, 0f)
    drawCornerSquare(0f, 14f)

    // Pseudo-random data modules
    val pattern = listOf(
      0b10101011, 0b01010101, 0b11001100, 0b00110011,
      0b10011001, 0b01100110, 0b11100011, 0b00011100
    )

    for (row in 8..12) {
      for (col in 0..20) {
        if (col in 8..12) continue // center hole for logo
        if ((row * 7 + col * 13) % 2 == 0) {
          drawRect(
            color = Color(0xFF111B21),
            topLeft = Offset(col * cellSize, row * cellSize),
            size = Size(cellSize, cellSize)
          )
        }
      }
    }

    for (row in 0..7) {
      for (col in 8..13) {
        if ((row * 11 + col * 5) % 2 == 0) {
          drawRect(
            color = Color(0xFF111B21),
            topLeft = Offset(col * cellSize, row * cellSize),
            size = Size(cellSize, cellSize)
          )
        }
      }
    }

    for (row in 14..20) {
      for (col in 8..20) {
        if ((row * 3 + col * 7) % 2 == 0) {
          drawRect(
            color = Color(0xFF111B21),
            topLeft = Offset(col * cellSize, row * cellSize),
            size = Size(cellSize, cellSize)
          )
        }
      }
    }
  }
}

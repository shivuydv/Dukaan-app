package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerOrder
import com.example.data.model.OrderStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.VoiceNoteAudioWaveform
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import com.example.ui.viewmodel.AppNavigationTab
import com.example.ui.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VendorDashboardScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val orders by viewModel.allOrders.collectAsState()
  val newOrdersCount by viewModel.newOrdersCount.collectAsState()
  val totalRevenue by viewModel.totalRevenue.collectAsState()
  val settings by viewModel.shopSettings.collectAsState()
  val activeFilter by viewModel.orderFilter.collectAsState()
  val recentAlert by viewModel.recentVendorNotification.collectAsState()

  var isStoreOpen by remember { mutableStateOf(true) }

  val filteredOrders = remember(orders, activeFilter) {
    if (activeFilter == null || activeFilter == "ALL") {
      orders
    } else {
      orders.filter { it.status == activeFilter }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF4F6F5))
  ) {
    // Vendor Top Bar
    Surface(
      color = WhatsAppDarkTeal,
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = WhatsAppLightGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = settings.shopName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
            Text(
              text = "Shopkeeper: ${settings.ownerName} • Main Market",
              fontSize = 12.sp,
              color = Color(0xFFB0BEC5)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Soundbox Quick Test
            IconButton(
              onClick = {
                viewModel.speakText("दुकान वॉइस साउंडबॉक्स चालू है! नया आर्डर आने पर यहाँ आवाज़ आएगी.")
              },
              modifier = Modifier.size(36.dp).testTag("soundbox_test_btn")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Test Soundbox Voice",
                tint = WhatsAppLightGreen,
                modifier = Modifier.size(20.dp)
              )
            }

            // Settings
            IconButton(
              onClick = { viewModel.openSettings() },
              modifier = Modifier.size(36.dp).testTag("vendor_settings_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Shop Settings",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Open/Close Store Switch
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x33FFFFFF))
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (isStoreOpen) "Khuli Hai" else "Band",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStoreOpen) WhatsAppLightGreen else Color.LightGray
              )
              Spacer(modifier = Modifier.width(4.dp))
              Switch(
                checked = isStoreOpen,
                onCheckedChange = { isStoreOpen = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = WhatsAppLightGreen,
                  checkedTrackColor = Color(0x66FFFFFF)
                ),
                modifier = Modifier.size(36.dp)
              )
            }
          }
        }
      }
    }

    // Live Alert Banner (when customer sends voice order)
    AnimatedVisibility(visible = recentAlert != null) {
      Surface(
        color = KiranaAmber,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.dismissNotification() }
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = recentAlert ?: "",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Analytics Summary Cards (Today's Sale, Total Orders, AI Voice %)
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      // Today Sales
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.weight(1.2f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "TODAY'S SALES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF78909C)
          )
          Text(
            text = "₹${totalRevenue?.toInt() ?: 0}",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WhatsAppDarkTeal,
            modifier = Modifier.padding(vertical = 2.dp)
          )
          Text(
            text = "Cash & UPI ready",
            fontSize = 10.sp,
            color = Color(0xFF43A047),
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Orders Count
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "ORDERS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF78909C)
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${orders.size}",
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF263238)
            )
            if (newOrdersCount > 0) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = KiranaAmber
              ) {
                Text(
                  text = "+$newOrdersCount NEW",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }
          }
          Text(
            text = "Active today",
            fontSize = 10.sp,
            color = Color(0xFF78909C)
          )
        }
      }

      // Voice Orders USP Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE0F2F1),
        shadowElevation = 1.dp,
        modifier = Modifier.weight(1.1f)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = null,
              tint = WhatsAppTealGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "VOICE AI",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = WhatsAppTealGreen
            )
          }
          val voiceCount = orders.count { it.isVoiceOrder }
          val percentage = if (orders.isNotEmpty()) (voiceCount * 100) / orders.size else 80
          Text(
            text = "$percentage%",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WhatsAppDarkTeal
          )
          Text(
            text = "WhatsApp Voice",
            fontSize = 10.sp,
            color = WhatsAppTealGreen,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Siya 24/7 AI Kirana Copilot Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 4.dp)
        .clickable { viewModel.setTab(AppNavigationTab.SIYA_AI) }
        .testTag("vendor_siya_copilot_card")
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(KiranaAmber),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "सिया (Siya) 24/7 AI Kirana Copilot",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
              )
              Text(
                text = "Stock inquiries, sales summary, recipes & festival WhatsApp drafts",
                fontSize = 10.5.sp,
                color = Color(0xFF6D4C41)
              )
            }
          }
          Text(
            text = "Open →",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = WhatsAppDarkTeal
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick action prompt chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCC80)),
              modifier = Modifier.clickable {
                viewModel.sendSiyaQuery("Vendor Report: Aaj kitni bikri hui aur kitne orders aaye?")
                viewModel.setTab(AppNavigationTab.SIYA_AI)
              }
            ) {
              Text(
                text = "📊 Today's Summary",
                fontSize = 11.sp,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Medium
              )
            }
          }
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCC80)),
              modifier = Modifier.clickable {
                viewModel.sendSiyaQuery("Customer ke liye weekend discount offer WhatsApp message draft karo")
                viewModel.setTab(AppNavigationTab.SIYA_AI)
              }
            ) {
              Text(
                text = "🎉 WhatsApp Offer Draft",
                fontSize = 11.sp,
                color = WhatsAppDarkTeal,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Medium
              )
            }
          }
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCC80)),
              modifier = Modifier.clickable {
                viewModel.sendSiyaQuery("Customer ke liye polite udhaar payment reminder draft karo")
                viewModel.setTab(AppNavigationTab.SIYA_AI)
              }
            ) {
              Text(
                text = "📝 Payment Reminder",
                fontSize = 11.sp,
                color = Color(0xFF5D4037),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // Status Filter Chips Bar
    val filterTabs = listOf(
      "ALL" to "Sabhi (${orders.size})",
      OrderStatus.NEW.name to "Naye (${orders.count { it.status == OrderStatus.NEW.name }})",
      OrderStatus.ACCEPTED.name to "Accept (${orders.count { it.status == OrderStatus.ACCEPTED.name }})",
      OrderStatus.PACKED.name to "Packed (${orders.count { it.status == OrderStatus.PACKED.name }})",
      OrderStatus.OUT_FOR_DELIVERY.name to "Raste me (${orders.count { it.status == OrderStatus.OUT_FOR_DELIVERY.name }})",
      OrderStatus.COMPLETED.name to "Delivered (${orders.count { it.status == OrderStatus.COMPLETED.name }})"
    )

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(filterTabs) { (key, label) ->
        val isSelected = (activeFilter ?: "ALL") == key
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (isSelected) WhatsAppDarkTeal else Color.White,
          shadowElevation = 0.5.dp,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { viewModel.setOrderFilter(key) }
            .testTag("filter_chip_$key")
        ) {
          Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF455A64),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Orders List
    if (filteredOrders.isEmpty()) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📦", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Koi order nahi mila",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF54656F)
          )
          Text(
            text = "Giraahak ke WhatsApp voice note aate hi yahan live dikhenge!",
            fontSize = 12.sp,
            color = Color(0xFF8696A0),
            modifier = Modifier.padding(top = 4.dp)
          )
          Spacer(modifier = Modifier.height(14.dp))
          Button(
            onClick = {
              viewModel.setOrderFilter("ALL")
              viewModel.reloadOrdersDemoData()
            },
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal)
          ) {
            Text("📋 Sample Voice Orders Load Karein")
          }
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredOrders, key = { it.id }) { order ->
          VendorOrderCard(
            order = order,
            viewModel = viewModel,
            onCallCustomer = {
              try {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                  data = Uri.parse("tel:${order.customerPhone}")
                }
                context.startActivity(intent)
              } catch (e: Exception) {}
            },
            onShareWhatsApp = {
              try {
                val itemsList = viewModel.deserializeItems(order.itemsJson)
                val summary = itemsList.joinToString("\n") { "• ${it.quantity} ${it.unit} ${it.name}" }
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_TEXT, "Namaste ${order.customerName}! Aapka order (${order.orderNumber}):\n$summary\nTotal: ₹${order.totalAmount.toInt()}\nStatus: ${order.status}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share via WhatsApp"))
              } catch (e: Exception) {}
            }
          )
        }
      }
    }
  }
}

@Composable
fun VendorOrderCard(
  order: CustomerOrder,
  viewModel: ShopViewModel,
  onCallCustomer: () -> Unit,
  onShareWhatsApp: () -> Unit
) {
  val items = remember(order.itemsJson) {
    viewModel.deserializeItems(order.itemsJson)
  }
  val orderTimeFormatted = remember(order.timestamp) {
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(order.timestamp))
  }

  // Packing Checklist state for Shopkeeper
  val checkedItems = remember { mutableStateMapOf<Int, Boolean>() }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("vendor_order_card_${order.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Order number, Voice Badge, Status
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = order.orderNumber,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = WhatsAppDarkTeal
          )
          Spacer(modifier = Modifier.width(6.dp))
          if (order.isVoiceOrder) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFE0F2F1)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Voice Order",
                  tint = WhatsAppTealGreen,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "Voice AI",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = WhatsAppTealGreen
                )
              }
            }
          }
        }

        OrderStatusBadge(status = order.status)
      }

      // Customer Info & Call Row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Column {
          Text(
            text = order.customerName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF263238)
          )
          Text(
            text = "📞 ${order.customerPhone} • $orderTimeFormatted",
            fontSize = 11.sp,
            color = Color(0xFF78909C)
          )
          Text(
            text = "📍 ${order.deliveryAddress}",
            fontSize = 11.sp,
            color = Color(0xFF54656F),
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        // Quick Actions: Soundbox Speak, Invoice Parchi, Call & WhatsApp Share
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Speak Order Soundbox
          Surface(
            shape = CircleShape,
            color = Color(0xFFFFF3E0),
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .clickable { viewModel.announceOrder(order) }
              .testTag("speak_order_${order.id}")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Speak Order",
                tint = Color(0xFFE65100),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          // Bill / Receipt
          Surface(
            shape = CircleShape,
            color = Color(0xFFEDE7F6),
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .clickable { viewModel.viewInvoice(order) }
              .testTag("view_invoice_${order.id}")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = "View Invoice Receipt",
                tint = Color(0xFF512DA8),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = Color(0xFFE8F5E9),
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .clickable { onCallCustomer() }
              .testTag("call_customer_btn")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call Customer",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = Color(0xFFE0F2F1),
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .clickable { onShareWhatsApp() }
              .testTag("share_whatsapp_btn")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share WhatsApp Update",
                tint = WhatsAppTealGreen,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      // Customer Voice Transcript (if Voice Order)
      if (order.isVoiceOrder && order.rawVoiceTranscript.isNotBlank()) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF9FBE7),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "🗣️ Audio Transcript Received:",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF827717)
            )
            Text(
              text = "\"${order.rawVoiceTranscript}\"",
              fontSize = 12.sp,
              color = Color(0xFF33691E),
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = Color(0xFFECEFF1)
      )

      // Packing Checklist (Shopkeeper physically checks off items as they put into bag)
      Text(
        text = "PACKING CHECKLIST (Tap item to check):",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF90A4AE)
      )

      items.forEachIndexed { index, item ->
        val isChecked = checkedItems[index] ?: (order.status != OrderStatus.NEW.name)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { checkedItems[index] = !isChecked }
            .padding(vertical = 2.dp)
        ) {
          Checkbox(
            checked = isChecked,
            onCheckedChange = { checkedItems[index] = it },
            colors = CheckboxDefaults.colors(
              checkedColor = WhatsAppTealGreen,
              uncheckedColor = Color(0xFFB0BEC5)
            ),
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${item.emoji} ${item.name}",
            fontSize = 13.sp,
            fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Medium,
            color = if (isChecked) Color(0xFF90A4AE) else Color(0xFF263238),
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

      // Total & Payment Mode
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "Payment: ${order.paymentMethod}",
          fontSize = 11.sp,
          color = Color(0xFF54656F),
          fontWeight = FontWeight.Medium
        )
        Text(
          text = "Total: ₹${order.totalAmount.toInt()}",
          fontSize = 16.sp,
          fontWeight = FontWeight.ExtraBold,
          color = WhatsAppDarkTeal
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dynamic Action Buttons based on order stage
      when (order.status) {
        OrderStatus.NEW.name -> {
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
                .clickable { viewModel.updateOrderStatus(order.id, OrderStatus.ACCEPTED) }
                .testTag("accept_order_${order.id}")
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
                  text = "Accept (Sweekar Karein)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFFEBEE),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.updateOrderStatus(order.id, OrderStatus.REJECTED) }
                .testTag("reject_order_${order.id}")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = null,
                  tint = Color(0xFFC62828),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Reject",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFC62828)
                )
              }
            }
          }
        }

        OrderStatus.ACCEPTED.name -> {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = WhatsAppTealGreen,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { viewModel.updateOrderStatus(order.id, OrderStatus.PACKED) }
              .testTag("mark_packed_${order.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Pack Ho Gaya (Mark as Packed)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        OrderStatus.PACKED.name -> {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1565C0),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) }
              .testTag("out_for_delivery_${order.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeliveryDining,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Delivery Boy Nikla (Out for Delivery)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        OrderStatus.OUT_FOR_DELIVERY.name -> {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF2E7D32),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { viewModel.updateOrderStatus(order.id, OrderStatus.COMPLETED) }
              .testTag("mark_delivered_${order.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Giraahak ko Mil Gaya (Delivered)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        OrderStatus.COMPLETED.name -> {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F8E9),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "✅ Order successfully delivered and paid.",
              fontSize = 12.sp,
              color = Color(0xFF33691E),
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
          }
        }

        OrderStatus.REJECTED.name -> {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "❌ Order cancelled.",
              fontSize = 12.sp,
              color = Color(0xFFC62828),
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
          }
        }
      }
    }
  }
}

package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerOrder
import com.example.data.model.OrderItem
import com.example.data.model.ShopSettings
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDialog(
  order: CustomerOrder,
  items: List<OrderItem>,
  shopSettings: ShopSettings,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val dateFormatted = remember(order.timestamp) {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.timestamp))
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = {
          val sb = StringBuilder()
          sb.append("🧾 *${shopSettings.shopName} - BILL RECEIPT*\n")
          sb.append("Order: ${order.orderNumber} | Date: $dateFormatted\n")
          sb.append("Customer: ${order.customerName} (${order.customerPhone})\n")
          sb.append("Address: ${order.deliveryAddress}\n\n")
          sb.append("ITEMS:\n")
          items.forEach { item ->
            sb.append("• ${item.name}: ${item.quantity} ${item.unit} x ₹${item.pricePerUnit.toInt()} = ₹${item.totalPrice.toInt()}\n")
          }
          sb.append("\n💰 *TOTAL AMOUNT: ₹${order.totalAmount.toInt()}*\n")
          sb.append("Payment: ${order.paymentMethod}\n")
          sb.append("UPI ID: ${shopSettings.upiId}\n\n")
          sb.append("🙏 *Dhanyawad! Padhariye fir se!*")

          val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
          }
          context.startActivity(Intent.createChooser(intent, "Share Bill Receipt"))
        },
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTealGreen),
        modifier = Modifier.testTag("share_bill_receipt_btn")
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share on WhatsApp")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close")
      }
    },
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = WhatsAppDarkTeal)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Dukaan Parchi (Invoice)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Authentic Indian Kirana Slip Style
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFFF59D), RoundedCornerShape(8.dp))
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
          ) {
            Text(
              text = "|| श्री गणेशाय नमः ||",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE65100)
            )
            Text(
              text = shopSettings.shopName,
              fontSize = 17.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF212121),
              textAlign = TextAlign.Center
            )
            Text(
              text = "${shopSettings.shopAddress}\n📞 ${shopSettings.whatsappNumber} | UPI: ${shopSettings.upiId}",
              fontSize = 10.sp,
              color = Color(0xFF616161),
              textAlign = TextAlign.Center,
              lineHeight = 14.sp
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFBDBDBD))

            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(text = "Bill #: ${order.orderNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(text = dateFormatted, fontSize = 10.sp, color = Color.DarkGray)
            }

            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
              Text(text = "Giraahak: ${order.customerName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              Text(text = order.customerPhone, fontSize = 10.sp, color = Color.DarkGray)
            }
            Text(
              text = "Address: ${order.deliveryAddress}",
              fontSize = 10.sp,
              color = Color.DarkGray,
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFBDBDBD))

            // Table Header
            Row(modifier = Modifier.fillMaxWidth()) {
              Text(text = "Item", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
              Text(text = "Qty", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
              Text(text = "Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
              Text(text = "Total", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE0E0E0))

            // Item rows
            items.forEach { item ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
              ) {
                Text(
                  text = "${item.emoji} ${item.name}",
                  fontSize = 11.sp,
                  color = Color(0xFF212121),
                  modifier = Modifier.weight(2f)
                )
                Text(
                  text = "${if (item.quantity % 1.0 == 0.0) item.quantity.toInt() else item.quantity} ${item.unit}",
                  fontSize = 10.sp,
                  color = Color.DarkGray,
                  modifier = Modifier.weight(1f),
                  textAlign = TextAlign.Center
                )
                Text(
                  text = "₹${item.pricePerUnit.toInt()}",
                  fontSize = 10.sp,
                  color = Color.DarkGray,
                  modifier = Modifier.weight(1f),
                  textAlign = TextAlign.End
                )
                Text(
                  text = "₹${item.totalPrice.toInt()}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF212121),
                  modifier = Modifier.weight(1f),
                  textAlign = TextAlign.End
                )
              }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFBDBDBD))

            // Summary Totals
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(text = "Delivery Fee:", fontSize = 11.sp, color = Color.DarkGray)
              Text(text = "FREE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
              Text(text = "GRAND TOTAL:", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
              Text(text = "₹${order.totalAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFBF360C))
            }

            Text(
              text = "Payment: ${order.paymentMethod}",
              fontSize = 10.sp,
              color = Color.DarkGray,
              modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFBDBDBD))

            Text(
              text = "🙏🙏 Dhanyawad! Padhariye fir se! 🙏🙏",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE65100),
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  )
}

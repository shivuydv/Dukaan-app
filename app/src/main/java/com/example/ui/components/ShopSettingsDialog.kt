package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShopSettings
import com.example.ui.theme.WhatsAppTealGreen

@Composable
fun ShopSettingsDialog(
  currentSettings: ShopSettings,
  onSave: (ShopSettings) -> Unit,
  onDismiss: () -> Unit,
  onResetDemoData: (() -> Unit)? = null
) {
  var shopName by remember { mutableStateOf(currentSettings.shopName) }
  var ownerName by remember { mutableStateOf(currentSettings.ownerName) }
  var whatsappNumber by remember { mutableStateOf(currentSettings.whatsappNumber) }
  var upiId by remember { mutableStateOf(currentSettings.upiId) }
  var shopAddress by remember { mutableStateOf(currentSettings.shopAddress) }
  var minOrderAmount by remember { mutableStateOf(currentSettings.minOrderAmount.toInt().toString()) }
  var voiceSoundboxEnabled by remember { mutableStateOf(currentSettings.voiceAssistantEnabled) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = WhatsAppTealGreen)
        Spacer(modifier = Modifier.size(8.dp))
        Text("Dukaan Details & Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = shopName,
          onValueChange = { shopName = it },
          label = { Text("Shop Name (Dukaan ka naam)") },
          modifier = Modifier.fillMaxWidth().testTag("setting_shop_name")
        )
        OutlinedTextField(
          value = ownerName,
          onValueChange = { ownerName = it },
          label = { Text("Shopkeeper Name (Dukaandar)") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = whatsappNumber,
          onValueChange = { whatsappNumber = it },
          label = { Text("WhatsApp Business Number") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = upiId,
          onValueChange = { upiId = it },
          label = { Text("UPI ID (for payments)") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = shopAddress,
          onValueChange = { shopAddress = it },
          label = { Text("Shop Address") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = minOrderAmount,
          onValueChange = { minOrderAmount = it },
          label = { Text("Min Order for Free Delivery (₹)") },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Soundbox Voice Switch
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = WhatsAppTealGreen)
            Spacer(modifier = Modifier.size(6.dp))
            Column {
              Text("Kirana Soundbox (बोलने वाला स्पीकर)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text("Naye order aane par Hindi me bolkar sunaye", fontSize = 10.sp, color = androidx.compose.ui.graphics.Color.Gray)
            }
          }
          Switch(
            checked = voiceSoundboxEnabled,
            onCheckedChange = { voiceSoundboxEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppTealGreen)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset Store Data Button
        androidx.compose.material3.OutlinedButton(
          onClick = {
            onResetDemoData?.invoke()
            onDismiss()
          },
          colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = androidx.compose.ui.graphics.Color(0xFFD32F2F)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("🔄 Reset 18 Demo Items & Sample Orders", fontSize = 11.sp)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            currentSettings.copy(
              shopName = shopName.ifBlank { "Shree Ganesh Kirana Store" },
              ownerName = ownerName.ifBlank { "Rajesh Gupta" },
              whatsappNumber = whatsappNumber.ifBlank { "+91 98765 43210" },
              upiId = upiId.ifBlank { "shreeganesh@upi" },
              shopAddress = shopAddress.ifBlank { "Shop #4, Main Market, Sector 12" },
              minOrderAmount = minOrderAmount.toDoubleOrNull() ?: 100.0,
              voiceAssistantEnabled = voiceSoundboxEnabled
            )
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTealGreen)
      ) {
        Text("Save Changes")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.ShopSettingsDialog
import com.example.ui.screens.MarketingPosterScreen
import com.example.ui.screens.SiyaAiScreen
import com.example.ui.screens.VendorDashboardScreen
import com.example.ui.screens.WhatsAppCatalogScreen
import com.example.ui.screens.WhatsAppChatScreen
import com.example.ui.theme.DukaanVoiceTheme
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import com.example.ui.viewmodel.AppNavigationTab
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: ShopViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DukaanVoiceTheme {
        MainAppContent(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: ShopViewModel) {
  val activeTab by viewModel.activeTab.collectAsState()
  val newOrdersCount by viewModel.newOrdersCount.collectAsState()
  val cart by viewModel.catalogCart.collectAsState()
  val cartCount = cart.values.sum()

  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("main_navigation_bar")
      ) {
        // Tab 1: WhatsApp Voice Assistant Simulator
        NavigationBarItem(
          selected = activeTab == AppNavigationTab.WHATSAPP_SIMULATOR,
          onClick = { viewModel.setTab(AppNavigationTab.WHATSAPP_SIMULATOR) },
          icon = {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Chat,
              contentDescription = "WhatsApp Chat",
              modifier = Modifier.size(22.dp)
            )
          },
          label = {
            Text(
              text = "WhatsApp AI",
              fontSize = 10.5.sp,
              fontWeight = if (activeTab == AppNavigationTab.WHATSAPP_SIMULATOR) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = WhatsAppDarkTeal,
            selectedTextColor = WhatsAppDarkTeal,
            indicatorColor = Color(0xFFE0F2F1),
            unselectedIconColor = Color(0xFF78909C),
            unselectedTextColor = Color(0xFF78909C)
          ),
          modifier = Modifier.testTag("nav_whatsapp_chat")
        )

        // Tab 2: Siya 24/7 AI Agent
        NavigationBarItem(
          selected = activeTab == AppNavigationTab.SIYA_AI,
          onClick = { viewModel.setTab(AppNavigationTab.SIYA_AI) },
          icon = {
            BadgedBox(
              badge = {
                Badge(containerColor = WhatsAppLightGreen) {
                  Text(text = "24/7", color = Color(0xFF075E54), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Siya 24/7 AI Agent",
                modifier = Modifier.size(22.dp)
              )
            }
          },
          label = {
            Text(
              text = "सिया AI",
              fontSize = 10.5.sp,
              fontWeight = if (activeTab == AppNavigationTab.SIYA_AI) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KiranaAmber,
            selectedTextColor = KiranaAmber,
            indicatorColor = Color(0xFFFFF3E0),
            unselectedIconColor = Color(0xFF78909C),
            unselectedTextColor = Color(0xFF78909C)
          ),
          modifier = Modifier.testTag("nav_siya_ai")
        )

        // Tab 2: Vendor Dashboard
        NavigationBarItem(
          selected = activeTab == AppNavigationTab.VENDOR_DASHBOARD,
          onClick = { viewModel.setTab(AppNavigationTab.VENDOR_DASHBOARD) },
          icon = {
            BadgedBox(
              badge = {
                if (newOrdersCount > 0) {
                  Badge(containerColor = KiranaAmber) {
                    Text(text = "$newOrdersCount", color = Color.White, fontSize = 10.sp)
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = "Vendor Dashboard",
                modifier = Modifier.size(22.dp)
              )
            }
          },
          label = {
            Text(
              text = "Dukaan",
              fontSize = 11.sp,
              fontWeight = if (activeTab == AppNavigationTab.VENDOR_DASHBOARD) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = WhatsAppDarkTeal,
            selectedTextColor = WhatsAppDarkTeal,
            indicatorColor = Color(0xFFE0F2F1),
            unselectedIconColor = Color(0xFF78909C),
            unselectedTextColor = Color(0xFF78909C)
          ),
          modifier = Modifier.testTag("nav_vendor_dashboard")
        )

        // Tab 3: Interactive Catalog
        NavigationBarItem(
          selected = activeTab == AppNavigationTab.INTERACTIVE_CATALOG,
          onClick = { viewModel.setTab(AppNavigationTab.INTERACTIVE_CATALOG) },
          icon = {
            BadgedBox(
              badge = {
                if (cartCount > 0) {
                  Badge(containerColor = WhatsAppTealGreen) {
                    Text(text = "$cartCount", color = Color.White, fontSize = 10.sp)
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = "Catalog",
                modifier = Modifier.size(22.dp)
              )
            }
          },
          label = {
            Text(
              text = "Catalog",
              fontSize = 11.sp,
              fontWeight = if (activeTab == AppNavigationTab.INTERACTIVE_CATALOG) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = WhatsAppDarkTeal,
            selectedTextColor = WhatsAppDarkTeal,
            indicatorColor = Color(0xFFE0F2F1),
            unselectedIconColor = Color(0xFF78909C),
            unselectedTextColor = Color(0xFF78909C)
          ),
          modifier = Modifier.testTag("nav_catalog")
        )

        // Tab 4: Marketing Stand / QR Code
        NavigationBarItem(
          selected = activeTab == AppNavigationTab.MARKETING_STAND,
          onClick = { viewModel.setTab(AppNavigationTab.MARKETING_STAND) },
          icon = {
            Icon(
              imageVector = Icons.Default.QrCode,
              contentDescription = "Poster",
              modifier = Modifier.size(22.dp)
            )
          },
          label = {
            Text(
              text = "QR Stand",
              fontSize = 11.sp,
              fontWeight = if (activeTab == AppNavigationTab.MARKETING_STAND) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = WhatsAppDarkTeal,
            selectedTextColor = WhatsAppDarkTeal,
            indicatorColor = Color(0xFFE0F2F1),
            unselectedIconColor = Color(0xFF78909C),
            unselectedTextColor = Color(0xFF78909C)
          ),
          modifier = Modifier.testTag("nav_marketing_stand")
        )
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    val selectedInvoiceOrder by viewModel.selectedOrderForInvoice.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val shopSettings by viewModel.shopSettings.collectAsState()

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (activeTab) {
        AppNavigationTab.WHATSAPP_SIMULATOR -> WhatsAppChatScreen(viewModel = viewModel)
        AppNavigationTab.SIYA_AI -> SiyaAiScreen(viewModel = viewModel)
        AppNavigationTab.VENDOR_DASHBOARD -> VendorDashboardScreen(viewModel = viewModel)
        AppNavigationTab.INTERACTIVE_CATALOG -> WhatsAppCatalogScreen(viewModel = viewModel)
        AppNavigationTab.MARKETING_STAND -> MarketingPosterScreen(viewModel = viewModel)
      }

      // Global Bill / Invoice Dialog
      selectedInvoiceOrder?.let { order ->
        val items = remember(order.itemsJson) {
          viewModel.deserializeItems(order.itemsJson)
        }
        InvoiceDialog(
          order = order,
          items = items,
          shopSettings = shopSettings,
          onDismiss = { viewModel.dismissInvoice() }
        )
      }

      // Global Shop Settings Dialog
      if (showSettingsDialog) {
        ShopSettingsDialog(
          currentSettings = shopSettings,
          onSave = { updated ->
            viewModel.updateSettings(updated)
          },
          onDismiss = { viewModel.closeSettings() },
          onResetDemoData = { viewModel.reloadAllDemoData() }
        )
      }
    }
  }
}

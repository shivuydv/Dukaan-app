package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShopItem
import com.example.ui.theme.KiranaAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTealGreen
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppCatalogScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val items by viewModel.allItems.collectAsState()
  val cart by viewModel.catalogCart.collectAsState()
  val settings by viewModel.shopSettings.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("ALL") }
  var showAddItemDialog by remember { mutableStateOf(false) }
  var editingItemPrice by remember { mutableStateOf<ShopItem?>(null) }
  var isVendorEditMode by remember { mutableStateOf(false) }

  val categories = remember(items) {
    listOf("ALL") + items.map { it.category }.distinct()
  }

  val filteredItems = remember(items, searchQuery, selectedCategory) {
    items.filter { item ->
      val matchesCat = (selectedCategory == "ALL") || (item.category == selectedCategory)
      val matchesSearch = searchQuery.isBlank() ||
        item.name.contains(searchQuery, ignoreCase = true) ||
        item.hindiName.contains(searchQuery, ignoreCase = true) ||
        item.searchKeywords.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesSearch
    }
  }

  val cartItemCount = cart.values.sum()
  val cartTotal = remember(cart, items) {
    val map = items.associateBy { it.id }
    cart.entries.sumOf { (id, qty) -> (map[id]?.price ?: 0.0) * qty }
  }

  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F9F8))
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(
                text = "WhatsApp Catalog",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${settings.shopName} • ${items.size} Items",
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp
              )
            }

            // Vendor Mode Toggle (to adjust prices or stock)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isVendorEditMode) KiranaAmber else Color(0x33FFFFFF))
                .clickable { isVendorEditMode = !isVendorEditMode }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("vendor_price_mode_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isVendorEditMode) "Edit Price/Stock" else "Customer View",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Search Bar
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF78909C),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search aalu, butter, aata, doodh...", fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color.Transparent,
                  unfocusedBorderColor = Color.Transparent
                )
              )
            }
          }
        }
      }

      // Categories Horizontal Row
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { category ->
          val isSelected = selectedCategory == category
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) WhatsAppTealGreen else Color.White,
            shadowElevation = 0.5.dp,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable { selectedCategory = category }
          ) {
            Text(
              text = if (category == "ALL") "All Categories" else category,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Color(0xFF37474F),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      // Catalog Items Grid/List
      if (filteredItems.isEmpty()) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(text = "🛒", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (searchQuery.isNotBlank()) "Koi item nahi mila" else "Catalog khali hai",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (searchQuery.isNotBlank()) "Kripya spelling check karein ya category badlein" else "Aap 1-tap me 18 daily Kirana items reload kar sakte hain.",
              fontSize = 12.sp,
              color = Color(0xFF78909C),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = {
                searchQuery = ""
                selectedCategory = "ALL"
                viewModel.reloadCatalogDemoData()
              },
              colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTealGreen)
            ) {
              Text("📦 18 Kirana Items Load Karein")
            }
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(
            start = 14.dp,
            end = 14.dp,
            top = 4.dp,
            bottom = if (cartItemCount > 0) 90.dp else 24.dp
          ),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(filteredItems, key = { it.id }) { item ->
            val qtyInCart = cart[item.id] ?: 0
            CatalogItemCard(
              item = item,
              qtyInCart = qtyInCart,
              isVendorMode = isVendorEditMode,
              onAddToCart = { viewModel.addToCart(item.id) },
              onRemoveFromCart = { viewModel.removeFromCart(item.id) },
              onToggleStock = { viewModel.updateItemStock(item.id, !item.inStock) },
              onEditPrice = { editingItemPrice = item }
            )
          }
        }
      }
    }

    // Add New Item FAB (Vendor Mode)
    if (isVendorEditMode) {
      FloatingActionButton(
        onClick = { showAddItemDialog = true },
        containerColor = KiranaAmber,
        contentColor = Color.White,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("add_item_fab")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 14.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Add Item", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Floating Cart Bar (Customer Mode)
    if (!isVendorEditMode && cartItemCount > 0) {
      Surface(
        color = WhatsAppDarkTeal,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .padding(16.dp)
          .testTag("catalog_cart_bar")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WhatsAppLightGreen)
            ) {
              Text(
                text = "$cartItemCount",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "₹${cartTotal.toInt()}",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
              )
              Text(
                text = "Total Cart Value",
                color = Color(0xFFB0BEC5),
                fontSize = 11.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = WhatsAppLightGreen,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { viewModel.checkoutCatalogCart() }
              .testTag("checkout_whatsapp_btn")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Order on WhatsApp",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }
  }

  // Edit Price Dialog
  if (editingItemPrice != null) {
    val item = editingItemPrice!!
    var newPriceText by remember { mutableStateOf(item.price.toInt().toString()) }

    AlertDialog(
      onDismissRequest = { editingItemPrice = null },
      title = { Text("Update Price: ${item.name}") },
      text = {
        Column {
          Text(text = "Current: ₹${item.price} per ${item.unit}", fontSize = 13.sp)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = newPriceText,
            onValueChange = { newPriceText = it },
            label = { Text("New Price (₹)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val p = newPriceText.toDoubleOrNull()
            if (p != null) {
              viewModel.updateItemPrice(item.id, p)
            }
            editingItemPrice = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTealGreen)
        ) {
          Text("Save Price")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingItemPrice = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Add Item Dialog
  if (showAddItemDialog) {
    var name by remember { mutableStateOf("") }
    var hindiName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kirana & Staples") }
    var price by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("packet") }
    var emoji by remember { mutableStateOf("🛒") }

    AlertDialog(
      onDismissRequest = { showAddItemDialog = false },
      title = { Text("Add Item to WhatsApp Catalog") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Item Name (e.g. Fortune Besan 500g)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = hindiName,
            onValueChange = { hindiName = it },
            label = { Text("Hindi Name (e.g. बेसन)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price in ₹") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Unit (kg, packet, litre, piece)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val p = price.toDoubleOrNull() ?: 0.0
            if (name.isNotBlank()) {
              viewModel.addNewItem(name, hindiName, category, p, unit, emoji)
            }
            showAddItemDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = KiranaAmber)
        ) {
          Text("Add to Catalog")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddItemDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun CatalogItemCard(
  item: ShopItem,
  qtyInCart: Int,
  isVendorMode: Boolean,
  onAddToCart: () -> Unit,
  onRemoveFromCart: () -> Unit,
  onToggleStock: () -> Unit,
  onEditPrice: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Emoji / Icon Box
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFFF1F8E9))
      ) {
        Text(text = item.emoji, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Item Details
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
          )
        }
        Text(
          text = "${item.hindiName} • ${item.category}",
          fontSize = 11.sp,
          color = Color(0xFF78909C)
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Text(
            text = "₹${item.price.toInt()}",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WhatsAppDarkTeal
          )
          Text(
            text = " / ${item.unit}",
            fontSize = 12.sp,
            color = Color(0xFF54656F)
          )
          if (!item.inStock) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFFFEBEE)
            ) {
              Text(
                text = "OUT OF STOCK",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // Actions: Vendor vs Customer
      if (isVendorMode) {
        Column(horizontalAlignment = Alignment.End) {
          // In-Stock switch
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (item.inStock) "In Stock" else "Khatam",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (item.inStock) WhatsAppTealGreen else Color.Red
            )
            Spacer(modifier = Modifier.width(4.dp))
            Switch(
              checked = item.inStock,
              onCheckedChange = { onToggleStock() },
              colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppTealGreen),
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Edit Price button
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE0F2F1),
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable { onEditPrice() }
          ) {
            Text(
              text = "Edit ₹",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = WhatsAppDarkTeal,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      } else {
        // Customer Cart Selector
        if (!item.inStock) {
          Text(
            text = "Unavailable",
            fontSize = 12.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Medium
          )
        } else if (qtyInCart == 0) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = WhatsAppDarkTeal,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onAddToCart() }
              .testTag("add_item_${item.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "ADD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFE0F2F1))
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            IconButton(
              onClick = { onRemoveFromCart() },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Minus",
                tint = WhatsAppDarkTeal,
                modifier = Modifier.size(16.dp)
              )
            }
            Text(
              text = "$qtyInCart",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = WhatsAppDarkTeal,
              modifier = Modifier.padding(horizontal = 6.dp)
            )
            IconButton(
              onClick = { onAddToCart() },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Plus",
                tint = WhatsAppDarkTeal,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

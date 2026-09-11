package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val hindiName: String,
  val category: String,
  val price: Double,
  val unit: String, // "kg", "packet", "litre", "gram", "piece"
  val inStock: Boolean = true,
  val emoji: String = "🛒",
  val searchKeywords: String = ""
)

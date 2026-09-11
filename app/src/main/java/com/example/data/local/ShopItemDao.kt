package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ShopItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {
  @Query("SELECT * FROM shop_items ORDER BY category ASC, name ASC")
  fun getAllItems(): Flow<List<ShopItem>>

  @Query("SELECT * FROM shop_items WHERE inStock = 1 ORDER BY name ASC")
  fun getInStockItems(): Flow<List<ShopItem>>

  @Query("SELECT * FROM shop_items WHERE category = :category ORDER BY name ASC")
  fun getItemsByCategory(category: String): Flow<List<ShopItem>>

  @Query("SELECT DISTINCT category FROM shop_items ORDER BY category ASC")
  fun getAllCategories(): Flow<List<String>>

  @Query("SELECT * FROM shop_items WHERE id = :id LIMIT 1")
  suspend fun getItemById(id: Long): ShopItem?

  @Query("SELECT * FROM shop_items WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(hindiName) LIKE '%' || LOWER(:query) || '%' OR LOWER(searchKeywords) LIKE '%' || LOWER(:query) || '%'")
  fun searchItems(query: String): Flow<List<ShopItem>>

  @Query("SELECT COUNT(*) FROM shop_items")
  suspend fun getItemCount(): Int

  @Query("SELECT * FROM shop_items")
  suspend fun getAllItemsSnapshot(): List<ShopItem>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: ShopItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<ShopItem>)

  @Update
  suspend fun updateItem(item: ShopItem)

  @Query("UPDATE shop_items SET inStock = :inStock WHERE id = :id")
  suspend fun updateStock(id: Long, inStock: Boolean)

  @Query("UPDATE shop_items SET price = :newPrice WHERE id = :id")
  suspend fun updatePrice(id: Long, newPrice: Double)

  @Delete
  suspend fun deleteItem(item: ShopItem)
}

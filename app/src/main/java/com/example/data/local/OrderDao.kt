package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomerOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
  @Query("SELECT * FROM customer_orders ORDER BY timestamp DESC")
  fun getAllOrders(): Flow<List<CustomerOrder>>

  @Query("SELECT * FROM customer_orders WHERE status = :status ORDER BY timestamp DESC")
  fun getOrdersByStatus(status: String): Flow<List<CustomerOrder>>

  @Query("SELECT * FROM customer_orders WHERE id = :id LIMIT 1")
  suspend fun getOrderById(id: Long): CustomerOrder?

  @Query("SELECT COUNT(*) FROM customer_orders WHERE status = 'NEW'")
  fun getNewOrdersCount(): Flow<Int>

  @Query("SELECT SUM(totalAmount) FROM customer_orders WHERE status != 'REJECTED'")
  fun getTotalRevenue(): Flow<Double?>

  @Query("SELECT COUNT(*) FROM customer_orders")
  suspend fun getOrderCount(): Int

  @Query("SELECT * FROM customer_orders")
  suspend fun getAllOrdersSnapshot(): List<CustomerOrder>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: CustomerOrder): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(orders: List<CustomerOrder>)

  @Update
  suspend fun updateOrder(order: CustomerOrder)

  @Query("UPDATE customer_orders SET status = :status WHERE id = :id")
  suspend fun updateOrderStatus(id: Long, status: String)

  @Delete
  suspend fun deleteOrder(order: CustomerOrder)
}

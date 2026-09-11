package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CustomerOrder
import com.example.data.model.ShopItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ShopItem::class, CustomerOrder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun shopItemDao(): ShopItemDao
  abstract fun orderDao(): OrderDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "dukaan_voice_database"
        )
        .fallbackToDestructiveMigration(true)
        .addCallback(AppDatabaseCallback(scope))
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class AppDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      scope.launch(Dispatchers.IO) {
        INSTANCE?.let { database ->
          database.shopItemDao().insertAll(PreloadedData.initialShopItems)
          database.orderDao().insertAll(PreloadedData.sampleInitialOrders)
        }
      }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
      super.onOpen(db)
      scope.launch(Dispatchers.IO) {
        INSTANCE?.let { database ->
          try {
            if (database.shopItemDao().getItemCount() == 0) {
              database.shopItemDao().insertAll(PreloadedData.initialShopItems)
            }
            if (database.orderDao().getOrderCount() == 0) {
              database.orderDao().insertAll(PreloadedData.sampleInitialOrders)
            }
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    }
  }
}

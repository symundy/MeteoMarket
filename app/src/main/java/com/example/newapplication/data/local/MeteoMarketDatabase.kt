package com.example.newapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameSaveSlot::class], version = 1, exportSchema = false)
abstract class MeteoMarketDatabase : RoomDatabase() {

    abstract fun gameSlotDao(): GameSlotDao

    companion object {
        @Volatile
        private var INSTANCE: MeteoMarketDatabase? = null

        fun getDatabase(context: Context): MeteoMarketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeteoMarketDatabase::class.java,
                    "meteo_market_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true) // Fixed version!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
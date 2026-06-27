package com.example.newapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// 1. Declare this as a Room Database, list your entities, and set the version
@Database(entities = [GameSaveSlot::class], version = 1, exportSchema = false)
abstract class MeteoMarketDatabase : RoomDatabase() {

    // 2. Connect your DAO interface
    abstract fun gameSlotDao(): GameSlotDao

    // 3. Set up a Singleton pattern to prevent multiple database instances running at once
    companion object {
        @Volatile
        private var INSTANCE: MeteoMarketDatabase? = null

        fun getDatabase(context: Context): MeteoMarketDatabase {
            // If the INSTANCE is not null, return it. If it is, create the database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeteoMarketDatabase::class.java,
                    "meteo_market_database" // The actual file name stored on the phone
                )
                    // If you update your database structure later, this wipes and resets the data
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
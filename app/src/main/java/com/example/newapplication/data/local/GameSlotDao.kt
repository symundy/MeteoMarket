package com.example.newapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSlotDao {

    // 1. Insert or Overwrite a save slot
    // If a save already exists in slot 1, REPLACE will just overwrite it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: GameSaveSlot)

    // 2. Update an existing slot (this fixes your current error!)
    @Update
    suspend fun updateSlot(slot: GameSaveSlot)

    // 3. Read all slots from the database (we will use this for the Main Menu later)
    @Query("SELECT * FROM game_slots ORDER BY slotNumber ASC")
    fun getAllSlots(): Flow<List<GameSaveSlot>>

    @Query("SELECT * FROM game_slots WHERE slotNumber = :slotId LIMIT 1")
    suspend fun getSlotById(slotId: Int): GameSaveSlot?

    @Query("DELETE FROM game_slots")
    suspend fun deleteAllSlots()
}
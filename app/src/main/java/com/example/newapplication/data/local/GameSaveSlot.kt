package com.example.newapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "game_slots")
data class GameSaveSlot(
    @PrimaryKey val slotNumber: Int, // 1, 2, or 3
    val balance: Double,
    val currentDay: Int,
    val isOccupied: Boolean = false
)
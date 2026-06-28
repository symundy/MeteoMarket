package com.example.newapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newapplication.data.local.GameSaveSlot
import com.example.newapplication.data.local.GameSlotDao
import com.example.newapplication.data.models.PlayerProfile
import com.example.newapplication.data.models.WeatherMarket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MarketViewModel(private val gameSlotDao: GameSlotDao) : ViewModel() {

    // Mutable state inside the ViewModel, exposed as read-only to the UI
    var playerProfile by mutableStateOf(PlayerProfile(username = "Snowbody", balance = 1500.0, "current game","Slot 1", "Slot 2", "Slot 3"))
        private set

    fun startNewGame(newGameName: String, slotNum: Int, startAmount: Double) {

        currentActiveSlotId = slotNum

        playerProfile = playerProfile.copy(
            gameName = newGameName + "",
            slotName1 = if(slotNum == 0) newGameName else "asdf",
            slotName2 = if(slotNum == 1) newGameName else "asdf2",
            slotName3 = if(slotNum == 2) newGameName else "asdf3",
            balance = startAmount

        )

        viewModelScope.launch(Dispatchers.IO) {
            val newSave = GameSaveSlot(
                slotNumber = slotNum,
                balance = startAmount,
                currentDay = 1,
                isOccupied = true,
                name = newGameName + ""
            )

            gameSlotDao.insertSlot(newSave)
        }
    }

    // Keep track of which slot the player is currently using
    var currentActiveSlotId by mutableIntStateOf(1)
        private set

    // 1. Grab the live data from the database and turn it into a StateFlow
    val saveSlots = gameSlotDao.getAllSlots()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // Start with an empty list while loading
        )

    fun loadGame(slotId: Int) {
        currentActiveSlotId = slotId // Remember the slot!

        viewModelScope.launch(Dispatchers.IO) {
            val savedGame = gameSlotDao.getSlotById(slotId)
            if (savedGame != null) {
                // Update your RAM with the database data!
                playerProfile = playerProfile.copy(
                    gameName = savedGame.name,
                    balance = savedGame.balance
                )
            }
        }
    }

    fun saveProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSave = GameSaveSlot(
                slotNumber = currentActiveSlotId, // Save to the correct slot!
                balance = playerProfile.balance,
                currentDay = 1, // Update this if you track days!
                isOccupied = true,
                name = if(currentActiveSlotId == 0) playerProfile.slotName1 else if(currentActiveSlotId == 1) playerProfile.slotName2 else if(currentActiveSlotId == 2) playerProfile.slotName3 else "???"
            )
            // insertSlot with REPLACE acts as an update
            gameSlotDao.insertSlot(updatedSave)
        }
    }

    // Hardcoded list of markets for now
    var markets by mutableStateOf(
        listOf(
            WeatherMarket(1, "Seattle", "Rain", 1.2),
            WeatherMarket(2, "Phoenix", "Over 100°F", 1.8),
            WeatherMarket(3, "Chicago", "Snow", 3.5)
        )
    )
        private set

    // Game Logic: Handle investing in a market
    fun investInMarket(marketId: Int, amount: Double) {
        if (playerProfile.balance >= amount) {
            // Deduct the money from the player's balance
            val updatedBalance = playerProfile.balance - amount
            playerProfile = playerProfile.copy(balance = updatedBalance)

            saveProgress()
            println("Invested $amount in market $marketId. New balance: $updatedBalance")
        }
    }

    // DEBUG: Clear all saves instantly
    fun clearAllSavesForTesting() {
        viewModelScope.launch(Dispatchers.IO) {
            gameSlotDao.deleteAllSlots()

            // Optional: Reset the active RAM profile just to be safe
            playerProfile = playerProfile.copy(
                username = "Empty Slot",
                balance = 0.0
            )
        }
    }
}
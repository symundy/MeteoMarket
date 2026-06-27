package com.example.newapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.newapplication.data.models.PlayerProfile
import com.example.newapplication.data.models.WeatherMarket

class MarketViewModel : ViewModel() {

    // Mutable state inside the ViewModel, exposed as read-only to the UI
    var playerProfile by mutableStateOf(PlayerProfile(username = "WeatherWhiz", balance = 1500.0))
        private set

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


            println("Invested $amount in market $marketId. New balance: $updatedBalance")
        }
    }
}
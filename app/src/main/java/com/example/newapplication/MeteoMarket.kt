package com.example.newapplication

import androidx.compose.material3.MaterialTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.newapplication.ui.dashboard.*
import com.example.newapplication.ui.mainmenu.MainMenuScreen
import com.example.newapplication.data.local.GameSaveSlot
import com.example.newapplication.ui.theme.NewApplicationTheme

enum class AppScreen {
    MainMenu,
    Dashboard
}
class MeteoMarket : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NewApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // 1. Track which screen is currently visible
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MainMenu) }

                    // 2. Dummy data for the 3 slots (until we hook up live DB fetching)
                    val dummySlots = listOf(
                        GameSaveSlot(slotNumber = 1, balance = 1250.0, currentDay = 4, isOccupied = true),
                        GameSaveSlot(slotNumber = 2, balance = 1000.0, currentDay = 1, isOccupied = false),
                        GameSaveSlot(slotNumber = 3, balance = 1000.0, currentDay = 1, isOccupied = false)
                    )

                    // 3. Navigation Switchboard
                    when (currentScreen) {
                        AppScreen.MainMenu -> {
                            MainMenuScreen(
                                username = "WeatherWhiz",
                                saveSlots = dummySlots,
                                onSlotSelected = { slotNumber, isOccupied ->
                                    // When a slot is clicked, transition to the game screen!
                                    currentScreen = AppScreen.Dashboard
                                }
                            )
                        }
                        AppScreen.Dashboard -> {
                            // Displays the main stock market view we built earlier
                            MarketDashboard()
                        }
                    }

                }
            }
        }
    }
}
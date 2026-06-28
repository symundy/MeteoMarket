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
import com.example.newapplication.ui.mainmenu.NewGameScreen
import com.example.newapplication.ui.theme.NewApplicationTheme
import androidx.activity.viewModels
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newapplication.data.local.MeteoMarketDatabase
import com.example.newapplication.viewmodel.MarketViewModel



enum class AppScreen {
    MainMenu,
    NewGame,
    Dashboard
}
class MeteoMarket : ComponentActivity() {

    private val marketViewModel: MarketViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MarketViewModel::class.java)) {
                    val database = MeteoMarketDatabase.getDatabase(applicationContext)
                    @Suppress("UNCHECKED_CAST")
                    return MarketViewModel(database.gameSlotDao()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            NewApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // 1. Track which screen is currently visible
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MainMenu) }
                    var slotSelected by rememberSaveable { mutableIntStateOf(-1) }

                    val currentUsername = marketViewModel.playerProfile.username

                    // 2. Dummy data for the 3 slots (until we hook up live DB fetching)
//                    val dummySlots = listOf(
//                        GameSaveSlot(slotNumber = 1, name = "Game 1", balance = 1250.0, currentDay = 4, isOccupied = false),
//                        GameSaveSlot(slotNumber = 2, name = "Game 2",  balance = 1000.0, currentDay = 1, isOccupied = false),
//                        GameSaveSlot(slotNumber = 3, name = "Game 3",  balance = 1000.0, currentDay = 1, isOccupied = false)
//                    )

                    val realDatabaseSlots by marketViewModel.saveSlots.collectAsState()

                    // 3. Navigation Switchboard
                    when (currentScreen) {
                        AppScreen.MainMenu -> {

                            MainMenuScreen(
                                username = currentUsername,
                                saveSlots = realDatabaseSlots,
                                onSlotSelected = { slotNumber, isOccupied ->
                                    slotSelected = slotNumber
                                    if (isOccupied) {
                                        // 🎯 THE MISSING LINK: Actually load the game!
                                        marketViewModel.loadGame(slotId = slotNumber)

                                        // Then go to the dashboard
                                        currentScreen = AppScreen.Dashboard
                                    } else {
                                        // New Game (goes to your NewGameScreen)
                                        currentScreen = AppScreen.NewGame
                                    }
                                }
                            )
                            Button(
                                onClick = { marketViewModel.clearAllSavesForTesting() },
                                // Optional: make it red so you know it's a dangerous debug button!
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("DEBUG: Wipe All Saves")
                            }
                        }
                        AppScreen.NewGame -> {

                            NewGameScreen(
                                saveSlot = slotSelected,
                                onBegin = { startMoney, gameTitle, begin ->
                                    //GameSaveSlot(slotNumber = slotSelected, name = gameTitle, balance = startMoney, currentDay = 1, isOccupied = begin)
                                    marketViewModel.startNewGame(
                                        newGameName = gameTitle, slotNum = slotSelected,
                                        startAmount = startMoney,
                                    )
                                    currentScreen = if(begin) AppScreen.Dashboard
                                    else AppScreen.NewGame
                                }
                            )

                        }
                        AppScreen.Dashboard -> {
                            // Displays the main stock market view
                            MarketDashboard(viewModel = marketViewModel)
                        }
                    }

                }
            }
        }
    }
}
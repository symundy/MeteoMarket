package com.example.newapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newapplication.data.local.MeteoMarketDatabase
import com.example.newapplication.ui.currentbets.CurrentBets
import com.example.newapplication.ui.dashboard.MarketDashboard
import com.example.newapplication.ui.mainmenu.MainMenuScreen
import com.example.newapplication.ui.mainmenu.NewGameScreen
import com.example.newapplication.ui.theme.NewApplicationTheme
import com.example.newapplication.viewmodel.MarketViewModel

enum class AppScreen {
    MainMenu, NewGame, Dashboard, CurrentBets, MarketDetails
}

class MeteoMarket : ComponentActivity() {

    private val marketViewModel: MarketViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                if (modelClass.isAssignableFrom(MarketViewModel::class.java)) {
                    val sharedPreferences = getSharedPreferences("meteo_market_prefs", MODE_PRIVATE)
                    @Suppress("UNCHECKED_CAST")
                    MarketViewModel(MeteoMarketDatabase.getDatabase(applicationContext).gameSlotDao(), sharedPreferences) as T
                } else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MainMenu) }
                    var slotSelected by rememberSaveable { mutableIntStateOf(-1) }
                    val currentUsername = marketViewModel.playerProfile.username
                    val realDatabaseSlots by marketViewModel.saveSlots.collectAsState()

                    when (currentScreen) {
                        AppScreen.MainMenu -> {
                            MainMenuScreen(
                                username = currentUsername,
                                saveSlots = realDatabaseSlots,
                                onSlotSelected = { slotNumber, isOccupied ->
                                    slotSelected = slotNumber
                                    if (isOccupied) {
                                        marketViewModel.loadGame(slotId = slotNumber)
                                        currentScreen = AppScreen.Dashboard
                                    } else {
                                        currentScreen = AppScreen.NewGame
                                    }
                                }
                            )
                            Button(
                                onClick = { marketViewModel.clearAllSavesForTesting() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("DEBUG: Wipe All Saves")
                            }
                        }
                        AppScreen.NewGame -> {
                            NewGameScreen(
                                saveSlot = slotSelected,
                                onBegin = { startMoney, gameTitle, begin ->
                                    marketViewModel.startNewGame(
                                        newGameName = gameTitle,
                                        slotNum = slotSelected,
                                        startAmount = startMoney
                                    )
                                    currentScreen = if (begin) AppScreen.Dashboard else AppScreen.NewGame
                                }
                            )
                        }
                        AppScreen.Dashboard -> {
                            MarketDashboard(viewModel = marketViewModel)
                            if (marketViewModel.currentScreen == AppScreen.CurrentBets) {
                                currentScreen = AppScreen.CurrentBets
                            }
                        }
                        AppScreen.CurrentBets -> {
                            CurrentBets(playerProfile = marketViewModel.playerProfile)
                        }
                        AppScreen.MarketDetails -> {
                            // To be implemented
                        }
                    }
                }
            }
        }
    }
}
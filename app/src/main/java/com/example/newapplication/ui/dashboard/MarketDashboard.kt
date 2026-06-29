package com.example.newapplication.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapplication.ui.currentbets.combineSimilarBets
import com.example.newapplication.viewmodel.*

// needs to have a way to know when to switch to the CurrentBets view
@Composable
fun MarketDashboard(viewModel: MarketViewModel) {
    // Read the current state directly from the ViewModel
    val player = viewModel.playerProfile
    val markets = viewModel.markets

    val currentSlots = viewModel.availableLiveSlots

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- HEADER ---

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Hello, ${player.username}", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(text = "$${player.balance}", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(player.gameName)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Available Markets", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        // Adds a button to show the current bets screen
        Text(
            text = "View Current Bets",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
             .padding(vertical = 8.dp)
             .clickable {
                 val combinedInvestments = combineSimilarBets(viewModel.playerProfile.investments)
                 // Navigate to CurrentBets screen with combinedInvestments

                 viewModel.showCurrentBetsScreen(combinedInvestments)

         }
        )

        // --- DEV BUTTON ---
        Text(
           text = "🧪 Reload Markets (Dev)",
           fontSize = 14.sp,
           color = MaterialTheme.colorScheme.tertiary,
           modifier = Modifier
               .padding(vertical = 8.dp)
               .clickable {
                   viewModel.forceReloadWeatherForTesting()
               }
        )


        // --- LIST OF MARKETS ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // If the list is empty (API is still loading), maybe show a loading text here!
            // Loading text:
            if (currentSlots.isEmpty()) {
                item {
                    Text(
                        text = "Loading markets...",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(currentSlots) { market ->
                MarketCard(
                    market = market,
                    onInvestClick = {
                        // Pass the cityName instead of an ID
                        viewModel.investInMarket(market.cityName, 10.0)
                    }
                )
            }
        }
    }
}
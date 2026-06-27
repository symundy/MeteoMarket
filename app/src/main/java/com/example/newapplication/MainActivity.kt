package com.example.newapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface

// Represents the user playing the game
data class PlayerProfile(
    val username: String,
    val balance: Double // Using Double to handle cents/decimals in their balance
)

// Represents a specific city's weather event players can invest in
data class WeatherMarket(
    val id: Int,
    val cityName: String,
    val targetCondition: String, // e.g., "Rain", "Over 90°F", "Snow"
    val payoutOdds: Double       // e.g., 1.5x payout if the prediction is correct
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply your app's Material theme wrapper
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    MarketDashboard()
                }
            }
        }
    }
}



@Composable
fun MarketDashboard() {
    // Hardcoded Dummy Data
    val player = PlayerProfile(username = "WeatherWhiz", balance = 1500.0)
    val markets = listOf(
        WeatherMarket(1, "Seattle", "Rain", 1.2),
        WeatherMarket(2, "Phoenix", "Over 100°F", 1.8),
        WeatherMarket(3, "Chicago", "Snow", 3.5)
    )

    // Main layout container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- HEADER: Player Profile & Balance ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hello, ${player.username}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$${player.balance}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Available Markets",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- BODY: Scrollable List of Markets ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Iterates through our list of dummy markets
            items(markets) { market ->
                MarketCard(market)
            }
        }
    }
}

// A reusable UI component for an individual city's market
@Composable
fun MarketCard(market: WeatherMarket) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // City Name and Condition on the left
            Column {
                Text(
                    text = market.cityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Bet: ${market.targetCondition}",
                    fontSize = 14.sp
                )
            }

            // Odds on the right
            Text(
                text = "Odds: ${market.payoutOdds}x",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
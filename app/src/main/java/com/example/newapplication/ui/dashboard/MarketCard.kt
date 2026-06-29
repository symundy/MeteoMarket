package com.example.newapplication.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapplication.viewmodel.*
@Composable
fun MarketCard(market: LiveGameSlot, onInvestClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Added weight(1f) to keep text from crushing the button
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(text = "${market.cityName}, ${market.stateName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                // Show the bet type (weather keyword) with hours until forecast
                Text(text = "Bet: ${market.betType} in ${market.hoursUntilForecast} hours", fontSize = 14.sp)
                // Show the multiplier (higher for rarer events)
                Text(text = "Odds: ${market.riskMultiplier}x", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }

            // The Action Button
            Button(onClick = onInvestClick) {
                Text("Invest $10")
            }
        }
    }
}
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
import com.example.newapplication.data.models.WeatherMarket
@Composable
fun MarketCard(market: WeatherMarket, onInvestClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = market.cityName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Bet: ${market.targetCondition}", fontSize = 14.sp)
                Text(text = "Odds: ${market.payoutOdds}x", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }

            // The Action Button
            Button(onClick = onInvestClick) {
                Text("Invest $10")
            }
        }
    }
}
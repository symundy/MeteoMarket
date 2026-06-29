package com.example.newapplication.ui.currentbets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapplication.data.models.Investment
import com.example.newapplication.data.models.PlayerProfile
import java.util.Locale

// Displays current bets for a player in a scrollable list using Compose
@Composable
fun CurrentBets(playerProfile: PlayerProfile) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Current Bets for ${playerProfile.username}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (playerProfile.investments.isEmpty()) {
            Text(text = "No current bets.", fontSize = 16.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(playerProfile.investments) { investment ->
                    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = investment.cityName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", investment.amount)}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

fun combineSimilarBets(investments: List<Investment>): List<Investment> =
    investments
        .groupBy { it.cityName }
        .map { (cityName, group) -> Investment(cityName, group.sumOf { it.amount }) }
package com.example.newapplication.ui.mainmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NewGameScreen(
    saveSlot: Int,
    onBegin: (startAmount: Double, gameTitle: String, begin: Boolean) -> Unit
) {

    var startMoney by rememberSaveable { mutableDoubleStateOf(0.0) }
    var amountChosen by rememberSaveable { mutableIntStateOf(-1) }
    var gameTitle by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "New Game",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Slot $saveSlot",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(52.dp))
        Text(
            text = "Start Amount",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(Modifier.height(12.dp))
        Row(
            //horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            (0..3).forEach { index ->
                Button(
                    // Set the start amount
                    onClick = {
                        startMoney = (index + 1) * 1000.0
                        amountChosen = index
                    },
                    // Highlight the currently selected amount
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (amountChosen == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "${index + 1}k",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 16.sp,
                        color = if (amountChosen == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(52.dp))
        Text (
            text = "Set Name",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        // Name input
        OutlinedTextField(
            value = gameTitle,
            onValueChange = { gameTitle = it },
            label = { Text("Enter game title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        // Begin button
        Button(

            onClick = {onBegin(startMoney, gameTitle, true)},
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                "Begin",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
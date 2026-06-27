package com.example.newapplication.ui.mainmenu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapplication.data.local.GameSaveSlot


enum class ScreenState {
    CreateProfile,
    MainMenu,
    Dashboard
}
@Composable
fun MainMenuScreen(
    username: String,
    saveSlots: List<GameSaveSlot>, // Expecting a list of exactly 3 slots from your database
    onSlotSelected: (slotNumber: Int, isOccupied: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- WELCOME HEADER ---
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "MeteoMarket",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Welcome back, $username",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Select a Save Slot",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- SAVE SLOTS LIST ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Generate the slots dynamically based on the 1 to 3 index
            items(3) { index ->
                val slotNumber = index + 1
                // Find if we have database data for this slot, or create a default empty state
                val slotData = saveSlots.find { it.slotNumber == slotNumber }
                    ?: GameSaveSlot(slotNumber = slotNumber, balance = 1000.0, currentDay = 1, isOccupied = false)

                SaveSlotCard(
                    slot = slotData,
                    onClick = { onSlotSelected(slotData.slotNumber, slotData.isOccupied) }
                )
            }
        }
    }
}

@Composable
fun SaveSlotCard(
    slot: GameSaveSlot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Slot details
            Column {
                Text(
                    text = "Slot ${slot.slotNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (slot.isOccupied) {
                    Text(text = "Day: ${slot.currentDay}", fontSize = 14.sp)
                    Text(
                        text = "Balance: $${slot.balance}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Empty Slot",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right side: Contextual Action Button
            Button(
                onClick = onClick,
                colors = if (slot.isOccupied) {
                    ButtonDefaults.buttonColors() // Default primary colors for loading
                } else {
                    ButtonDefaults.filledTonalButtonColors() // Lighter color tone for "New Game"
                }
            ) {
                Text(text = if (slot.isOccupied) "Load Game" else "New Game")
            }
        }
    }
}
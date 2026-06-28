package com.example.newapplication.data.models

// Represents the user playing the game
data class PlayerProfile(
    val username: String,
    val balance: Double, // Using Double to handle cents/decimals in their balance
    val gameName: String,
    val slotName1: String,
    val slotName2: String,
    val slotName3: String
)
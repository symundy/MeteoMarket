package com.example.newapplication.data.models

// Represents the user playing the game
data class PlayerProfile(
    val username: String,
    val balance: Double // Using Double to handle cents/decimals in their balance
)
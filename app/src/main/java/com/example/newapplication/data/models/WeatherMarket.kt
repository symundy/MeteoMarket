package com.example.newapplication.data.models

// Represents a specific city's weather event players can invest in
data class WeatherMarket(
    val id: Int,
    val cityName: String,
    val targetCondition: String, // e.g., "Rain", "Over 90°F", "Snow"
    val payoutOdds: Double       // e.g., 1.5x payout if the prediction is correct
)
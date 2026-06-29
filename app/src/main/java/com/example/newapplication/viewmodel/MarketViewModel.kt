package com.example.newapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.SharedPreferences
import com.example.newapplication.AppScreen
import com.example.newapplication.data.local.GameSaveSlot
import com.example.newapplication.data.local.GameSlotDao
import com.example.newapplication.data.models.PlayerProfile
import com.example.newapplication.data.models.WeatherMarket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.newapplication.data.local.eligibleCities
import com.example.newapplication.data.models.Investment
import com.example.newapplication.services.*
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Serializable
data class LiveGameSlot(
    val cityName: String,
    val stateName: String,  // Added state name for clarity
    val betType: String,  // e.g. "Thunderstorm", "Snow", "Hail", etc.
    val riskMultiplier: Double,
    val hoursUntilForecast: Int  // Hours until this weather is forecasted
)

class MarketViewModel(private val gameSlotDao: GameSlotDao, private val sharedPreferences: SharedPreferences) : ViewModel() {

    // ...existing state variables...
    var playerProfile by mutableStateOf(PlayerProfile(username = "Snowbody", balance = 1500.0, "current game","Slot 1", "Slot 2", "Slot 3"))
        private set

    var availableLiveSlots by mutableStateOf<List<LiveGameSlot>>(emptyList())
        private set

    var currentScreen by mutableStateOf(AppScreen.MainMenu)
        private set

    var currentActiveSlotId by mutableIntStateOf(1)
        private set

    var markets by mutableStateOf(
        listOf(
            WeatherMarket(1, "Seattle", "Rain", 1.2),
            WeatherMarket(2, "Phoenix", "Over 100°F", 1.8),
            WeatherMarket(3, "Chicago", "Snow", 3.5)
        )
    )
        private set

    // Track the last time weather slots were loaded (persisted in SharedPreferences)
    private val LAST_WEATHER_LOAD_KEY = "last_weather_load_time"
    private val CACHED_WEATHER_SLOTS_KEY = "cached_weather_slots"
    private var lastWeatherLoadTime: Long
        get() = sharedPreferences.getLong(LAST_WEATHER_LOAD_KEY, 0L)
        set(value) = sharedPreferences.edit().putLong(LAST_WEATHER_LOAD_KEY, value).apply()

    private fun saveWeatherSlots(slots: List<LiveGameSlot>) {
        val json = Json.encodeToString(slots)
        sharedPreferences.edit().putString(CACHED_WEATHER_SLOTS_KEY, json).apply()
    }

    private fun loadCachedWeatherSlots(): List<LiveGameSlot> {
        val json = sharedPreferences.getString(CACHED_WEATHER_SLOTS_KEY, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    val saveSlots = gameSlotDao.getAllSlots()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Restore cached weather slots immediately on app startup
        availableLiveSlots = loadCachedWeatherSlots()
        // If no cached data, use demo markets immediately
        if (availableLiveSlots.isEmpty()) {
            availableLiveSlots = listOf(
                // add state names to these demo markets for clarity
                LiveGameSlot("Seattle", "Washington","Rain", 1.1, 6),
                LiveGameSlot("Denver", "Washington", "Snow", 2.0, 12),
                LiveGameSlot("Miami", "Washington", "Thunderstorm", 2.5, 3),
                LiveGameSlot("Chicago", "Washington", "Blizzard", 2.3, 18),
                LiveGameSlot("Phoenix", "Washington", "Wind", 1.4, 9)
            )
            println("🎮 [INIT] Using demo markets - no cached data")
        }
        // Only refresh from API if it's been 24+ hours
        loadWeatherSlotsIfScheduled()
    }

    // ...existing startNewGame, loadGame, saveProgress functions...

    fun startNewGame(newGameName: String, slotNum: Int, startAmount: Double) {
        currentActiveSlotId = slotNum
        playerProfile = playerProfile.copy(
            gameName = newGameName,
            slotName1 = if(slotNum == 0) newGameName else "asdf",
            slotName2 = if(slotNum == 1) newGameName else "asdf2",
            slotName3 = if(slotNum == 2) newGameName else "asdf3",
            balance = startAmount
        )
        viewModelScope.launch(Dispatchers.IO) {
            gameSlotDao.insertSlot(
                GameSaveSlot(
                    slotNumber = slotNum,
                    balance = startAmount,
                    currentDay = 1,
                    isOccupied = true,
                    name = newGameName
                )
            )
        }
    }

    fun loadGame(slotId: Int) {
        currentActiveSlotId = slotId
        viewModelScope.launch(Dispatchers.IO) {
            val savedGame = gameSlotDao.getSlotById(slotId)
            if (savedGame != null) {
                playerProfile = playerProfile.copy(
                    gameName = savedGame.name,
                    balance = savedGame.balance
                )
            }
        }
    }

    fun saveProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            val slotName = when(currentActiveSlotId) {
                0 -> playerProfile.slotName1
                1 -> playerProfile.slotName2
                2 -> playerProfile.slotName3
                else -> "???"
            }
            gameSlotDao.insertSlot(
                GameSaveSlot(
                    slotNumber = currentActiveSlotId,
                    balance = playerProfile.balance,
                    currentDay = 1,
                    isOccupied = true,
                    name = slotName
                )
            )
        }
    }

    private fun shouldLoadWeatherSlots(): Boolean {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isAfter215AM = currentHour > 2 || (currentHour == 2 && currentMinute >= 15)
        val daysSinceLastLoad = (now - lastWeatherLoadTime) / (1000 * 60 * 60 * 24)

        return lastWeatherLoadTime == 0L || (daysSinceLastLoad >= 1 && isAfter215AM)
    }

    fun loadWeatherSlotsIfScheduled() {
        if (shouldLoadWeatherSlots()) {
            loadWeatherSlots()
        }
    }

    fun loadWeatherSlots() {
        viewModelScope.launch(Dispatchers.IO) {
            val liveSlotsList = mutableListOf<LiveGameSlot>()
            val pool = eligibleCities.shuffled()
            var apiCallsAttempted = 0
            var apiCallsSucceeded = 0

            // Keep pulling from all 50 cities until we have 10 cards or exhaust the pool
            for (city in pool) {
                if (liveSlotsList.size >= 10) break

                try {
                    apiCallsAttempted++
                    val pointResponse = WeatherNetwork.retrofit.getPointData(city.lat, city.lon)
                    val forecastUrl = pointResponse.properties.forecast
                    val forecastResponse = WeatherNetwork.retrofit.getForecastFromUrl(forecastUrl)
                    val primaryPeriod = forecastResponse.properties.periods.firstOrNull()
                    val description = primaryPeriod?.detailedForecast ?: "Calm"
                    val shortForecast = primaryPeriod?.shortForecast ?: "Dynamic Conditions"

                    // gets the state name as well for clarity in the market card
                    val stateName = city.state

                    // copilot change this val to add 24 hours for every day before the day of this weather event
                    val hoursUntilForecast = if (primaryPeriod != null) {
                        try {
                            @Suppress("NewApi")
                            // Parse forecast time and compute difference in the forecast's timezone
                            val forecastTime = ZonedDateTime.parse(primaryPeriod.startTime)
                            val nowInForecastZone = ZonedDateTime.now(forecastTime.zone)

                            // Number of whole days between the two dates (based on date portion)
                            val daysBetween = ChronoUnit.DAYS.between(
                                nowInForecastZone.toLocalDate(),
                                forecastTime.toLocalDate()
                            ).toInt()

                            // Hours difference of the time-of-day portion only
                            val timeOfDayHours = ChronoUnit.HOURS.between(
                                nowInForecastZone.toLocalTime(),
                                forecastTime.toLocalTime()
                            ).toInt()

                            // Total hours = 24 * daysBetween + timeOfDayHours
                            val totalHours = daysBetween * 24 + timeOfDayHours

                            // If somehow negative (past event or parsing issues) clamp to 0
                            if (totalHours < 0) 0 else totalHours
                        } catch (e: Exception) {
                            0  // Fallback to 0 if parsing fails
                        }
                    } else {
                        0
                    }

                    apiCallsSucceeded++
                    println("✅ ${city.name}: $shortForecast (in $hoursUntilForecast hours)")

                    // Check for weather keywords and create separate cards for each
                    val betCards = mutableListOf<Pair<String, Double>>()  // betType to multiplier
                     
                    if (description.contains("Tornado", ignoreCase = true)) {
                        betCards.add("Tornado" to 4.0)  // Rarest = highest multiplier
                    }
                    if (description.contains("Hail", ignoreCase = true)) {
                        betCards.add("Hail" to 3.0)
                    }
                    if (description.contains("Thunderstorm", ignoreCase = true) || 
                        description.contains("Thunder", ignoreCase = true) ||
                        description.contains("T-Storm", ignoreCase = true)) {
                        betCards.add("Thunderstorm" to 2.5)
                    }
                    if (description.contains("Blizzard", ignoreCase = true)) {
                        betCards.add("Blizzard" to 2.3)
                    }
                    if (description.contains("Snow", ignoreCase = true)) {
                        betCards.add("Snow" to 2.0)
                    }
                    if (description.contains("Sleet", ignoreCase = true)) {
                        betCards.add("Sleet" to 1.9)
                    }
                    if (description.contains("Freezing", ignoreCase = true) || 
                        description.contains("Icy", ignoreCase = true)) {
                        betCards.add("Ice" to 1.8)
                    }
                    if ((description.contains("Strong wind", ignoreCase = true) ||
                        description.contains("High wind", ignoreCase = true) ||
                        description.contains("Severe wind", ignoreCase = true) ||
                        description.contains("Gale", ignoreCase = true) ||
                        description.contains("Gusty", ignoreCase = true) ||
                        description.contains("Wind gust", ignoreCase = true)) &&
                        !description.contains("Calm", ignoreCase = true)) {
                        betCards.add("Wind" to 1.4)
                    }
                    if (description.contains("Rain", ignoreCase = true) && 
                        !description.contains("No rain", ignoreCase = true)) {
                        betCards.add("Rain" to 1.1)  // Most common = lowest multiplier
                    }

                    // Add a card for each keyword found (up to 10 total markets)
                    for ((betType, multiplier) in betCards) {
                        if (liveSlotsList.size >= 10) break
                        liveSlotsList.add(
                            // adds state name as well as city name for clarity in the market card


                            LiveGameSlot(
                                cityName = city.name,
                                stateName = stateName,
                                betType = betType,
                                riskMultiplier = multiplier,
                                hoursUntilForecast = hoursUntilForecast
                            )
                        )
                        println("🎯 Card added: ${city.name} - $betType ($multiplier x) in $hoursUntilForecast hours")
                    }

                    if (betCards.isEmpty()) {
                        println("❌ ${city.name}: No exciting conditions - $shortForecast")
                    }
                } catch (e: Exception) {
                    println("🚨 Failed to gather weather for ${city.name}: ${e.message}")
                }
            }

            println("📊 API: $apiCallsSucceeded/$apiCallsAttempted calls, Found ${liveSlotsList.size} betting cards")

            withContext(Dispatchers.Main) {
                // If we got new data, use it. Otherwise, keep the cached data
                if (liveSlotsList.isNotEmpty()) {
                    availableLiveSlots = liveSlotsList
                    saveWeatherSlots(liveSlotsList)
                    lastWeatherLoadTime = System.currentTimeMillis()
                    println("✨ Markets loaded: ${liveSlotsList.size} cards")
                } else {
                    // API calls failed or no matches, fall back to cached slots
                    val cached = loadCachedWeatherSlots()
                    if (cached.isNotEmpty()) {
                        availableLiveSlots = cached
                        println("📦 Loaded ${cached.size} cards from cache")
                    } else {
                        // No cached data - use demo markets as fallback
                        val demoMarkets = listOf(
                            // add state names to these demo markets for clarity
                            LiveGameSlot("Seattle", "Washington","Rain", 1.1, 6),
                            LiveGameSlot("Denver", "Washington", "Snow", 2.0, 12),
                            LiveGameSlot("Miami", "Washington", "Thunderstorm", 2.5, 3),
                            LiveGameSlot("Chicago", "Washington", "Blizzard", 2.3, 18),
                            LiveGameSlot("Phoenix", "Washington", "Wind", 1.4, 9)
                        )
                        availableLiveSlots = demoMarkets
                        println("🎮 Using demo markets (no cache/API data)")
                    }
                    lastWeatherLoadTime = System.currentTimeMillis()
                }
            }
        }
    }

    fun investInMarket(cityName: String, amount: Double) {
        if (playerProfile.balance >= amount) {
            playerProfile = playerProfile.copy(balance = playerProfile.balance - amount)
            saveProgress()
            playerProfile = playerProfile.copy(
                investments = playerProfile.investments + Investment(cityName, amount)
            )
            println("Player invested $amount in $cityName!")
        }
    }

    fun forceReloadWeatherForTesting() {
        println("🧪 [DEV] Forcing weather reload...")
        lastWeatherLoadTime = 0L  // Reset timestamp to bypass 24-hour check
        loadWeatherSlots()
    }

    fun showCurrentBetsScreen(combinedInvestments: List<Investment>) {
        playerProfile = playerProfile.copy(investments = combinedInvestments)
        currentScreen = AppScreen.CurrentBets
    }

    fun clearAllSavesForTesting() {
        viewModelScope.launch(Dispatchers.IO) {
            gameSlotDao.deleteAllSlots()
            playerProfile = playerProfile.copy(username = "Empty Slot", balance = 0.0)
        }
    }
}
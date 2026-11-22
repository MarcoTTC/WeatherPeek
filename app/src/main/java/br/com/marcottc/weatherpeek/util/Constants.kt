package br.com.marcottc.weatherpeek.util

// The OpenWeather One Call app id is no longer kept in source: add "oneCallAppId=YOUR_APP_ID" to
// the (git ignored) local.properties file. It reaches the code as BuildConfig.ONE_CALL_APP_ID.
const val BASE_URL: String = "https://api.openweathermap.org/"

const val ROOM_DB_NAME: String = "WeatherPeek-Database"

const val sharedPreferencesDb = "WeatherSharedPreferences"
const val forceRefreshSettings = "FORCE_REFRESH_SETTINGS"

package br.com.marcottc.weatherpeek.util

// If this val is an empty string, you must access "https://openweathermap.org/api/one-call-4",
// register yourself an account, subscribe to the "One Call API" plan (One Call is not covered by
// the plain free plan that comes with a new account; keys without it get a 401), create an app id
// for your project, and add it in this val.
// Note: each weather refresh costs 5 billed calls in 4.0 (current, daily, and 3 hourly pages).
const val oneCallAppId: String = ""
const val BASE_URL: String = "https://api.openweathermap.org/"

const val ROOM_DB_NAME: String = "WeatherPeek-Database"

const val sharedPreferencesDb = "WeatherSharedPreferences"
const val forceRefreshSettings = "FORCE_REFRESH_SETTINGS"

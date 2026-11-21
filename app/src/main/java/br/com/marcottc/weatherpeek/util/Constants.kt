package br.com.marcottc.weatherpeek.util

// If this val is an empty string, you must access "https://openweathermap.org/api/one-call-3",
// register yourself an account, subscribe to the "One Call by Call" plan (One Call 3.0 is not
// covered by the plain free plan that comes with a new account), create an app id for your
// project, and add it in this val
const val oneCallAppId: String = ""
const val BASE_URL: String = "https://api.openweathermap.org/"

const val ROOM_DB_NAME: String = "WeatherPeek-Database"

const val sharedPreferencesDb = "WeatherSharedPreferences"
const val forceRefreshSettings = "FORCE_REFRESH_SETTINGS"

package br.com.marcottc.weatherpeek.util

import br.com.marcottc.weatherpeek.BuildConfig

// Provides the OpenWeather One Call app id, set through "oneCallAppId" in local.properties.
// Builds without it still succeed with an empty id, which isEmpty() reports so the UI can tell the
// user to set one up instead of sending requests bound to fail with a 401.
class AppKeyUtil(private val appKey: String = BuildConfig.ONE_CALL_APP_ID) {

    fun getAppKey(): String {
        return appKey
    }

    fun isEmpty(): Boolean {
        return appKey.isBlank()
    }
}

package br.com.marcottc.weatherpeek.util

class AppKeyUtil {

    fun getAppKey(): String {
        return oneCallAppId
    }

    fun isEmpty(): Boolean {
        return oneCallAppId.isEmpty()
    }
}
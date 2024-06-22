package br.com.marcottc.weatherpeek.util

import android.util.Log
import java.lang.Exception

class LoggerUtil() {

    fun e(tag: String, msg: String, exception: Exception) {
        Log.e(tag, msg, exception)
    }
}
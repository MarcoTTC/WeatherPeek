package br.com.marcottc.weatherpeek.util

import android.util.Log
import java.lang.Exception

class LoggerUtil(private val className: String) {

    fun e(msg: String, exception: Exception) {
        Log.e(className, msg, exception)
    }
}
package br.com.marcottc.weatherpeek.model.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Stores the One Call 4.0 alert id lists as a JSON array in a single TEXT column.
class WeatherPeekConverters {
    private val gson = Gson()
    private val stringListType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrBlank()) emptyList() else gson.fromJson(value, stringListType)
}

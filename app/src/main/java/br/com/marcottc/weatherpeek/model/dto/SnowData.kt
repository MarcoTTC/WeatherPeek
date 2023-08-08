package br.com.marcottc.weatherpeek.model.dto

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

// Snow related data (When available)
data class SnowData(
    @SerializedName("1h")
    @ColumnInfo(defaultValue = "0")
    var oneHour: Double
)
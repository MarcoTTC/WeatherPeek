package br.com.marcottc.weatherpeek.model.dto

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

// Rain related data (When available)
data class RainData(
    @SerializedName("1h")
    @ColumnInfo(defaultValue = "0")
    var oneHour: Double
)
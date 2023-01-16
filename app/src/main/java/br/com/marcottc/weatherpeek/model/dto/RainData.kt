package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Rain related data
data class RainData(
    @SerializedName("1h") var oneHour: Double
)
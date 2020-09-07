package br.com.marcottc.weatherpeek.model

import com.google.gson.annotations.SerializedName

data class RainData(
    @SerializedName("1h") var oneHour: Double
)
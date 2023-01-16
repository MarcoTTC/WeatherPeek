package br.com.marcottc.weatherpeek.model.dto

// Temperature related Data
data class TempData(
    var day: Double,
    var min: Double,
    var max: Double,
    var night: Double,
    var eve: Double,
    var morn: Double
)
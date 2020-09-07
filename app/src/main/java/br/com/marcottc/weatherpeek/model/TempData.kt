package br.com.marcottc.weatherpeek.model

data class TempData(
    var day: Double,
    var min: Double,
    var max: Double,
    var night: Double,
    var eve: Double,
    var morn: Double
)
package br.com.marcottc.weatherpeek.model.dco

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import kotlin.math.roundToInt

@Entity
data class DailyWeatherCache(
    @PrimaryKey
    var dt: Long,
    var minTemp: Double,
    var maxTemp: Double,
    var iconUrl: String,
    var uvi: Double,
    // One Call 4.0 daily visibility, in metres (max 10000)
    @ColumnInfo(defaultValue = "0")
    var visibility: Int = 0,
    // One Call 4.0 alert ids active during this day
    @ColumnInfo(defaultValue = "[]")
    var alertIds: List<String> = emptyList()
) {
    constructor(data: DailyWeatherData) : this(
        data.dt, data.temp.min, data.temp.max, data.weatherList[0].getIconUrl(), data.uvi,
        data.visibility.roundToInt(), data.alertIds ?: emptyList()
    )
}

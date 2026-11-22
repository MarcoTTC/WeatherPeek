package br.com.marcottc.weatherpeek.model.dco

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData

@Entity
data class HourlyWeatherCache(
    @PrimaryKey
    var dt: Long,
    var temp: Double,
    // One Call 4.0 alert ids active during this hour
    @ColumnInfo(defaultValue = "[]")
    var alertIds: List<String> = emptyList()
) {
    constructor(data: HourlyWeatherData) : this(
        data.dt, data.temp, data.alertIds ?: emptyList()
    )
}

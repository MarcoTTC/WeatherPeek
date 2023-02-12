package br.com.marcottc.weatherpeek.model.room

import android.content.Context
import androidx.room.Room

class RoomDatabaseInstance {

    companion object {
        private var database: WeatherPeekDatabase? = null

        fun getRoomInstance(applicationContext: Context): WeatherPeekDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    applicationContext,
                    WeatherPeekDatabase::class.java,
                    "WeatherPeek-Database"
                )
                .build()
            }
            return database as WeatherPeekDatabase
        }
    }
}
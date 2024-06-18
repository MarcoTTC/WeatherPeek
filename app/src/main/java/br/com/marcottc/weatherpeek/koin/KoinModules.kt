package br.com.marcottc.weatherpeek.koin

import br.com.marcottc.weatherpeek.model.room.RoomDatabaseInstance
import br.com.marcottc.weatherpeek.model.room.WeatherPeekDatabase
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single {
        RoomDatabaseInstance.getRoomInstance(androidApplication())
    }
    single {
        get<WeatherPeekDatabase>().getWeatherCacheDao()
    }
    single {
        get<WeatherPeekDatabase>().getCurrentWeatherDao()
    }
    single {
        get<WeatherPeekDatabase>().getHourlyWeatherCacheDao()
    }
    single {
        get<WeatherPeekDatabase>().getDailyWeatherCacheDao()
    }

    single {
        WeatherPeekRepository(
            get(),
            get(),
            get(),
            get()
        )
    }
}

val viewModelModule = module {
    includes(repositoryModule)

    viewModel {
        WeatherDataViewModel(
            androidApplication(),
            get()
        )
    }
}
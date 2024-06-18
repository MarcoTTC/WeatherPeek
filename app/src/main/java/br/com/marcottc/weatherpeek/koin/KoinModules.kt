package br.com.marcottc.weatherpeek.koin

import android.content.Context
import android.location.LocationManager
import br.com.marcottc.weatherpeek.model.room.RoomDatabaseInstance
import br.com.marcottc.weatherpeek.model.room.WeatherPeekDatabase
import br.com.marcottc.weatherpeek.network.RetrofitClientInstance
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.sharedPreferencesDb
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

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

val networkModule = module {
    single {
        RetrofitClientInstance.getRetrofitInstance()
    }
    single {
        get<Retrofit>().create(OneCallService::class.java)
    }
}

val contextModule = module {
    single {
        androidApplication().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    single {
        androidApplication().getSharedPreferences(
            sharedPreferencesDb,
            Context.MODE_PRIVATE
        )
    }
}

val viewModelModule = module {
    includes(repositoryModule)
    includes(networkModule)
    includes(contextModule)

    viewModel {
        WeatherDataViewModel(
            androidApplication(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
package br.com.marcottc.weatherpeek.koin

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.room.Room
import br.com.marcottc.weatherpeek.model.room.WeatherPeekDatabase
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.BASE_URL
import br.com.marcottc.weatherpeek.util.LoggerUtil
import br.com.marcottc.weatherpeek.util.NetworkUtil
import br.com.marcottc.weatherpeek.util.PermissionUtil
import br.com.marcottc.weatherpeek.util.ROOM_DB_NAME
import br.com.marcottc.weatherpeek.util.sharedPreferencesDb
import br.com.marcottc.weatherpeek.viewmodel.WeatherPeekViewModel
import br.com.marcottc.weatherpeek.worker.factory.WeatherPeekWorkerFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            WeatherPeekDatabase::class.java,
            ROOM_DB_NAME
        ).build()
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
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        AppKeyUtil()
    }

    single {
        get<Retrofit>().create(OneCallService::class.java)
    }
}

val contextModule = module {
    factory {
        androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    factory {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    factory {
        androidContext().getSharedPreferences(
            sharedPreferencesDb,
            Context.MODE_PRIVATE
        )
    }

    factory {
        androidContext().resources
    }

    factory {
        PermissionUtil(androidContext())
    }

    factory {
        NetworkUtil(get())
    }
}

val utilModule = module {
    single {
        LoggerUtil()
    }
}

val viewModelModule = module {
    includes(repositoryModule)
    includes(networkModule)
    includes(contextModule)
    includes(utilModule)

    viewModel {
        WeatherPeekViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            logger = get()
        )
    }
}

val workerFactoryModule = module {
    includes(repositoryModule)
    includes(networkModule)
    includes(utilModule)

    single {
        WeatherPeekWorkerFactory(
            get(),
            get(),
            get(),
            get()
        )
    }
}
package br.com.marcottc.weatherpeek

import android.app.Application
import androidx.work.*
import br.com.marcottc.weatherpeek.koin.contextModule
import br.com.marcottc.weatherpeek.koin.networkModule
import br.com.marcottc.weatherpeek.koin.repositoryModule
import br.com.marcottc.weatherpeek.koin.utilModule
import br.com.marcottc.weatherpeek.koin.viewModelModule
import br.com.marcottc.weatherpeek.koin.workerFactoryModule
import br.com.marcottc.weatherpeek.worker.UpdateWeatherCache
import br.com.marcottc.weatherpeek.worker.factory.WeatherPeekWorkerFactory
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WeatherApplication)
            modules(repositoryModule)
            modules(networkModule)
            modules(contextModule)
            modules(utilModule)
            modules(viewModelModule)
            modules(workerFactoryModule)
        }

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(getKoin().get<WeatherPeekWorkerFactory>())
                .build()
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val updateWorkRequest =
            PeriodicWorkRequest.Builder(UpdateWeatherCache::class.java, 8, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
                .build()

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(
            "UPDATE_WORKER",
            ExistingPeriodicWorkPolicy.UPDATE,
            updateWorkRequest
        )
    }
}
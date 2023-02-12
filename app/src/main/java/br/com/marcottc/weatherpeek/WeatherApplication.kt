package br.com.marcottc.weatherpeek

import android.app.Application
import androidx.work.*
import br.com.marcottc.weatherpeek.worker.UpdateWeatherCache
import java.util.concurrent.TimeUnit

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()

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
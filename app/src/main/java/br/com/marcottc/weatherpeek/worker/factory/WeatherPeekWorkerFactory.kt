package br.com.marcottc.weatherpeek.worker.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import br.com.marcottc.weatherpeek.network.datasource.OneCallRemoteDataSource
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.LoggerUtil
import br.com.marcottc.weatherpeek.worker.UpdateWeatherCache

class WeatherPeekWorkerFactory(
    private val appKeyUtil: AppKeyUtil,
    private val oneCallRemoteDataSource: OneCallRemoteDataSource,
    private val weatherPeekRepository: WeatherPeekRepository,
    private val logger: LoggerUtil
): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            UpdateWeatherCache::class.java.canonicalName -> {
                UpdateWeatherCache(
                    appContext,
                    workerParameters,
                    appKeyUtil,
                    oneCallRemoteDataSource,
                    weatherPeekRepository,
                    logger
                )
            }

            else ->
                null
        }
    }
}
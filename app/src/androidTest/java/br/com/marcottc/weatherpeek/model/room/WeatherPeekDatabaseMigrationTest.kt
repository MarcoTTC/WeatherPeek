package br.com.marcottc.weatherpeek.model.room

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Validates WeatherPeekDatabase's auto-migrations against the schemas exported to app/schemas
// (wired as androidTest assets in app/build.gradle.kts).
//
// Every migration is validated twice: by MigrationTestHelper, which compares the migrated tables
// with the exported schema, and by opening the migrated file through Room itself, which also checks
// the schema identity hash and reads the rows back through the DAOs and type converters.
@RunWith(AndroidJUnit4::class)
class WeatherPeekDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        WeatherPeekDatabase::class.java
    )

    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migrate4To5_keepsTheCachedRowsAndFillsTheNewColumnsWithDefaults() = runTest {
        helper.createDatabase(TEST_DB, 4).use { db ->
            db.execSQL(
                "INSERT INTO CurrentWeatherCache (dt, latitude, longitude, timezone, sunrise, " +
                        "sunset, temp, pressure, windSpeed, windDeg, windGust, rainAmount, " +
                        "snowAmount, humidity, clouds) VALUES (1718844339, 37.422, -122.084, " +
                        "'America/Los_Angeles', 1718801250, 1718854329, 20.37, 1011, 8.75, " +
                        "350.0, 12.35, 2.93, 1.17, 58, 40)"
            )
            db.execSQL("INSERT INTO HourlyWeatherCache (dt, temp) VALUES (1718845200, 21.55)")
            db.execSQL(
                "INSERT INTO DailyWeatherCache (dt, minTemp, maxTemp, iconUrl, uvi) VALUES " +
                        "(1718827200, 12.13, 25.24, 'http://openweathermap.org/img/wn/10d@2x.png', 8.29)"
            )
            db.execSQL(
                "INSERT INTO WeatherCache (id, main, description, icon) VALUES " +
                        "(500, 'Rain', 'chuva leve', '10d')"
            )
        }

        helper.runMigrationsAndValidate(TEST_DB, 5, true).use { db ->
            db.query("SELECT alertIds FROM CurrentWeatherCache").use { cursor ->
                cursor.moveToFirst()
                assertEquals("[]", cursor.getString(0))
            }
            db.query("SELECT alertIds FROM HourlyWeatherCache").use { cursor ->
                cursor.moveToFirst()
                assertEquals("[]", cursor.getString(0))
            }
            db.query("SELECT visibility, alertIds FROM DailyWeatherCache").use { cursor ->
                cursor.moveToFirst()
                assertEquals(0, cursor.getInt(0))
                assertEquals("[]", cursor.getString(1))
            }
        }

        openWithRoom().use { database ->
            val current = database.getCurrentWeatherDao().getValues()
            assertNotNull(current)
            assertEquals(1718844339L, current!!.dt)
            assertEquals(20.37, current.temp, 0.0001)
            assertEquals(12.35, current.windGust, 0.0001)
            assertEquals(1011, current.pressure)
            assertEquals(emptyList<String>(), current.alertIds)

            val hourly = database.getHourlyWeatherCacheDao().getAll()!!
            assertEquals(1, hourly.size)
            assertEquals(21.55, hourly[0].temp, 0.0001)
            assertEquals(emptyList<String>(), hourly[0].alertIds)

            val daily = database.getDailyWeatherCacheDao().getAll()
            assertEquals(1, daily.size)
            assertEquals(25.24, daily[0].maxTemp, 0.0001)
            assertEquals(0, daily[0].visibility)
            assertEquals(emptyList<String>(), daily[0].alertIds)
        }
    }

    @Test
    fun migrateAllVersions_fromTheFirstSchemaToTheLatest() = runTest {
        // A user still on the very first release, with the version 1 columns only
        helper.createDatabase(TEST_DB, 1).use { db ->
            db.execSQL(
                "INSERT INTO CurrentWeatherCache (dt, latitude, longitude, timezone, sunrise, " +
                        "sunset, temp, pressure, humidity, clouds) VALUES (1718844339, 37.422, " +
                        "-122.084, 'America/Los_Angeles', 1718801250, 1718854329, 20.37, 1011, 58, 40)"
            )
        }

        for (version in 2..LATEST_VERSION) {
            helper.runMigrationsAndValidate(TEST_DB, version, true).close()
        }

        openWithRoom().use { database ->
            val current = database.getCurrentWeatherDao().getValues()
            assertNotNull(current)
            assertEquals(1718844339L, current!!.dt)
            assertEquals(0.0, current.windSpeed, 0.0001)
            assertEquals(0.0, current.rainAmount, 0.0001)
            assertEquals(emptyList<String>(), current.alertIds)
        }
    }

    // Opening through Room fails if the migrated file does not match the current entities
    private fun openWithRoom(): WeatherPeekDatabase =
        Room.databaseBuilder(context, WeatherPeekDatabase::class.java, TEST_DB)
            .build()
            .also { it.openHelper.writableDatabase }

    private inline fun <R> WeatherPeekDatabase.use(block: (WeatherPeekDatabase) -> R): R =
        try {
            block(this)
        } finally {
            close()
        }

    companion object {
        private const val TEST_DB = "weatherpeek-migration-test"
        private const val LATEST_VERSION = 5
    }
}

package com.bignerdranch.android.a23_lab11

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.os.AsyncTask
import android.widget.ImageView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

const val apiKey = "24d60fdb1cbedcecdf3e1d08ae743218"
const val cityName = "London"
val userName = "aakihabara"
class MainActivity : AppCompatActivity() {

    private lateinit var weatherField: TextView
    private lateinit var sunriseField: TextView
    private lateinit var sunsetField: TextView
    private lateinit var humidityField: TextView
    private lateinit var tempField: TextView
    private lateinit var pressureField: TextView
    private lateinit var cityCountryField: TextView
    private lateinit var minTempField: TextView
    private lateinit var maxTempField: TextView
    private lateinit var windField: TextView
    private lateinit var updateTime: TextView
    private lateinit var creatorField: TextView
    private lateinit var sunsetImage: ImageView
    private lateinit var sunriseImage: ImageView
    private lateinit var windImage: ImageView
    private lateinit var pressureImage: ImageView
    private lateinit var humidImage: ImageView
    private lateinit var creatorImage: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$apiKey&units=metric"


        weatherField = findViewById(R.id.weather_forecast)
        sunriseField = findViewById(R.id.sunrise_time)
        sunsetField = findViewById(R.id.sunset_time)
        humidityField = findViewById(R.id.humid_value)
        tempField = findViewById(R.id.temp)
        pressureField = findViewById(R.id.pressure_value)
        cityCountryField = findViewById(R.id.location)
        minTempField = findViewById(R.id.temp_min)
        maxTempField = findViewById(R.id.temp_max)
        windField = findViewById(R.id.wind_value)
        updateTime = findViewById(R.id.update_time)
        creatorField = findViewById(R.id.created_user)

        sunsetImage = findViewById(R.id.sunset_image)
        sunsetImage.setColorFilter(Color.WHITE)

        sunriseImage = findViewById(R.id.sunrise_image)
        sunriseImage.setColorFilter(Color.WHITE)

        windImage = findViewById(R.id.wind_image)
        windImage.setColorFilter(Color.WHITE)

        pressureImage = findViewById(R.id.pressure_image)
        pressureImage.setColorFilter(Color.WHITE)

        humidImage = findViewById(R.id.humid_image)
        humidImage.setColorFilter(Color.WHITE)

        creatorImage = findViewById(R.id.created_image)
        creatorImage.setColorFilter(Color.WHITE)

        creatorField.text = userName

        FetchWeatherTask().execute(url)
    }

    private inner class FetchWeatherTask : AsyncTask<String, Void, String>() {

        //Выполнение подлючения не в главном потоке
        override fun doInBackground(vararg params: String): String {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            try {
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                bufferedReader.close()
                return stringBuilder.toString()
            } finally {
                connection.disconnect()
            }
        }

        //После успешного подключения начинается выборка данных
        override fun onPostExecute(result: String) {
            val jsonObject = JSONObject(result)
            val temp = jsonObject.getJSONObject("main").getDouble("temp")
            val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            val sunrise = jsonObject.getJSONObject("sys").getLong("sunrise")
            val sunset = jsonObject.getJSONObject("sys").getLong("sunset")
            val humidity = jsonObject.getJSONObject("main").getInt("humidity")
            val pressure = jsonObject.getJSONObject("main").getInt("pressure")
            val city = jsonObject.getString("name")
            val country = jsonObject.getJSONObject("sys").getString("country")
            val minTemperature = jsonObject.getJSONObject("main").getDouble("temp_min")
            val maxTemperature = jsonObject.getJSONObject("main").getDouble("temp_max")
            val windSpeed = jsonObject.getJSONObject("wind").getDouble("speed")
            val dateSunrise = Date(sunrise * 1000)
            val dateSunset = Date(sunset * 1000)
            val timeFormat = SimpleDateFormat("HH:mm a")
            timeFormat.timeZone = TimeZone.getDefault()
            val timeSunriseString = timeFormat.format(dateSunrise)
            val timeSunsetString = timeFormat.format(dateSunset)
            val tempRounded = String.format("%.1f", temp)
            val tempMinRounded = String.format("%.1f", minTemperature)
            val tempMaxRounded = String.format("%.1f", maxTemperature)
            tempField.text = "$tempRounded С\u00B0"
            minTempField.text = "$tempMinRounded С\u00B0"
            maxTempField.text = "$tempMaxRounded С\u00B0"
            weatherField.text = weather
            sunriseField.text = timeSunriseString
            sunsetField.text = timeSunsetString
            humidityField.text = humidity.toString()
            pressureField.text = pressure.toString()
            cityCountryField.text = "$city, $country"
            windField.text = windSpeed.toString()

            val updateTimeFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a")
            val timeNow = updateTimeFormat.format(Date())
            updateTime.text = "Updated at: $timeNow"

        }
    }
}
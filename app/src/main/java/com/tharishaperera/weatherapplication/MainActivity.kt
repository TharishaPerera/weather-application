package com.tharishaperera.weatherapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.tharishaperera.weatherapplication.databinding.ActivityMainBinding
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var txtSearch : EditText
    lateinit var btnGo : Button
    lateinit var lblDescription : TextView
    lateinit var imgWeatherImage : ImageView
    lateinit var lblCity : TextView
    lateinit var lblTemp : TextView
    lateinit var lblFeels : TextView
    lateinit var lblAirPressure : TextView
    lateinit var lblHumidity : TextView
    lateinit var lblWindSpeed : TextView
    lateinit var lblDate : TextView

    private lateinit var binding: ActivityMainBinding

    var city = "Colombo"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // to hide the top bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(binding.root)
        // setContentView(R.layout.activity_main)

        lblDescription = findViewById(R.id.lbl_description)
        lblCity = findViewById(R.id.lbl_city)
        lblTemp = findViewById(R.id.lbl_temp)
        lblFeels = findViewById(R.id.lbl_temp_feels)
        lblAirPressure = findViewById(R.id.lbl_air_pressure)
        lblHumidity = findViewById(R.id.lbl_humidity)
        lblWindSpeed = findViewById(R.id.lbl_wind_speed)
        lblDate = findViewById(R.id.lbl_date)
        txtSearch = findViewById(R.id.txt_city_search)
        btnGo = findViewById(R.id.btn_go)
        imgWeatherImage = findViewById(R.id.img_image)

        // Get current date
        val formatter = DateTimeFormatter.ofPattern("d EEEE, MMMM yyyy")
        lblDate.text = LocalDate.now().format(formatter).toString()

        downloadData()

        btnGo.setOnClickListener {
            val cityInput = txtSearch.getText()
            city = cityInput.toString()
            downloadData()
            txtSearch.text = null
            txtSearch.clearFocus()
        }
    }

    fun downloadData(){
        val apiKey = "1c53c4a7dfd07adebb24140fc247195f"

        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${apiKey}&units=metric"

        val request = JsonObjectRequest(url, JSONObject(), { response ->
            try {
                var city = response.getString("name")
                var country = response.getJSONObject("sys").getString("country")
                var temp = response.getJSONObject("main").getDouble("temp")
                var feels = response.getJSONObject("main").getDouble("feels_like")
                var icon = response.getJSONArray("weather").getJSONObject(0).getString("icon")

                val imgUrl = "http://openweathermap.org/img/wn/${icon}@4x.png"

                lblDescription.text = response.getJSONArray("weather").getJSONObject(0).getString("description")
                lblCity.text = "$city, $country"
                lblTemp.text = "${temp.toInt().toString()}°"
                lblFeels.text = "${feels.toInt().toString()}°"
                lblAirPressure.text = response.getJSONObject("main").getInt("pressure").toString() + " Pa"
                lblHumidity.text = response.getJSONObject("main").getInt("humidity").toString() + " %"
                lblWindSpeed.text = response.getJSONObject("wind").getInt("speed").toString() + " mph"

//                Glide.with(this)
//                    .load(imgUrl)
//                    .into(binding.imgImage)

            } catch (error: Exception) {

            }
        }, { error ->
            Log.e("Error: ", error.toString())
        })

        queue.add(request)

    }
}
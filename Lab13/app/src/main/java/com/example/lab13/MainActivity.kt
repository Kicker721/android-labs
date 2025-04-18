package com.example.lab13

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab13.Common.cities

class MainActivity : AppCompatActivity() {
    lateinit var btnSelectCity: Button
    lateinit var btnShowOnMap: Button
    lateinit var tvCity: TextView
    lateinit var tvCityRegion: TextView
    lateinit var tvCityDistrict: TextView
    lateinit var tvCityPostalCode: TextView
    lateinit var tvCityTimezone: TextView
    lateinit var tvCityPopulation: TextView
    lateinit var tvCityFounded: TextView

    var cityIndex: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnSelectCity = findViewById(R.id.btnSelectCity)
        btnShowOnMap = findViewById(R.id.btnShowOnMap)
        tvCity = findViewById(R.id.tvName)
        tvCityRegion = findViewById(R.id.tvRegion)
        tvCityDistrict = findViewById(R.id.tvFederalDistrict)
        tvCityPostalCode = findViewById(R.id.tvPostalCode)
        tvCityTimezone = findViewById(R.id.tvTimeZone)
        tvCityPopulation = findViewById(R.id.tvPopulation)
        tvCityFounded = findViewById(R.id.tvFounded)
        Common.initCities(this)
        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val index = result.data?.getIntExtra("city_index", -1) ?: -1
                    if (index > 0) {
                        val selectedCity = cities[index]
                        cityIndex = index
                        tvCity.text =
                            String.format(getString(R.string.city), selectedCity.title)
                        tvCityRegion.text =
                            String.format(getString(R.string.region), selectedCity.region)
                        tvCityDistrict.text = String.format(
                            getString(R.string.federal_district),
                            selectedCity.district
                        )
                        tvCityPostalCode.text =
                            String.format(getString(R.string.postal_code), selectedCity.postalCode)
                        tvCityTimezone.text =
                            String.format(getString(R.string.time_zone), selectedCity.timezone)
                        tvCityPopulation.text =
                            String.format(getString(R.string.population), selectedCity.population)
                        tvCityFounded.text =
                            String.format(getString(R.string.founded), selectedCity.founded)
                    } else {
                        Toast.makeText(this, "Неверный индекс города", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        btnSelectCity.setOnClickListener {
            val intent = Intent(this, SelectCityActivity::class.java)
            resultLauncher.launch(intent)
        }

        btnShowOnMap.setOnClickListener {
            if(cityIndex>0){
                val intent = Uri.parse("geo:"+cities[cityIndex].lat + cities[cityIndex].lon).let { location ->
                    Intent(Intent.ACTION_VIEW, location)
                }
                try {
                    startActivity(intent)
                }
                catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, getString(R.string.no_maps_app), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
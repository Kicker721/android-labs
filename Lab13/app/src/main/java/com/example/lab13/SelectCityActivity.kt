package com.example.lab13

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class SelectCityActivity : AppCompatActivity() {
    lateinit var rvCities: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_city)

        rvCities = findViewById(R.id.rvCities)
        val adapter = CitiesAdapter(Common.cities)
        adapter.setOnClickListener { selected ->
            val intent = intent
            intent.putExtra("city_index", selected)
            setResult(RESULT_OK, intent)
            finish()
        }
        rvCities.adapter = adapter
    }
}
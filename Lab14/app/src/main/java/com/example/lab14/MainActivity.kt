package com.example.lab14

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity(), CitySelectionListener {
    private var isHorizontal = false
    private var selectedPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isHorizontal = findViewById<View>(R.id.detail_container) != null
        selectedPos = savedInstanceState?.getInt("SEL_POS", 0) ?: 0

        Common.initCities(this)
        Log.i("MainActivity", "isHorizontal onCreate: $isHorizontal")
        Log.i("MainActivity", "savedInstanceState onCreate: ${savedInstanceState == null}")

        if (savedInstanceState == null) {
            if (isHorizontal) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.list_container, CityListFragment())
                    .replace(R.id.detail_container, CityInfoFragment.newInstance(0))
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CityListFragment())
                    .commit()
            }
        } else if (isHorizontal) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_container,
                    CityInfoFragment.newInstance(selectedPos))
                .commit()
        }
        else
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    CityInfoFragment.newInstance(selectedPos))
                .addToBackStack(null)
                .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SEL_POS", selectedPos)
    }



    override fun onCitySelected(position: Int) {
        selectedPos = position
        if (isHorizontal) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_container, CityInfoFragment.newInstance(position))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CityInfoFragment.newInstance(position))
                .addToBackStack(null)
                .commit()
        }
    }
}
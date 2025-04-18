package com.example.lab13

import android.content.Context

object Common {
    val cities: MutableList<City> = mutableListOf()
    fun initCities(ctx: Context) {
        if (cities.isEmpty()) {
            val lines =
                ctx.resources.openRawResource(R.raw.cities).bufferedReader().lines().toArray()
            for (i in 1 until lines.count()) {
                val parts = lines[i].toString().split(";")
                val city = City(
                    parts[3], // city
                    parts[2], // region
                    parts[1], // federal_district
                    parts[0], // postal_code
                    parts[4], // timezone
                    parts[7], // population
                    parts[8], // founded
                    parts[5].toFloat(), // lat
                    parts[6].toFloat() // lon
                )
                cities.add(city)
            }
            cities.sortBy { it.title }
        }
    }
}
package com.example.lab14

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


class CityInfoFragment : Fragment(R.layout.fragment_city_info) {
    companion object {
        private const val ARG_CITY_POSITION = "city_position"

        fun newInstance(position: Int): CityInfoFragment {
            val fragment = CityInfoFragment()
            val args = Bundle()
            args.putInt(ARG_CITY_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var btnShowOnMap: Button
    private lateinit var tvCity: TextView
    private lateinit var tvCityRegion: TextView
    private lateinit var tvCityDistrict: TextView
    private lateinit var tvCityPostalCode: TextView
    private lateinit var tvCityTimezone: TextView
    private lateinit var tvCityPopulation: TextView
    private lateinit var tvCityFounded: TextView
    private var position = -1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnShowOnMap = view.findViewById(R.id.btnShowOnMap)
        tvCity = view.findViewById(R.id.tvName)
        tvCityRegion = view.findViewById(R.id.tvRegion)
        tvCityDistrict = view.findViewById(R.id.tvFederalDistrict)
        tvCityPostalCode = view.findViewById(R.id.tvPostalCode)
        tvCityTimezone = view.findViewById(R.id.tvTimeZone)
        tvCityPopulation = view.findViewById(R.id.tvPopulation)
        tvCityFounded = view.findViewById(R.id.tvFounded)
        position = arguments?.getInt(ARG_CITY_POSITION, -1) ?: -1
        if (position != -1 && position < Common.cities.size) {
            displayCityInfo(Common.cities[position])
        }

        btnShowOnMap.setOnClickListener {
            if(position>0){
                val intent = Uri.parse("geo:${Common.cities[position].lat},${Common.cities[position].lon}").let { location ->
                    Intent(Intent.ACTION_VIEW, location)
                }
                try {
                    startActivity(intent)
                }
                catch (e: ActivityNotFoundException) {
                    Toast.makeText(this.context, getString(R.string.no_maps_app), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun displayCityInfo(selectedCity: City) {
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
    }
}
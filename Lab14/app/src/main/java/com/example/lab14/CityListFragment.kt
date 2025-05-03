package com.example.lab14

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CityListFragment : Fragment(R.layout.fragment_city_list) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitiesAdapter
    private var citySelectionListener: CitySelectionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CitySelectionListener) {
            citySelectionListener = context
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
        val state = layoutManager?.onSaveInstanceState()
        outState.putParcelable("LIST_STATE", state)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Common.initCities(requireContext())

        recyclerView = view.findViewById<RecyclerView>(R.id.rvCities)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CitiesAdapter(Common.cities)
        recyclerView.adapter = adapter

        adapter.setOnClickListener { position ->
            citySelectionListener?.onCitySelected(position)
        }


    }


}
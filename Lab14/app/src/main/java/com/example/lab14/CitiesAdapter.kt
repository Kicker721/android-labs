package com.example.lab14

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CitiesAdapter(val cities: List<City>) :
    RecyclerView.Adapter<CitiesAdapter.CityHolder>() {
    class CityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvRegion: TextView
        var llCityListItem: LinearLayout
        init{
            tvName = itemView.findViewById(R.id.tvCityName)
            tvRegion = itemView.findViewById(R.id.tvCityRegion)
            llCityListItem = itemView.findViewById(R.id.llCityListItem)
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnClickListener(f: (Int) -> Unit) {onItemClickListener = f}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.city_item, parent, false)
        val holder = CityHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: CityHolder, position: Int) {
        holder.tvName.text = cities[position].title
        holder.tvRegion.text = cities[position].region
        holder.llCityListItem.setOnClickListener { onItemClickListener?.invoke(position) }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

}
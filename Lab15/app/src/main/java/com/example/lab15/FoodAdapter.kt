package com.example.lab15

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(val food: List<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodHolder>() {
    class FoodHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvAmount: TextView
        var llCityListItem: LinearLayout
        init{
            tvName = itemView.findViewById(R.id.tvFoodName)
            tvAmount = itemView.findViewById(R.id.tvFoodAmount)
            llCityListItem = itemView.findViewById(R.id.llFoodItem)
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnClickListener(f: (Int) -> Unit) {onItemClickListener = f}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.food_item, parent, false)
        val holder = FoodHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        val context = holder.itemView.context
        holder.tvName.text = food[position].name
        holder.tvAmount.text = context.getString(R.string.amount_format, food[position].amount)
        holder.llCityListItem.setOnClickListener { onItemClickListener?.invoke(position) }
    }

    override fun getItemCount(): Int {
        return food.size
    }

}
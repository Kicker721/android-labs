package com.example.lab15

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), FoodDialog.FoodDialogListener {
    private val list = mutableListOf<Food>(
        Food("Яблоки", "1 кг"),
        Food("Бананы", "2 кг"),
        Food("Груши", "3 кг"),
        Food("Апельсины", "4 кг"),
        Food("Киви", "5 кг"),
        Food("Персики", "6 кг"),
        Food("Виноград", "7 кг")
    )
    private lateinit var adapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvFood = findViewById<RecyclerView>(R.id.rvFood)
        adapter = FoodAdapter(list)
        rvFood.adapter = adapter

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                list.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvFood)

        adapter.setOnClickListener { position ->
            showFoodDialog(position)
        }

        val fabAddFood = findViewById<FloatingActionButton>(R.id.fabAddFood)
        fabAddFood.setOnClickListener {
            showFoodDialog()
        }
    }

    private fun showFoodDialog(position: Int = -1) {
        val dialog = if (position >= 0) {
            val food = list[position]
            FoodDialog.newInstance(food.name, food.amount, position)
        } else {
            FoodDialog.newInstance()
        }

        dialog.show(supportFragmentManager, "FoodDialog")
    }

    override fun onDialogData(name: String, amount: String, position: Int) {
        if (name.isNotEmpty() && amount.isNotEmpty()) {
            if (position >= 0) {
                list[position] = Food(name, amount)
                adapter.notifyItemChanged(position)
            } else {
                list.add(Food(name, amount))
                adapter.notifyItemInserted(list.size-1)
            }

        }
    }
}

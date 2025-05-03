package com.example.lab15

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import android.widget.EditText

class FoodDialog : DialogFragment() {

    interface FoodDialogListener {
        fun onDialogData(name: String, amount: String, position: Int)
    }

    lateinit var listener: FoodDialogListener

    private var position: Int = -1
    private var foodName: String = ""
    private var foodAmount: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.food_dialog, null)

        val etFoodName = view.findViewById<EditText>(R.id.etFoodName)
        val etFoodAmount = view.findViewById<EditText>(R.id.etFoodAmount)

        etFoodName.setText(foodName)
        etFoodAmount.setText(foodAmount)

        builder.setView(view)
            .setTitle(if (position >= 0) "Редактировать продукт" else "Добавить продукт")
            .setPositiveButton("ОК") { _, _ ->
                listener.onDialogData(
                    etFoodName.text.toString(),
                    etFoodAmount.text.toString(),
                    position
                )
            }
            .setNegativeButton("Отмена", null)

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FoodDialogListener
    }

    companion object {
        fun newInstance(name: String = "", amount: String = "", position: Int = -1): FoodDialog {
            val dialog = FoodDialog()
            dialog.foodName = name
            dialog.foodAmount = amount
            dialog.position = position
            return dialog
        }
    }
}
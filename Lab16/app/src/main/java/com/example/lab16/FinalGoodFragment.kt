package com.example.lab16

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController


class FinalGoodFragment : Fragment(R.layout.fragment_final_good) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnOneMoreTime = view.findViewById<Button>(R.id.btn_one_more_time)

        btnOneMoreTime.setOnClickListener {
            findNavController().navigate(R.id.action_finalGoodFragment_to_introFragment)
        }
    }
}
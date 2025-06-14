package com.example.lab16

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController


class HareFragment : Fragment(R.layout.fragment_hare) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnStay = view.findViewById<Button>(R.id.btn_stay)
        val btnRun = view.findViewById<Button>(R.id.btn_run)

        btnStay.setOnClickListener {
            findNavController().navigate(R.id.action_hareFragment_to_finalBadFragment)
        }

        btnRun.setOnClickListener {
            findNavController().navigate(R.id.action_hareFragment_to_wolfFragment)
        }
    }
}
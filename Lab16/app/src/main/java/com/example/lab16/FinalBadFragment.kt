package com.example.lab16

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class FinalBadFragment : Fragment(R.layout.fragment_final_bad) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnTryAgain = view.findViewById<Button>(R.id.btn_try_again)

        btnTryAgain.setOnClickListener {
            findNavController().navigate(R.id.action_finalBadFragment_to_introFragment)
        }

    }
}
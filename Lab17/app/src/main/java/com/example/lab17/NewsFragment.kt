package com.example.lab17

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class NewsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        val clearButton = view.findViewById<Button>(R.id.clear_news_btn)
        clearButton.setOnClickListener {
            (activity as? MainActivity)?.resetNewsCounter()
        }
        return view
    }
}
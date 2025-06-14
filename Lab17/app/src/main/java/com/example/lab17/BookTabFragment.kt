package com.example.lab17

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class BookTabFragment : Fragment(R.layout.fragment_book_tab) {

    companion object {
        private const val ARG_TAB = "tab"

        fun newInstance(tab: String): BookTabFragment {
            val fragment = BookTabFragment()
            val args = Bundle()
            args.putString(ARG_TAB, tab)
            fragment.arguments = args
            return fragment
        }
    }

    private var tab: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tab = arguments?.getString(ARG_TAB)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.book_tab_text).text = tab

    }

}
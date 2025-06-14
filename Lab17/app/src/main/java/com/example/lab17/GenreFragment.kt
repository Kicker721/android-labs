package com.example.lab17

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GenreFragment : Fragment(R.layout.fragment_genre) {
    companion object {
        private const val ARG_GENRE = "genre"

        fun newInstance(genre: String): GenreFragment {
            val fragment = GenreFragment()
            val args = Bundle()
            args.putString(ARG_GENRE, genre)
            fragment.arguments = args
            return fragment
        }
    }

    private var genre: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre = arguments?.getString(ARG_GENRE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.genre_text)
        textView.text = getString(R.string.genre_is, genre)
    }


}
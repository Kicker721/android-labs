package com.example.lab17

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MusicPagerAdapter(fragment: Fragment, private val genres: List<String>)
    : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = genres.size

    override fun createFragment(position: Int): Fragment {
        return GenreFragment.newInstance(genres[position])
    }
}
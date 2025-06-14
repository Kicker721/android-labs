package com.example.lab17

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BooksPagerAdapter(fragment: Fragment, private val tabs: List<String>)
    : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return BookTabFragment.newInstance(tabs[position])
    }
}
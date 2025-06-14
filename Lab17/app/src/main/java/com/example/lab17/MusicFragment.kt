package com.example.lab17

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicFragment : Fragment(R.layout.fragment_music) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val genres = listOf(
        "Поп", "Рок", "Джаз", "Классика", "Электро", "Хип-хоп"
    )



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        viewPager.adapter = MusicPagerAdapter(this, genres)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = genres[position]
        }.attach()
    }
}
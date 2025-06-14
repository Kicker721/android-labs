package com.example.lab17

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BooksFragment : Fragment(R.layout.fragment_books) {

    private val bookTabs = listOf("Новое", "Прочитанное")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = view.findViewById<TabLayout>(R.id.books_tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.books_view_pager)

        viewPager.adapter = BooksPagerAdapter(this, bookTabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = bookTabs[position]
        }.attach()
    }

}
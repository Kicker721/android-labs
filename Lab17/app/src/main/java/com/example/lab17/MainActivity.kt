package com.example.lab17

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var unreadNewsCount = 0
    private var newsTimer: CountDownTimer? = null
    private var newsTabSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_music -> {
                    openFragment(MusicFragment())
                    newsTabSelected = false
                    startNewsTimer()
                    true
                }
                R.id.menu_books -> {
                    openFragment(BooksFragment())
                    newsTabSelected = false
                    startNewsTimer()
                    true
                }
                R.id.menu_news -> {
                    openFragment(NewsFragment())
                    newsTabSelected = true
                    stopNewsTimer()
                    hideNewsBadge()
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.menu_music
            newsTabSelected = false
            startNewsTimer()
        }
    }
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun startNewsTimer() {
        if (!newsTabSelected) {
            if (newsTimer == null) {
                newsTimer = object : CountDownTimer(60000, 2000) {
                    override fun onTick(millisUntilFinished: Long) {
                        unreadNewsCount++
                        showNewsBadge()
                    }

                    override fun onFinish() {}
                }
                newsTimer?.start()
            }
        }
    }

    private fun stopNewsTimer() {
        newsTimer?.cancel()
        newsTimer = null
    }

    private fun showNewsBadge() {
        val badge = bottomNavigationView.getOrCreateBadge(R.id.menu_news)
        badge.isVisible = true
        badge.number = unreadNewsCount
    }

    fun resetNewsCounter() {
        unreadNewsCount = 0
        hideNewsBadge()
        if (!newsTabSelected) startNewsTimer()
    }

    private fun hideNewsBadge() {
        val badge = bottomNavigationView.getBadge(R.id.menu_news)
        badge?.isVisible = false
    }
}
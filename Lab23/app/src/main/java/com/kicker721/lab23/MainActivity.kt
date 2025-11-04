package com.kicker721.lab23

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: EditText
    private val projects = mutableListOf<Project>()
    private lateinit var adapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.rvProjects)
        adapter = ProjectAdapter(projects)
        etSearch = findViewById(R.id.etSearch)
        recyclerView.adapter = adapter
        etSearch.setOnEditorActionListener { v, actionId, event ->
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                loadProjects(v.text.toString())
                withContext(Dispatchers.Main) {
                    Log.d("MainActivity", projects.toString())
                    adapter.notifyDataSetChanged()
                }
            }
            true
        }
    }

    private fun loadProjects(query: String) {
        val url = URL("https://api.github.com/search/repositories?q=$query")
        val con = url.openConnection() as HttpsURLConnection
        var result: String? = null
        try {
            val stream = BufferedInputStream(con.inputStream)
            result = stream.bufferedReader().use { it.readText() }
        } finally {
            con.disconnect()
        }
        Log.d("MainActivity", result ?: "null")
        val json = JSONObject(result)
        val projects = json.getJSONArray("items")
        for (i in 0 until 5) {
            val item = projects.getJSONObject(i)
            val name = item.getString("name")
            val description = item.getString("description")
            val language = item.getString("language")
            val url = item.getString("html_url")
            val owner = item.getJSONObject("owner")
            val authorUrl = owner.getString("html_url")
            val author = owner.getString("login")
            this.projects.add(Project(name, description, language, url, authorUrl, author))
        }


    }
}
package com.example.lab18

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var btnLoad: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CurrencyAdapter
    private val currencies = mutableListOf<Currency>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnLoad = findViewById(R.id.btnLoad)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = CurrencyAdapter(currencies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnLoad.setOnClickListener {
            btnLoad.isEnabled = false
            progressBar.visibility = View.VISIBLE
            currencies.clear()
            adapter.notifyDataSetChanged()

            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val result = try {
                    val xml = URL("https://www.cbr.ru/scripts/XML_daily.asp")
                        .readText(Charset.forName("Windows-1251"))
                    parseXml(xml)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnLoad.isEnabled = true
                    if (result != null) {
                        currencies.clear()
                        currencies.addAll(result)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@MainActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun parseXml(xml: String): List<Currency> {
        val result = mutableListOf<Currency>()
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(StringReader(xml))

        var eventType = parser.eventType
        var charCode = ""
        var name = ""
        var value = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "Valute") {
                charCode = ""
                name = ""
                value = ""
            } else if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "CharCode" -> charCode = parser.nextText()
                    "Name" -> name = parser.nextText()
                    "Value" -> value = parser.nextText()
                }
            } else if (eventType == XmlPullParser.END_TAG && parser.name == "Valute") {
                if (charCode.isNotEmpty() && name.isNotEmpty() && value.isNotEmpty()) {
                    result.add(Currency(charCode, name, value))
                }
            }
            eventType = parser.next()
        }
        return result
    }
}
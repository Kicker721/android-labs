package com.example.lab21

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etMinutes: EditText
    private lateinit var etSeconds: EditText
    private lateinit var btnStartStop: Button
    private lateinit var tvStatus: TextView
    private var isRunning = false

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("MainActivity", "Received intent: ${intent?.action}")
            when (intent?.action) {
                TimerService.ACTION_UPDATE -> {
                    val millisLeft = intent.getLongExtra(TimerService.EXTRA_TIME_LEFT, 0L)
                    updateTime(millisLeft)
                }
                TimerService.ACTION_FINISH -> {
                    toInitialState()
                }
                TimerService.ACTION_STARTED -> {
                    toRunningState()
                }
            }
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted)
            Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etMinutes = findViewById(R.id.etMinutes)
        etSeconds = findViewById(R.id.etSeconds)
        btnStartStop = findViewById(R.id.btnStartStop)
        tvStatus = findViewById(R.id.tvStatus)

        btnStartStop.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@setOnClickListener
                }
            }
            if (!isRunning) {
                val min = etMinutes.text.toString().toIntOrNull() ?: 0
                val sec = etSeconds.text.toString().toIntOrNull() ?: 0
                val totalMillis = (min * 60 + sec) * 1000L
                if (totalMillis > 0) {
                    TimerService.start(this, totalMillis)
                }
            } else {
                TimerService.stop(this)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(TimerService.ACTION_UPDATE)
            addAction(TimerService.ACTION_FINISH)
            addAction(TimerService.ACTION_STARTED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(timerReceiver, filter)
        }

        if (TimerService.isRunning(this)) {
            toRunningState()
            TimerService.requestCurrentTime(this)
        } else {
            toInitialState()
        }
    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(timerReceiver)
    }

    private fun updateTime(millis: Long) {
        Log.i("MainActivity", "Updating time: $millis ms")
        val min = (millis / 1000) / 60
        val sec = (millis / 1000) % 60
        tvStatus.text = String.format("%d:%02d", min, sec)
    }

    private fun toRunningState() {
        isRunning = true
        etMinutes.isEnabled = false
        etSeconds.isEnabled = false
        btnStartStop.text = getString(R.string.stop)
    }

    private fun toInitialState() {
        isRunning = false
        etMinutes.isEnabled = true
        etSeconds.isEnabled = true
        btnStartStop.text = getString(R.string.go)
        tvStatus.text = getString(R.string.ready)
    }
}
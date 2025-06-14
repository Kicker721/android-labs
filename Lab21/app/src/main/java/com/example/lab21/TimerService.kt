package com.example.lab21

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.edit

class TimerService : Service() {
    private var timer: CountDownTimer? = null
    private var millisLeft: Long = 0L

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                millisLeft = intent.getLongExtra(EXTRA_TIME_LEFT, 0L)
                saveState(applicationContext, millisLeft, true)
                startForeground(NOTIFICATION_ID, buildNotification())
                startTimer()
                val startedIntent = Intent(ACTION_STARTED)
                startedIntent.setPackage(packageName)
                sendBroadcast(startedIntent)
                Log.i("MainActivity", "Sent ACTION_STARTED broadcast")
            }
            ACTION_STOP -> stopTimer()
            ACTION_QUERY -> sendUpdate()
        }
        return START_STICKY
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(millisLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                millisLeft = millisUntilFinished
                saveState(applicationContext, millisLeft, true)
                sendUpdate()
                updateNotification()
            }
            override fun onFinish() {
                millisLeft = 0L
                saveState(applicationContext, 0, false)
                sendUpdate()
                val finishIntent = Intent(ACTION_FINISH)
                finishIntent.setPackage(packageName)
                sendBroadcast(finishIntent)
                stopForeground(true)
                stopSelf()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        saveState(applicationContext, 0, false)
        stopForeground(true)
        stopSelf()
        val finishIntent = Intent(ACTION_FINISH)
        finishIntent.setPackage(packageName)
        sendBroadcast(finishIntent)
    }

    private fun sendUpdate() {
        val intent = Intent(ACTION_UPDATE)
        intent.putExtra(EXTRA_TIME_LEFT, millisLeft)
        intent.setPackage(packageName)
        Log.i("Timer", "Sending update: $millisLeft ms left")
        sendBroadcast(intent)
    }

    @SuppressLint("DefaultLocale")
    private fun buildNotification(): Notification {
        val (min, sec) = ((millisLeft/1000)/60) to ((millisLeft/1000)%60)
        val contentText = String.format("%d:%02d", min, sec)
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notification = buildNotification()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.lab21.START"
        const val ACTION_STOP = "com.example.lab21.STOP"
        const val ACTION_UPDATE = "com.example.lab21.UPDATE"
        const val ACTION_FINISH = "com.example.lab21.FINISH"
        const val ACTION_STARTED = "com.example.lab21.STARTED"
        const val ACTION_QUERY = "com.example.lab21.QUERY"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"

        fun start(context: Context, millis: Long) {
            val intent = Intent(context, TimerService::class.java)
            intent.action = ACTION_START
            intent.putExtra(EXTRA_TIME_LEFT, millis)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, TimerService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }

        fun requestCurrentTime(context: Context) {
            val intent = Intent(context, TimerService::class.java)
            intent.action = ACTION_QUERY
            context.startService(intent)
        }

        fun isRunning(context: Context): Boolean {
            val prefs = context.getSharedPreferences("timer_prefs", MODE_PRIVATE)
            return prefs.getBoolean("running", false)
        }

        private fun saveState(context: Context, millis: Long, running: Boolean) {
            val prefs = context.getSharedPreferences("timer_prefs", MODE_PRIVATE)
            prefs.edit { putLong("millis", millis).putBoolean("running", running) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val prefs = getSharedPreferences("timer_prefs", MODE_PRIVATE)
        val running = prefs.getBoolean("running", false)
        if (running) {
            millisLeft = prefs.getLong("millis", 0L)
            if (millisLeft > 0) {
                startForeground(NOTIFICATION_ID, buildNotification())
                startTimer()
            } else {
                stopTimer()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.timer)
            val descriptionText = getString(R.string.timer_notifications)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
package com.example.lab20

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "color_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_RESET = "com.example.lab20.RESET"
        const val ACTION_OPEN = "com.example.lab20.OPEN"
        const val ACTION_COLOR_INPUT = "com.example.lab20.COLOR_INPUT"
        const val EXTRA_COLOR = "color_hex"
    }

    private lateinit var main: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var showNotificationButton: Button
    private lateinit var colorLabel: TextView

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showControllerNotification()
        else Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
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

        main = findViewById(R.id.main)
        showNotificationButton = findViewById(R.id.showNotificationButton)
        colorLabel = findViewById(R.id.colorLabel)

        createNotificationChannel()

        showNotificationButton.setOnClickListener { checkAndShowNotification() }

        handleIntentAction(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentAction(intent)
    }

    private fun handleIntentAction(intent: Intent?) {
        when (intent?.action) {
            ACTION_RESET -> setColor(Color.WHITE)
            ACTION_COLOR_INPUT -> {
                val color = intent.getStringExtra(EXTRA_COLOR)
                Log.i("MainActivity", "Received color: $color")
                if (color != null && color.matches(Regex("[0-9A-Fa-f]{6}"))) {
                    setColor("#$color".toColorInt())
                    colorLabel.text = getString(R.string.chosen_color, color.uppercase())
                }
            }
        }
    }

    private fun setColor(color: Int) {
        main.setBackgroundColor(color)
        if (color == Color.WHITE) {
            colorLabel.text = ""
        }
    }

    private fun checkAndShowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        showControllerNotification()
    }

    fun showControllerNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_OPEN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                PendingIntent.FLAG_MUTABLE
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                PendingIntent.FLAG_IMMUTABLE
            else ->
                PendingIntent.FLAG_UPDATE_CURRENT
        }
        val resetIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = ACTION_RESET
        }
        val resetPendingIntent = PendingIntent.getBroadcast(this, 1, resetIntent, flags)

        val colorInputIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = ACTION_COLOR_INPUT
        }
        val colorInputPendingIntent = PendingIntent.getBroadcast(this, 2, colorInputIntent, flags)

        val remoteInput = androidx.core.app.RemoteInput.Builder(EXTRA_COLOR)
            .setLabel(getString(R.string.color_format))
            .build()

        val colorAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            getString(R.string.set_color),
            colorInputPendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentTitle(getString(R.string.manager))
            .setContentText(getString(R.string.here_you_can))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(0, getString(R.string.reset_color), resetPendingIntent)
            .addAction(colorAction)
            .setAutoCancel(false)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Color Channel"
            val descriptionText = "Уведомления для управления цветом"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
package com.example.lab20

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null || intent == null) return

        when (intent.action) {
            MainActivity.ACTION_RESET -> {
                val openIntent = Intent(context, MainActivity::class.java)
                openIntent.action = MainActivity.ACTION_RESET
                openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(openIntent)
            }
            MainActivity.ACTION_COLOR_INPUT -> {
                val results = RemoteInput.getResultsFromIntent(intent)
                val color = results?.getCharSequence(MainActivity.EXTRA_COLOR)?.toString()
                if (color != null && color.matches(Regex("[0-9A-Fa-f]{6}"))) {
                    val openIntent = Intent(context, MainActivity::class.java)
                    openIntent.action = MainActivity.ACTION_COLOR_INPUT
                    openIntent.putExtra(MainActivity.EXTRA_COLOR, color)
                    openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(openIntent)
                }

                NotificationManagerCompat.from(context).cancel(MainActivity.NOTIFICATION_ID)
            }
        }
    }

}
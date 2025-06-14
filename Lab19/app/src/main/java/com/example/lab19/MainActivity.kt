package com.example.lab19

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var main: LinearLayout
    private lateinit var btnGetContacts: Button
    private lateinit var tvResult: TextView
    private lateinit var deniedLayout: LinearLayout
    private lateinit var tvDeniedText: TextView
    private lateinit var btnOpenSettings: Button
    private lateinit var btnCancel: Button
    private val REQUEST_READ_CONTACTS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showContactsScreen()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    showPermissionDeniedScreen()
                } else {
                    showPermissionDenied()
                }
            }
        }

        main = findViewById(R.id.main)
        btnGetContacts = findViewById(R.id.btnGetContacts)
        tvResult = findViewById(R.id.tvResult)
        deniedLayout = findViewById(R.id.deniedLayout)
        tvDeniedText = findViewById(R.id.tvDeniedText)
        btnOpenSettings = findViewById(R.id.btnOpenSettings)
        btnCancel = findViewById(R.id.btnCancel)

        btnGetContacts.setOnClickListener { checkContactsPermissionAndProceed() }
        btnOpenSettings.setOnClickListener { openAppSettings() }
        btnCancel.setOnClickListener { showButtonOnlyScreen() }

        showButtonOnlyScreen()
    }

    private fun showButtonOnlyScreen() {
        btnGetContacts.visibility = View.VISIBLE
        tvResult.visibility = View.GONE
        deniedLayout.visibility = View.GONE
    }

    private fun showContactsScreen() {
        btnGetContacts.visibility = View.VISIBLE
        tvResult.visibility = View.VISIBLE
        deniedLayout.visibility = View.GONE

        val contacts = getContacts()
        tvResult.text = if (contacts.isNotEmpty()) {
            getString(R.string.success) + contacts.joinToString("\n")
        } else {
            getString(R.string.not_found)
        }
    }

    private fun showPermissionDeniedScreen() {
        btnGetContacts.visibility = View.GONE
        tvResult.visibility = View.GONE
        deniedLayout.visibility = View.VISIBLE
    }

    private fun checkContactsPermissionAndProceed() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                showContactsScreen()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) -> {
                showRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun showRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.rationale))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no_god_please_no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.ladno)) { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS
                )
            }
        builder.create().show()
    }

    @SuppressLint("Range")
    private fun getContacts(): List<String> {
        val result = mutableListOf<String>()
        val cur = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cur != null) {
            val colName = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (cur.moveToNext()) {
                val name = cur.getString(colName)
                result.add(name)
            }
            cur.close()
        }
        return result
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContactsScreen()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    showPermissionDeniedScreen()
                } else {
                    showPermissionDenied()
                }
            }
        }
    }

    private fun showPermissionDenied() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.one_more_chance))
            .setNegativeButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
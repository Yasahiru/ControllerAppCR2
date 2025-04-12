package com.club.controllerappcr2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.club.controllerappcr2.utils.BluetoothManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ControlActivity : AppCompatActivity() {

    private lateinit var address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS) ?: return

        if (!hasBluetoothPermission()) {
            requestBluetoothPermission()
        } else {
            connectToDevice()
        }

        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)

        btn1.setOnClickListener { BluetoothManager.send("a") }
        btn2.setOnClickListener { BluetoothManager.send("b") }
        btn3.setOnClickListener {
            BluetoothManager.disconnect()
            finish()
        }
    }

    private fun hasBluetoothPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            101
        )
    }

    private fun connectToDevice() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    BluetoothManager.adapter = BluetoothAdapter.getDefaultAdapter()
                    BluetoothManager.connect(this@ControlActivity, address)
                }
            } catch (e: Exception) {
                Log.e("ControlActivity", "Connection failed", e)
                showToast("Connection failed: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@ControlActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToDevice()
            } else {
                Log.e("ControlActivity", "Bluetooth permission denied")
                showToast("Bluetooth permission denied")
            }
        }
    }
}

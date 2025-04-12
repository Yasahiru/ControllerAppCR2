package com.club.controllerappcr2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.club.controllerappcr2.model.SensorData
import com.club.controllerappcr2.utils.BluetoothManager
import com.club.controllerappcr2.utils.BluetoothManager.send
import com.club.controllerappcr2.utils.BluetoothManager.socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ControlActivity : AppCompatActivity() {

    private lateinit var address: String
    private lateinit var btnConnect: ImageButton
    private lateinit var adapter: SensorDataAdapter
    private val dataList = mutableListOf<SensorData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect = findViewById(R.id.btnConnect)
        address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS) ?: return

        if (!hasBluetoothPermission()) {
            requestBluetoothPermission()
        } else {
            connectToDevice()
        }

        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btnForward: ImageButton = findViewById(R.id.btnForward)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnLeft: ImageButton = findViewById(R.id.btnLeft)
        val btnRight: ImageButton = findViewById(R.id.btnRight)

        btn1.setOnClickListener { BluetoothManager.send("1") }
        btn2.setOnClickListener { BluetoothManager.send("2") }
        btn3.setOnClickListener { BluetoothManager.send("3") }
        btn4.setOnClickListener { BluetoothManager.send("4") }
        btn5.setOnClickListener { BluetoothManager.send("5") }
        btn6.setOnClickListener { BluetoothManager.send("6") }
        btn7.setOnClickListener { BluetoothManager.send("7") }
        btn8.setOnClickListener { BluetoothManager.send("8") }
        btnConnect.setOnClickListener {
            BluetoothManager.disconnect()
            finish()
        }
        btnForward.setOnClickListener { BluetoothManager.send("F") }
        btnBack.setOnClickListener { BluetoothManager.send("B") }
        btnLeft.setOnClickListener { BluetoothManager.send("L") }
        btnRight.setOnClickListener { BluetoothManager.send("R") }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = SensorDataAdapter(dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val getDataButton = findViewById<Button>(R.id.btnGetData)
        getDataButton.setOnClickListener {
            send("G") // Ask Arduino to send data
            readData()
        }


    }

    private fun readData() {
        Thread {
            try {
                val reader = BufferedReader(InputStreamReader(socket?.inputStream))
                val incoming = reader.readLine() // Waits until full line (\n) is received

                runOnUiThread {
                    if (!incoming.isNullOrBlank()) {
                        adapter.addData(SensorData(incoming.trim()))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
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

                    // Update button color based on connection success
                    withContext(Dispatchers.Main) {
                        updateConnectButtonColor(true)  // Change to green if successful
                    }
                }
            } catch (e: Exception) {
                Log.e("ControlActivity", "Connection failed", e)

                // Update button color on failure
                withContext(Dispatchers.Main) {
                    updateConnectButtonColor(false)  // Keep it red if failed
                }
            }
        }
    }


    private fun updateConnectButtonColor(isConnected: Boolean) {
        if (isConnected) {
            btnConnect.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
        } else {
            btnConnect.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
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

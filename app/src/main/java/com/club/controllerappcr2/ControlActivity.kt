package com.club.controllerappcr2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.S)
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

        var istoggledbtn1 = false
        var istoggledbtn2 = false
        var istoggledbtn3 = false
        var istoggledbtn4 = false

        fun setDefault(){
            btn1.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
            btn2.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
            btn3.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
            btn4.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
        }

        fun btnselected(wich:Int){
            when(wich){
                1-> {
                    setDefault()
                    istoggledbtn2 = false
                    istoggledbtn3 = false
                    istoggledbtn4 = false
                    btn1.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark))
                }
                2-> {
                    setDefault()
                    istoggledbtn1 = false
                    istoggledbtn3 = false
                    istoggledbtn4 = false
                    btn2.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark))
                }
                3-> {
                    setDefault()
                    istoggledbtn1 = false
                    istoggledbtn2 = false
                    istoggledbtn4 = false
                    btn3.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark))
                }
                4-> {
                    setDefault()
                    istoggledbtn1 = false
                    istoggledbtn2 = false
                    istoggledbtn3 = false
                    btn4.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark))
                }
            }
        }


        btn1.setOnClickListener {
            istoggledbtn1 = !istoggledbtn1
            if(istoggledbtn1){
                btnselected(1)
            }else{
                btn1.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
                Log.d("test","$istoggledbtn1")
            }
        }
        btn2.setOnClickListener {
            istoggledbtn2 = !istoggledbtn2
            if(istoggledbtn2){
                btnselected(2)
            }else{
                btn2.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
                Log.d("test","$istoggledbtn2")
            }
        }
        btn3.setOnClickListener {
            istoggledbtn3 = !istoggledbtn3
            if(istoggledbtn3){
                btnselected(3)
            }else{
                btn3.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
                Log.d("test","$istoggledbtn3")
            }
        }
        btn4.setOnClickListener {
            istoggledbtn4 = !istoggledbtn4
            if(istoggledbtn4){
                btnselected(4)
            }else{
                btn4.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark))
                Log.d("test","$istoggledbtn4")
            }
        }

        btn5.setOnClickListener {
            Log.i("test","cas4")
            BluetoothManager.send("q")
        }
        btn6.setOnClickListener {
            Log.i("test","5")
            BluetoothManager.send("u")
        }
        btn7.setOnClickListener {
            Log.i("test","6")
            BluetoothManager.send("y")
        }
        btn8.setOnClickListener {
            Log.i("test","7")
            BluetoothManager.send("3")
        }

        btnForward.setOnClickListener {
           if(istoggledbtn1){
               BluetoothManager.send("a")
               Log.d("test","a")
           }else if(istoggledbtn2){
               BluetoothManager.send("e")
               Log.d("test","e")
           }else if(istoggledbtn3){
               BluetoothManager.send("i")
               Log.d("test","i")
           }else if(istoggledbtn4){
               BluetoothManager.send("m")
               Log.d("test","m")
           }else{
                BluetoothManager.send("F")
               Log.d("test","F")
           }
        }

        btnBack.setOnClickListener {
            if(istoggledbtn1){
                BluetoothManager.send("b")
                Log.d("test","b")
            }else if(istoggledbtn2){
                BluetoothManager.send("f")
                Log.d("test","f")
            }else if(istoggledbtn3){
                BluetoothManager.send("g")
                Log.d("test","g")
            }else if(istoggledbtn4){
                BluetoothManager.send("n")
                Log.d("test","n")
            }else{
                BluetoothManager.send("D")
                Log.d("test","D")
            }
        }

        btnLeft.setOnClickListener {
            if(istoggledbtn1){
                BluetoothManager.send("c")
                Log.d("test","c")
            }else if(istoggledbtn2){
                BluetoothManager.send("j")
                Log.d("test","j")
            }else if(istoggledbtn3){
                BluetoothManager.send("h")
                Log.d("test","h")
            }else if(istoggledbtn4){
                BluetoothManager.send("o")
                Log.d("test","o")
            }else{
                BluetoothManager.send("L")
                Log.d("test","L")
            }
        }

        btnRight.setOnClickListener {
            if(istoggledbtn1){
                BluetoothManager.send("d")
                Log.d("test","d")
            }else if(istoggledbtn2){
                BluetoothManager.send("n")
                Log.d("test","n")
            }else if(istoggledbtn3){
                BluetoothManager.send("l")
                Log.d("test","l")
            }else if(istoggledbtn4){
                BluetoothManager.send("p")
                Log.d("test","p")
            }else{
                BluetoothManager.send("R")
                Log.d("test","R")
            }
        }

        btnConnect.setOnClickListener {
            BluetoothManager.disconnect()
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = SensorDataAdapter(dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val getDataButton = findViewById<Button>(R.id.btnGetData)
        getDataButton.setOnClickListener {
            send("G")
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasBluetoothPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
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

    private fun showToast() {
        runOnUiThread {
            Toast.makeText(this@ControlActivity, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToDevice()
            } else {
                Log.e("ControlActivity", "Bluetooth permission denied")
                showToast()
            }
        }
    }
}

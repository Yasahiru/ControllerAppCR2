package com.club.controllerappcr2

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SelectDeviceActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val REQUEST_BLUETOOTH_CONNECT_PERMISSION = 2

    private lateinit var selectDeviceRefresh: Button
    private lateinit var selectDeviceList: ListView

    companion object {
        const val EXTRA_ADDRESS = "Device_address"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        selectDeviceRefresh = findViewById(R.id.select_device_refresh)
        selectDeviceList = findViewById(R.id.select_device_list)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (m_bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        if (checkBluetoothPermission()) {
            setupBluetooth()
        } else {
            requestBluetoothPermission()
        }

        selectDeviceRefresh.setOnClickListener {
            checkBluetoothPermissionAndListDevices()
        }
    }

    private fun checkBluetoothPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            REQUEST_BLUETOOTH_CONNECT_PERMISSION
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkBluetoothPermissionAndListDevices() {
        if (checkBluetoothPermission()) {
            pairedDeviceList()
        } else {
            requestBluetoothPermission()
        }
    }

    private fun setupBluetooth() {
        if (!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            pairedDeviceList()
        }
    }

    private fun pairedDeviceList() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (m_pairedDevices.isNotEmpty()) {
            for (device in m_pairedDevices) {
                list.add(device)
                Log.i("Device", "${device.name} - ${device.address}")
            }
        } else {
            Toast.makeText(this, "No paired Bluetooth devices found", Toast.LENGTH_SHORT).show()
        }

        val deviceNames = list.map { "${it.name ?: "Unnamed Device"}\n${it.address}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames)
        selectDeviceList.adapter = adapter

        selectDeviceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device = list[position]
            val address = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            val message = when (resultCode) {
                Activity.RESULT_OK -> "Bluetooth has been enabled"
                Activity.RESULT_CANCELED -> "Bluetooth enabling has been canceled"
                else -> "Bluetooth status unknown"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pairedDeviceList()
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.club.controllerappcr2.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

object BluetoothManager {
    lateinit var adapter: BluetoothAdapter
    var socket: BluetoothSocket? = null
    var isConnected = false
    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connect(context: Context, address: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BluetoothManager", "BLUETOOTH_CONNECT permission not granted")
            throw SecurityException("Bluetooth permission not granted")
        }

        try {
            val device: BluetoothDevice = adapter.getRemoteDevice(address)
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid)
            adapter.cancelDiscovery()
            socket!!.connect()
            isConnected = true
        } catch (e: IOException) {
            isConnected = false
            Log.e("BluetoothManager", "Connection failed", e)
            throw e
        }
    }


    fun send(command: String) {
        try {
            socket?.outputStream?.write(command.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            socket?.close()
            socket = null
            isConnected = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

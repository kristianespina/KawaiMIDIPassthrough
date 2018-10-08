package com.kristianespina.midipassthrough
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.support.v4.content.PermissionChecker
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanFilter
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat.requestPermissions
import android.util.Log


class Bluetooth(context: Context) {
    private val mBluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val mBluetoothAdapter: BluetoothAdapter = mBluetoothManager.adapter
    private val bluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
    var deviceList : BluetoothDevice? = null
    private val bleScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d("ScanDeviceActivity", "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
            deviceList = result?.device
        }
    }

    fun scan() {
        Log.d("SCANNING...","Hello World")
        bluetoothLeScanner.startScan(bleScanner)
        //bluetoothLeScanner.stopScan(bleScanner)
    }
    fun stopScan() {
        Log.d("BLUETOOTH", "STOP SCANNING!")
        bluetoothLeScanner.stopScan(bleScanner)
    }


}
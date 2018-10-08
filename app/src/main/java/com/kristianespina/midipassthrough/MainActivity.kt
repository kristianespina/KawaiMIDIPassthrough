package com.kristianespina.midipassthrough

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.PermissionChecker
import android.util.Log

import android.view.View

import android.widget.AdapterView
import android.widget.ArrayAdapter

import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* MIDI */
        val bt = Bluetooth(this@MainActivity)
        when (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> bt.scan()
            else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        // Stop scanning after 10 seconds
        val midi = Midi(this@MainActivity)
        Handler().postDelayed({
            bt.stopScan()

            midi.openBluetooth(bt.deviceList)
            midi.scan(this@MainActivity)


            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, midi.deviceList)
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            spinnerInput?.adapter = adapter;
            spinnerOutput?.adapter = adapter;
        },5000)



        buttonStart.setOnClickListener(){
            Toast.makeText(this@MainActivity,
                        "MIDI Pass-through Started!",
                        Toast.LENGTH_SHORT).show();
            midi.initializeMIDI()
        }
        spinnerInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                midi.midiIn = position
            }
        }
        spinnerOutput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                midi.midiOut = position
            }
        }


    }

}



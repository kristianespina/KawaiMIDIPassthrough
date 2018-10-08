package com.kristianespina.midipassthrough

//import android.media.midi.MidiDeviceInfo
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.*
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import java.util.*

class Midi(context: Context) {

    private val handler = Handler()
    private var midiInputDevice : MidiDevice? = null
    private var midiOutputDevice : MidiDevice? = null
    private var midiInputPort :  MidiInputPort? = null
    private var midiOutputPort : MidiOutputPort? = null
    val test = 1
    var deviceList : MutableList<String> = ArrayList()

    var midiIn : Int = 0
    var midiOut : Int = 0
    private val manager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager
    private var deviceInfo  = manager.devices


    fun openBluetooth(bluetoothDevice: BluetoothDevice?) {
        if(bluetoothDevice == null) return
        manager.openBluetoothDevice(bluetoothDevice,{
            Log.d("BTMidiDevice","$it.info")
            val name = it.info.properties["name"]
            //if(name != null)
            //    deviceList.add(name.toString())
        },handler)
    }
    fun scan(context: Context) {
        Log.d("scan()","Started scanning MIDI")
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            deviceList = ArrayList() // Empty Array
            // do MIDI stuff
            val builder = AlertDialog.Builder(context)
            builder.setTitle("MIDI Support")
            builder.setMessage("Support detected!")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            for (midiDeviceInfo in manager.devices) {
                Log.d("Device Info", midiDeviceInfo.toString())

                val name = midiDeviceInfo.properties["name"]
                if(name != null)
                    deviceList.add(name.toString())
            }
        }
    }

    fun initializeMIDI() {
        val midiReceiver = object : MidiReceiver() {
            override fun onSend(msg: ByteArray, offset: Int, count: Int, timestamp: Long) {
                // Process received data
                midiInputPort?.send(msg,offset,count,timestamp)
            }
        }
        // Register Input MIDI
        Log.d("MidiInput", "$midiIn")
        Log.d("MidiOutput", "$midiOut")
        manager.openDevice(manager.devices[midiOut], {
            midiInputDevice = it
            midiInputPort = it.openInputPort(0)
        }, handler)

        // Register Output MIDI
        manager.openDevice(manager.devices[midiIn], {
            midiOutputDevice = it
            midiOutputPort = it.openOutputPort(0)
            midiOutputPort?.onConnect(midiReceiver)
        }, handler)
        //ping()

    }

    private fun closeMIDI(){
        midiInputPort?.close()
        midiOutputPort?.close()
        midiInputDevice?.close()
        midiOutputDevice?.close()
        midiInputPort = null
        midiOutputPort = null
    }
    private fun ping() {
        val buffer = ByteArray(32)
        var numBytes = 0
        val channel = 0
        buffer[numBytes++] = 0x90.toByte() // note on
        buffer[numBytes++] = 60.toByte() // pitch is middle C
        buffer[numBytes++] = 127.toByte() // max velocity
        val offset = 0
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.i("tag", "Middle C3 every 5 seconds")
                midiInputPort?.send(buffer, offset, numBytes)
            }
        }, 0, 5000)
    }
    private fun openMIDI(midiDeviceInfo: MidiDeviceInfo) {
        val properties = midiDeviceInfo.properties
        val ports = midiDeviceInfo.ports
        val propertyName = midiDeviceInfo.properties["name"]
        Log.d("[PORTS]", "$ports")
        Log.d("[PROPERTIES]", "$properties")
        Log.d("[NAME]", "$propertyName")

        closeMIDI().also {
            for (midiPorts in midiDeviceInfo.ports) {
                if(midiPorts.type == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                    midiOut = midiPorts.portNumber
                } else {
                    midiIn = midiPorts.portNumber
                }
            }
        }

    }

}

package com.example.targil1.Utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.targil1.Interfaces.TiltCallback
import com.example.targil1.enums.Directions

class TiltDetector(context: Context, private val tiltCallback: TiltCallback?) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
    private lateinit var sensorEventListener: SensorEventListener
    private val tiltThreshold = 3.0  // Can be configurable
    private val debounceTimeMs = 500L



    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                calculateTilt(x)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // pass
            }
        }
    }
    private fun calculateTilt(x: Float) {
        //Log.d("TiltDetector", "X value: $x")
        if (System.currentTimeMillis() - timestamp >= debounceTimeMs) {//enough time passed since late check:
            timestamp = System.currentTimeMillis()
            if (x >= tiltThreshold) {
                tiltCallback?.tiltCharacter(direction = Directions.LEFT)
              //  SignalManager.getInstance().toast("Tilt left")

            }else if (x <= -tiltThreshold){
                tiltCallback?.tiltCharacter(direction = Directions.RIGHT)
               // SignalManager.getInstance().toast("Tilt right")
            }
        }
    }
    fun start(){
        sensorManager
            .registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }
    fun stop(){
        sensorManager
            .unregisterListener(
                sensorEventListener,
                sensor
            )
    }

}
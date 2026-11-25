package com.iyrh.bytebase.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun rememberTemaOscuroPorLuz(): Boolean {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val sensorManager = remember(context) {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val sensorLuz = remember(sensorManager) {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    var modoOscuro by remember {
        mutableStateOf(false)
    }

    DisposableEffect(sensorManager, sensorLuz, lifecycleOwner) {

        if (sensorLuz == null) {
            onDispose { }
        } else {

            var registrado = false

            val listener = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent?) {

                    val lux = event?.values?.firstOrNull() ?: return

                    when {
                        lux < 30f -> modoOscuro = true
                        lux > 60f -> modoOscuro = false
                    }
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int
                ) {
                    // No se necesita realizar ninguna acción.
                }
            }

            fun registrarSensor() {
                if (!registrado) {
                    registrado = sensorManager.registerListener(
                        listener,
                        sensorLuz,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }

            fun liberarSensor() {
                if (registrado) {
                    sensorManager.unregisterListener(listener)
                    registrado = false
                }
            }

            val observer = LifecycleEventObserver { _, event ->

                when (event) {
                    Lifecycle.Event.ON_START -> registrarSensor()
                    Lifecycle.Event.ON_STOP -> liberarSensor()
                    else -> Unit
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            if (
                lifecycleOwner.lifecycle.currentState
                    .isAtLeast(Lifecycle.State.STARTED)
            ) {
                registrarSensor()
            }

            onDispose {
                liberarSensor()
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    return modoOscuro
}
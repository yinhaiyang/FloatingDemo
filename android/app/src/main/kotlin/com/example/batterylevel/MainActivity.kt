package com.example.batterylevel

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant
import androidx.annotation.NonNull
import io.flutter.plugin.common.MethodChannel
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.os.IBinder
import android.content.ComponentName
import android.content.ServiceConnection

class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private var floatingService:FloatingService? = null;

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
            call, result ->
            Log.e("*********************",call.method)
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()
                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else if(call.method=="openSetting"){
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),0)
            } else if(call.method=="openFloatingWindow"){
                startFloatingWindow()
            }else if(call.method=="changeText"){
                if( floatingService!!.textView!!.text == "Hello Flutter") {
                    floatingService!!.textView!!.text = "Nice to meet you"
                }else{
                    floatingService!!.textView!!.text = "Hello Flutter"
                }
            }
            else {
                result.notImplemented()
            }
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }

    private fun startFloatingWindow(){
//        startService(Intent(this@MainActivity, FloatingService::class.java))
        bindService(Intent(this@MainActivity, FloatingService::class.java),conn,Context.BIND_AUTO_CREATE)
        Log.e("*********************","startFloatingWindow")
    }

    var conn: ServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            floatingService = (service as FloatingService.ControlBinder).getService()
        }
    }

}

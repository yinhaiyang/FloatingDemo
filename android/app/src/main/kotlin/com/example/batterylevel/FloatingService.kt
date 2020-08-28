package com.example.batterylevel

import android.annotation.TargetApi
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.view.WindowManager
import android.view.Gravity
import android.widget.TextView

class FloatingService : Service() {

    var floatingView: View? = null
    var textView: TextView? = null

    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    inner class ControlBinder: Binder() {
        fun getService():FloatingService {
            return this@FloatingService
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("*********************", "onCreate")
        isStarted = true

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)
        textView = floatingView!!.findViewById(R.id.text_view)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams!!.format = PixelFormat.RGBA_8888
        layoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams!!.width = 800
        layoutParams!!.height = 450
        layoutParams!!.x = 300
        layoutParams!!.y = 300
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("*********************","onBind")
        showFloatingWindow()
        return ControlBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("*********************","onStartCommand")
        showFloatingWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            windowManager!!.addView(floatingView, layoutParams)
            floatingView!!.setOnTouchListener(FloatingOnTouchListener())
            floatingView!!.setOnClickListener(FloatingOnClickListener())

        }
    }

    private inner class FloatingOnClickListener : View.OnClickListener{
        override fun onClick(p0: View?) {
            val intent = Intent(this@FloatingService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this@FloatingService.startActivity(intent)
        }
    }

    private inner class FloatingOnTouchListener : View.OnTouchListener {
        private var x: Int = 0
        private var y: Int = 0

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    layoutParams!!.x = layoutParams!!.x + movedX
                    layoutParams!!.y = layoutParams!!.y + movedY
                    windowManager!!.updateViewLayout(view, layoutParams)
                }
                else -> {
                }
            }
            return false
        }
    }

    companion object {
        var isStarted = false
    }
}
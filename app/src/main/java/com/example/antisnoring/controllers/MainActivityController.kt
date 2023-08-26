package com.example.antisnoring.controllers

import com.example.antisnoring.callbacks.CallbackAmplitude
import com.example.antisnoring.common.Dictaphone
import com.example.antisnoring.common.Whistling
import com.example.antisnoring.screens.MainActivity

const val TRIGGER: Double = 35.0

class MainActivityController(private val mainActivity: MainActivity): CallbackAmplitude {
    private val dictaphone: Dictaphone by lazy {
        Dictaphone(mainActivity, this).apply {
            prepare()
        }
    }
    private val whistling: Whistling by lazy { Whistling(mainActivity) }


    fun destroy(){
        whistling.release()
        dictaphone.release()
    }

    fun pause(){
        dictaphone.stop()
        whistling.stop()
    }

    fun tapButtonSound(){
        if (dictaphone.isListen){
            stopScan()
        }else {
            startScan()
        }
    }

    private fun startScan(){
        dictaphone.start()
        mainActivity.setImageButtonSound(true)
    }

    private fun stopScan(){
        whistling.stop()
        dictaphone.stop()
        mainActivity.setImageButtonSound(false)
        mainActivity.setValue(0.0)
    }

    override fun onNewValue(value: Double) {
        mainActivity.runOnUiThread {
            mainActivity.setValue(value)
        }
        if (value >= TRIGGER){
            if (!whistling.isPlay) {
                whistling.playHandler(3000)
            }
        }
    }
}
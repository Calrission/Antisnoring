package com.example.antisnoring.controllers

import com.example.antisnoring.callbacks.CallbackAmplitude
import com.example.antisnoring.common.Dictaphone
import com.example.antisnoring.common.Whistling
import com.example.antisnoring.screens.MainActivity

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
    }

    override fun onNewValue(value: Double) {
        mainActivity.runOnUiThread {
            mainActivity.setValue(value)
        }
        if (value >= 80.0){
            if (!whistling.isPlay) {
                whistling.playHandler(3000)
            }
        }
    }
}
package com.example.antisnoring.common

import android.media.MediaRecorder
import com.example.antisnoring.callbacks.CallbackAmplitude
import java.util.TimerTask

class SoundAmplitudeTimerTask(
    private val recorder: MediaRecorder,
    private val callbackAmplitude: CallbackAmplitude
): TimerTask() {
    override fun run() {
        var dp = 20 * kotlin.math.log10(kotlin.math.abs(recorder.maxAmplitude).toDouble())
        if (dp == Double.NEGATIVE_INFINITY){
            dp = 0.0
        }
        callbackAmplitude.onNewValue(dp)

    }
}
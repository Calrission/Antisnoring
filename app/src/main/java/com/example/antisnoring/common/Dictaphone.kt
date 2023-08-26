package com.example.antisnoring.common

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.example.antisnoring.callbacks.CallbackAmplitude
import java.io.File
import java.util.Timer

const val PERIOD: Long = 1000
const val DELAY: Long = 0



class Dictaphone(
    context: Context,
    private val callbackAmplitude: CallbackAmplitude
) {
    private val recorder: MediaRecorder = getInstanceMediaRecorder(context)


    private var timer: Timer? = null

    var isListen = false
        private set

    fun prepare(){
        recorder.prepare()
        recorder.start()
        recorder.pause()
    }

    fun start(){
        recorder.resume()
        isListen = true
        timer = Timer()
        timer!!.schedule(SoundAmplitudeTimerTask(recorder, callbackAmplitude), DELAY, PERIOD)
    }

    fun stop(){
        recorder.pause()
        isListen = false
        timer?.cancel()
    }

    fun release(){
        recorder.release()
        timer?.cancel()
    }

    private fun getInstanceMediaRecorder(context: Context): MediaRecorder{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            MediaRecorder(context)
        }else{
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("${context.cacheDir.absolutePath}${File.pathSeparator}${System.currentTimeMillis()}.mp4")
        }
    }
}
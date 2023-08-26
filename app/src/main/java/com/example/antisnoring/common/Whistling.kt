package com.example.antisnoring.common

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import com.example.antisnoring.R

class Whistling(context: Context) {
    private var soundPool: SoundPool = SoundPool.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
        .build()
    private var streamId: Int? = null
    private var soundId: Int? = null

    var isPlay = false
        private set

    init {
        soundId = soundPool.load(context.resources.openRawResourceFd(R.raw.whistling), 1)
    }
    fun play(){
        streamId = soundPool.play(soundId!!, 1f, 1f, 1, -1, 1f)
        isPlay = true
    }

    fun playHandler(ms: Long){
        play()
        Handler(Looper.getMainLooper()).postDelayed({
             stop()
        }, ms)
    }

    fun stop(){
        if (streamId != null) {
            soundPool.stop(streamId!!)
            isPlay = false
        }
    }

    fun release(){
        soundPool.release()
    }
}
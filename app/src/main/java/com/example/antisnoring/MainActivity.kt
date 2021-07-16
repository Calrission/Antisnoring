package com.example.antisnoring

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaRecorder
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    lateinit var soundPool: SoundPool
    var recorder: MediaRecorder? = null
    var sound_id: Int? = null
    var stream_id: Int? = null
    var isScan = false
    lateinit var thread: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        recorder = MediaRecorder()
        button_sound.setOnClickListener {
            if (isScan){
                stopScan()
            }else {
                startScan()
            }
            switchIMG()
        }

        initSoundPool()
        initMediaRecorder()

    }

    private fun initSoundPool(){
        soundPool = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            .build()
        sound_id = soundPool.load(resources.openRawResourceFd(R.raw.svist), 1)
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
    }

    fun startLoopSoundSvist(){
       stream_id = soundPool.play(sound_id!!, 1f, 1f, 1, -1, 1f)
    }

    fun stopSoundSvist(){
        try {
            soundPool.stop(stream_id!!)
        }catch (e: Exception){}
    }

    private fun initMediaRecorder(){
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder!!.setOutputFile("/dev/null")
        recorder!!.prepare()
        recorder!!.start()
        recorder!!.pause()
    }

    private fun startScan(){
        isScan = true
        recorder!!.resume()
        thread = Timer()
        thread.schedule(MyTimerTask(this, recorder!!, value, Handler()), 0, 1000)
    }

    private fun stopScan(){
        isScan = false
        stopSoundSvist()
        recorder!!.pause()
        thread.cancel()
        value.text = "0 ДБ"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
            requestPermissions()
        }
    }

    private fun switchIMG(){
        if (isScan)
            button_sound.setImageResource(R.drawable.pause)
        else
            button_sound.setImageResource(R.drawable.play)
    }

    override fun onPause() {
        super.onPause()
        try {
            recorder!!.pause()
            soundPool.pause(stream_id!!)
        }catch (e: Exception){}
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            soundPool.release()
            recorder!!.release()
            thread.cancel()
        }catch (e: Exception){}
    }
}

class MyTimerTask(val context: Context, val recorder: MediaRecorder, val value: TextView, val handler: Handler): TimerTask() {
    override fun run() {
        var playSound = false
        (context as Activity).runOnUiThread {
            var dp = 20 * kotlin.math.log10(abs(recorder.maxAmplitude).toDouble())
            if (dp == Double.NEGATIVE_INFINITY){
                dp = 0.0
            }
            val str_dp = String.format("%.1f", dp).replace(",", ".") + " ДБ"
            value.text = str_dp
            if (dp >= 80.0){
                if (!playSound) {
                    playSound = true
                    (context as MainActivity).startLoopSoundSvist()
                    handler.postDelayed({
                        playSound = false
                        (context as MainActivity).stopSoundSvist()
                    }, 2900)
                }
            }
        }
    }
}
package com.example.antisnoring

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaRecorder
import android.media.SoundPool
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.antisnoring.databinding.ActivityMainBinding
import java.io.File
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    lateinit var soundPool: SoundPool
    var recorder: MediaRecorder? = null
    var sound_id: Int? = null
    var stream_id: Int? = null
    var isScan = false
    lateinit var thread: Timer

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        binding.buttonSound.setOnClickListener {
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
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("${cacheDir.absolutePath}${File.pathSeparator}${System.currentTimeMillis()}.mp4")
            prepare()
            start()
            pause()
        }
    }

    private fun startScan(){
        isScan = true
        recorder?.resume()
        thread = Timer()
        thread.schedule(MyTimerTask(this, recorder!!, binding.value, Handler()), 0, 1000)
    }

    private fun stopScan(){
        isScan = false
        stopSoundSvist()
        recorder?.pause()
        thread.cancel()
        binding.value.text = "0 ДБ"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
            requestPermissions()
        }
    }

    private fun switchIMG(){
        if (isScan)
            binding.buttonSound.setImageResource(R.drawable.pause)
        else
            binding.buttonSound.setImageResource(R.drawable.play)
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
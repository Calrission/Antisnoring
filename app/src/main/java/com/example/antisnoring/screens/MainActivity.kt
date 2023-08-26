package com.example.antisnoring.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.antisnoring.controllers.MainActivityController
import com.example.antisnoring.R
import com.example.antisnoring.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Locale


private const val REQUEST_CODE = 101
class MainActivity : AppCompatActivity() {
    private val controller = MainActivityController(this)
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        binding.buttonSound.setOnClickListener {
            controller.tapButtonSound()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setValue(db: Double){
        binding.value.text = String.format(
            Locale.getDefault(), "%.1f", db
        ).replace(",", ".") + " ДБ"
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(
                binding.root,
                "Разрешение не предоствленно, функционал прилоэение ограничен",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun setImageButtonSound(isPlay: Boolean){
        if (isPlay)
            binding.buttonSound.setImageResource(R.drawable.play)
        else
            binding.buttonSound.setImageResource(R.drawable.pause)
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }


}
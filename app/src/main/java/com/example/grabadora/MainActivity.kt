package com.example.grabadora

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.grabadora.databinding.ActivityMainBinding
import kotlin.Exception


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var grabacion: MediaRecorder
    lateinit var reproducion:MediaPlayer
    var salida:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            record.setOnClickListener {
                Permiso()
            }
            play.setOnClickListener {
                Reproducir()
            }
        }
    }

    fun Permiso(){
        if(ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO),1000)
        }
        else{
            Grabar()
        }
    }


    fun Grabar(){

        if (grabacion==null){
            salida=Environment.getExternalStorageDirectory().absolutePath+"/Gabacion.mp3"
            grabacion=MediaRecorder()
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC)
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            grabacion.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            grabacion.setOutputFile(salida)
            try {
                grabacion.prepare()
                grabacion.start()
            }
            catch (e:Exception){

            }
            binding.record.setBackgroundResource(R.drawable.recred)
            Toast.makeText(this, "Grabando", Toast.LENGTH_SHORT).show()
        }
        else if(grabacion!=null){
            grabacion.stop()
            grabacion.release()
            binding.record.setBackgroundResource(R.drawable.rec)
            Toast.makeText(this, "Grabacion finalizada", Toast.LENGTH_SHORT).show()


        }
    }


    fun Reproducir(){
        try {
            reproducion.setDataSource(salida)
            reproducion.prepare()
            
        }catch (e:Exception){

        }
        reproducion.start()
        Toast.makeText(this, "El audio se esta Reproduciendo", Toast.LENGTH_SHORT).show()

    }

}


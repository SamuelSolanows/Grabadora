package com.example.grabadora

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.example.grabadora.databinding.ActivityMainBinding
import java.io.IOException
import kotlin.Exception


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var grabar: MediaRecorder
    lateinit var reproducion: MediaPlayer
    var ruta: Uri? = null
    var grabando: Boolean = false
    var verificarPermisi = false
    var microfono = false
    var almacenamiento = false
    lateinit var LanzarPermisos: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PedirPermisos()
        binding.apply {
            record.setOnClickListener {


                if (!verificarPermisi) {

                } else {
                    StartAll()
                }

            }


            play.setOnClickListener {
                Reproducir()
            }
        }
    }

//    val PreguntarPermiso =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permitido ->
//            if (permitido) {
//                Toast.makeText(this, "Permitido", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Denegado", Toast.LENGTH_SHORT).show()
//            }
//        }

    private fun PedirPermisos() {
        LanzarPermisos =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
                microfono = permisos[Manifest.permission.RECORD_AUDIO] ?: microfono
                almacenamiento =
                    permisos[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: almacenamiento

            }
        MostrarPermisos()
    }

    private fun MostrarPermisos() {
        microfono = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        almacenamiento = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        verificarPermisi = true
    }

    private fun StartAll() {
        if (ruta == null) {
            MostrarAlmacenamiento()
        } else {
            GrabaroDetener()
        }
    }

    private fun GrabaroDetener() {
        if (grabando) {
            DetenerGrabacion()
        } else {
            IniciarGrabacion()
        }
    }

    private fun MostrarAlmacenamiento() {
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        BotonesDeGrabacion.launch(intent)

    }

    val BotonesDeGrabacion =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.data.let { uri ->
                    ruta = uri
                    IniciarGrabacion()
                }
            }
        }


    private fun IniciarGrabacion() {
        try {
            grabando = true
            grabar = MediaRecorder()
            binding.record.setText("Detener")
            grabar.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                val NombreAudio = "Audio.mp3"
                val documentFile = DocumentFile.fromTreeUri(this@MainActivity, ruta!!)
                val ArchivoAudio = documentFile!!.createFile(
                    "audio/mpeg", NombreAudio
                )      //El miniType es el formato que se le dara al audio?

                setOutputFile(
                    contentResolver.openFileDescriptor(
                        ArchivoAudio!!.uri, "w"
                    )!!.fileDescriptor
                )  //porque esata cosa pide un modo?  y siempre tiene que ser la W?


                prepare()
                start()
            }


        } catch (e: IOException) {
            Log.e("Grabar", e.toString())
        }
    }

    private fun DetenerGrabacion() {
        try {
            grabando = false
            binding.record.setText("Grabar")
            grabar.stop()
            grabar.release()
        } catch (e: Exception) {
            Log.e("Grabar", e.toString())
        }
    }


    fun Reproducir() {
        try {
            reproducion.setDataSource(ruta.toString())
            reproducion.prepare()

        } catch (e: Exception) {

        }
        reproducion.start()
        Toast.makeText(this, "El audio se esta Reproduciendo", Toast.LENGTH_SHORT).show()

    }

}


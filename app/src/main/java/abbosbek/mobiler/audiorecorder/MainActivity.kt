package abbosbek.mobiler.audiorecorder

import abbosbek.mobiler.audiorecorder.databinding.ActivityMainBinding
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), Timer.OnTimeTickListener {

    private lateinit var binding : ActivityMainBinding

    private lateinit var recorder: MediaRecorder

    private var dirPath = ""
    private var fileName = ""

    private var isRecording = false
    private var isPaused = false

    private lateinit var timer : Timer

    private lateinit var vibrator: Vibrator

    private lateinit var amplitudes : ArrayList<Float>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
            if (isGranted){
                Toast.makeText(this, "Permission now Granted", Toast.LENGTH_SHORT).show()
                startRecorder()
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        binding.btnRecord.setOnClickListener {
            checkPermission()
        }

        binding.btnList.setOnClickListener {
            //todo
            Toast.makeText(this, "list saved", Toast.LENGTH_SHORT).show()

        }

        binding.btnDone.setOnClickListener {
            //todo
            stopRecorder()
            Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show()
        }
        binding.btnDelete.setOnClickListener {
            stopRecorder()
            File("$dirPath$fileName.mp3")
            Toast.makeText(this, "Record delete", Toast.LENGTH_SHORT).show()
        }

        binding.btnDelete.isClickable = false

    }

    private fun checkPermission(){
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) -> {
                when{
                    isPaused -> resumeRecording()
                    isRecording -> pauseRecording()
                    else -> startRecorder()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.RECORD_AUDIO
                )
            }
        }
    }

    private fun startRecorder(){
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        }else{
            MediaRecorder()
        }

        dirPath = "${externalCacheDir?.absolutePath}/"

        var date = SimpleDateFormat("yyyy.MM.DD_hh_mm.ss").format(Date())

        fileName = "audio_recorder_${date}"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")
            try {
                prepare()
            }catch (e : Exception){
                e.printStackTrace()
            }
            start()
        }

        binding.btnRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false

        timer.start()

        binding.apply {
            btnDelete.isClickable = true
            btnDelete.setImageResource(R.drawable.ic_delete)

            btnList.visibility = View.GONE
            btnDone.visibility = View.VISIBLE
        }
    }

    private fun pauseRecording(){
        recorder.pause()
        isPaused = true
        binding.btnRecord.setImageResource(R.drawable.ic_record)
        timer.pause()
    }
    private fun resumeRecording(){
        recorder.resume()
        isPaused = false
        binding.btnRecord.setImageResource(R.drawable.ic_pause)
        timer.start()
    }

    private fun stopRecorder(){
        timer.stop()
        recorder.apply {
            stop()
            release()
        }

        isPaused = false
        isRecording = false


        binding.btnList.visibility = View.VISIBLE
        binding.btnDone.visibility = View.GONE

        binding.btnDelete.isClickable = false
        binding.btnDelete.setImageResource(R.drawable.ic_delete_disabled)

        binding.btnRecord.setImageResource(R.drawable.ic_record)

        binding.tvTimer.text = "00:00:00"
        binding.waveFormView.clear()
    }

    override fun onTimerTick(duration: String) {

        binding.tvTimer.text = duration
        binding.waveFormView.addAmplitude(recorder.maxAmplitude.toFloat())
    }
}
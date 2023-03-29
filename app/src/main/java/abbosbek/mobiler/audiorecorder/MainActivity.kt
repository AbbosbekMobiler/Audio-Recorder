package abbosbek.mobiler.audiorecorder

import abbosbek.mobiler.audiorecorder.databinding.ActivityMainBinding
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var bottomSheetBehavior : BottomSheetBehavior<LinearLayout>

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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetInclude.bottomSheet)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


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

            stopRecorder()
            Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show()

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.bottomSheetBg.visibility = View.VISIBLE
            binding.bottomSheetInclude.fileNameInput.setText(fileName)
        }

        binding.bottomSheetInclude.btnCancel.setOnClickListener {
            File("$dirPath$fileName.mp3").delete()
            dismiss()
        }
        binding.bottomSheetInclude.btnOk.setOnClickListener {
            dismiss()
            saveRecord()
        }

        binding.bottomSheetBg.setOnClickListener {
            File("$dirPath$fileName.mp3").delete()
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            stopRecorder()
            File("$dirPath$fileName.mp3").delete()
            Toast.makeText(this, "Record delete", Toast.LENGTH_SHORT).show()
        }

        binding.btnDelete.isClickable = false

    }

    private fun saveRecord() = with(binding) {

        val newFileName = bottomSheetInclude.fileNameInput.text.toString()
        if (newFileName != fileName){
            var newFile = File("$dirPath$newFileName.mp3")
            File("$dirPath$newFileName.mp3").renameTo(newFile)
        }
    }

    private fun dismiss(){
        binding.bottomSheetBg.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        },100)
        hideKeyBoard(binding.bottomSheetInclude.fileNameInput)

    }

    private fun hideKeyBoard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
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
package abbosbek.mobiler.audiorecorder.ui

import abbosbek.mobiler.audiorecorder.R
import abbosbek.mobiler.audiorecorder.databinding.ActivityAudioPlayerBinding
import android.media.MediaPlayer
import android.media.PlaybackParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import java.text.DecimalFormat
import java.text.NumberFormat

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var binding : ActivityAudioPlayerBinding

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay = 1000L
    private var jumpValue = 1000

    private var playBackSpeed = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var filePath = intent.getStringExtra("filepath")
        var fileName = intent.getStringExtra("filename")

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.tvFileName.text = fileName

        mediaPlayer = MediaPlayer()

        handler = Handler(Looper.getMainLooper())

        mediaPlayer.apply {
            setDataSource(filePath)
            prepare()
        }
        binding.tvTrackDuration.text = dateFormat(mediaPlayer.duration)
        runnable = Runnable{
            binding.seekBar.progress = mediaPlayer.currentPosition
            binding.tvTrackProgress.text = dateFormat(mediaPlayer.currentPosition)
            handler.postDelayed(runnable,delay)
        }
        binding.btnPlay.setOnClickListener {
            playPausePlayer()
        }

        playPausePlayer()
        binding.seekBar.max = mediaPlayer.duration

        mediaPlayer.setOnCompletionListener {
            binding.btnPlay.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_circle_play,theme)
            handler.removeCallbacks(runnable)
        }
        binding.btnForward.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition + jumpValue)
            binding.seekBar.progress += jumpValue
        }

        binding.btnBackward.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition - jumpValue)
            binding.seekBar.progress -= jumpValue
        }
        binding.chip.setOnClickListener {
            if (playBackSpeed != 2f){
                playBackSpeed += 0.5f
            }else{
                playBackSpeed = 0.5f
            }
            mediaPlayer.playbackParams = PlaybackParams().setSpeed(playBackSpeed)
            binding.chip.text = "x $playBackSpeed"
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mediaPlayer.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

    }

    private fun playPausePlayer() = with(binding) {

        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
            btnPlay.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_circle_pause,theme)
            handler.postDelayed(runnable,delay)
        }else{
            mediaPlayer.pause()
            btnPlay.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_circle_play,theme)
            handler.removeCallbacks(runnable)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }

    private fun dateFormat(duration : Int) : String{
        var d = duration/1000
        var s = d%60
        var m = (d/60%60)
        var h = ((d - m*60)/360).toInt()

        val f : NumberFormat = DecimalFormat("00")
        var str = "${m}:${f.format(s)}"
        if (h > 0){
            str = "$h:${str}"
        }
        return str
    }
}
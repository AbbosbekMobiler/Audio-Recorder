package abbosbek.mobiler.audiorecorder.ui

import abbosbek.mobiler.audiorecorder.R
import abbosbek.mobiler.audiorecorder.adapter.Adapter
import abbosbek.mobiler.audiorecorder.adapter.OnItemClickListener
import abbosbek.mobiler.audiorecorder.databinding.ActivityGalleryBinding
import abbosbek.mobiler.audiorecorder.db.AppDatabase
import abbosbek.mobiler.audiorecorder.model.AudioRecord
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity(),OnItemClickListener{

    private lateinit var binding : ActivityGalleryBinding

    private lateinit var records : ArrayList<AudioRecord>
    private lateinit var mAdapter: Adapter
    private lateinit var db : AppDatabase

    private lateinit var searchInput : TextInputEditText

    private lateinit var editBar : RelativeLayout

    private var allChecked = false

    private lateinit var bottomSheet : LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        records = ArrayList()
        editBar = binding.editBar

        setSupportActionBar(binding.toolbar)

        db = AppDatabase.getInstance(this)

        bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        mAdapter = Adapter(records,this)

        binding.recyclerview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@GalleryActivity)

        }

        searchInput = binding.searchInput
        searchInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var query = p0.toString()
                searchDatabase(query)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        fetchAll()

        binding.btnClose.setOnClickListener {
            editBar.visibility = View.GONE
            records.map {
                it.isChecked = false
                mAdapter.setEditMode(false)
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.btnSelectAll.setOnClickListener {
            allChecked = !allChecked
            records.map { it.isChecked = allChecked }
            mAdapter.notifyDataSetChanged()
        }

    }

    private fun searchDatabase(query: String) {
        GlobalScope.launch {
            records.clear()
            var queryResult = db.audioRecordDao().searchDatabase("%$query%")
            records.addAll(queryResult)

            runOnUiThread {
                mAdapter.notifyDataSetChanged()
            }

        }

    }

    private fun fetchAll(){
        GlobalScope.launch {
            records.clear()
            var queryResult = db.audioRecordDao().getAll()
            records.addAll(queryResult)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClickListener(position: Int) {

        var audioRecord = records[position]

        if (mAdapter.isEditMode()){
            records[position].isChecked = !records[position].isChecked
            mAdapter.notifyItemChanged(position)
        }else{
            val intent = Intent(this,AudioPlayerActivity::class.java)
            intent.putExtra("filepath",records[position].filePath)
            intent.putExtra("filename",records[position].fileName)

            startActivity(intent)
        }

    }

    override fun onItemLongClickListener(position: Int) {
        mAdapter.setEditMode(true)
        records[position].isChecked = !records[position].isChecked
        mAdapter.notifyItemChanged(position)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        if (mAdapter.isEditMode() && editBar.visibility == View.GONE){
            editBar.visibility = View.VISIBLE
        }
    }
}
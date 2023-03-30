package abbosbek.mobiler.audiorecorder.ui

import abbosbek.mobiler.audiorecorder.R
import abbosbek.mobiler.audiorecorder.adapter.Adapter
import abbosbek.mobiler.audiorecorder.adapter.OnItemClickListener
import abbosbek.mobiler.audiorecorder.databinding.ActivityGalleryBinding
import abbosbek.mobiler.audiorecorder.db.AppDatabase
import abbosbek.mobiler.audiorecorder.model.AudioRecord
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity(),OnItemClickListener{

    private lateinit var binding : ActivityGalleryBinding

    private lateinit var records : ArrayList<AudioRecord>
    private lateinit var mAdapter: Adapter
    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        records = ArrayList()

        db = AppDatabase.getInstance(this)

        mAdapter = Adapter(records,this)

        binding.recyclerview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@GalleryActivity)

        }

        fetchAll()

    }

    private fun fetchAll(){
        CoroutineScope(Dispatchers.IO).launch {
            records.clear()
            var queryResult = db.audioRecordDao().getAll()
            records.addAll(queryResult)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClickListener(position: Int) {
        Toast.makeText(this, "Item Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClickListener(position: Int) {
        Toast.makeText(this, "Item Long Clicked", Toast.LENGTH_SHORT).show()
    }
}
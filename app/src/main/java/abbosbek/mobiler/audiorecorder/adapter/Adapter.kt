package abbosbek.mobiler.audiorecorder.adapter

import abbosbek.mobiler.audiorecorder.databinding.ItemviewLayoutBinding
import abbosbek.mobiler.audiorecorder.model.AudioRecord
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class Adapter(var records : List<AudioRecord>,var listener: OnItemClickListener) : RecyclerView.Adapter<Adapter.ItemHolder>(){

    inner class ItemHolder(val binding: ItemviewLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            ItemviewLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var record = records[position]

        var sdf = SimpleDateFormat("dd/MM/yyyy")
        var date = Date(record.timestamp)

        var strDate = sdf.format(date)

        holder.binding.apply {
            tvFileName.text = record.fileName
            tvMeta.text = "${record.duration} $strDate"
        }

        holder.itemView.setOnLongClickListener {
            listener.onItemLongClickListener(position)
            true
        }

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(position)
        }
    }

}
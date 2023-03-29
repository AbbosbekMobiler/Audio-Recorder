package abbosbek.mobiler.audiorecorder.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "audioRecords")
data class AudioRecord(
    val fileName : String,
    val filePath : String,
    val timestamp : Long,
    val duration : String,
    val ampsPath : String
){
    @PrimaryKey(autoGenerate = true)
    var id = 0
    @Ignore
    var isChecked = false
}

package abbosbek.mobiler.audiorecorder.db

import abbosbek.mobiler.audiorecorder.model.AudioRecord
import android.provider.MediaStore.Audio
import androidx.room.*

@Dao
interface AudioRecordDao {

    @Query("select * from audioRecords") fun getAll() : List<AudioRecord>

    @Query("select * from audioRecords where fileName like :query")
    fun searchDatabase(query: String) : List<AudioRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(audioRecord: AudioRecord)

    @Delete
    fun delete(audioRecord: AudioRecord)

    @Delete
    fun delete(audioRecords : Array<AudioRecord>)

    @Update
    fun update(audioRecord: AudioRecord)

}
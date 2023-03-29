package abbosbek.mobiler.audiorecorder.db

import abbosbek.mobiler.audiorecorder.model.AudioRecord
import androidx.room.*

@Dao
interface AudioRecordDao {

    @Query("select * from audioRecords") fun getAll() : List<AudioRecord>

    @Insert
    fun insert(vararg audioRecord: AudioRecord)

    @Delete
    fun delete(audioRecord: AudioRecord)

    @Delete
    fun delete(audioRecords : Array<AudioRecord>)

    @Update
    fun update(audioRecord: AudioRecord)

}
package abbosbek.mobiler.audiorecorder.db

import abbosbek.mobiler.audiorecorder.model.AudioRecord
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [AudioRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audioRecordDao() : AudioRecordDao

    companion object{
        @Volatile
        private var instance : AppDatabase ?= null

        @Synchronized
        fun getInstance(context: Context) : AppDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "audio_record.db"
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return instance as AppDatabase
        }
    }


}
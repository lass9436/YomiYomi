package com.lass.yomiyomi.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lass.yomiyomi.data.dao.DailyRecordDao
import com.lass.yomiyomi.data.model.DailyRecord
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.dao.KanjiDao
import com.lass.yomiyomi.data.dao.WordDao
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.dao.MyWordDao
import com.lass.yomiyomi.data.model.MyWord

@Database(
    entities = [
        DailyRecord::class,
        Kanji::class,
        Word::class,
        MyWord::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyRecordDao(): DailyRecordDao
    abstract fun kanjiDao(): KanjiDao
    abstract fun wordDao(): WordDao
    abstract fun myWordDao(): MyWordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_db"
                )
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
            }
        }
    }
}

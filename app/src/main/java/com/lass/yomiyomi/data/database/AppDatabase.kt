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
import com.lass.yomiyomi.data.dao.MyKanjiDao
import com.lass.yomiyomi.data.model.MyKanji

@Database(
    entities = [
        DailyRecord::class,
        Kanji::class,
        Word::class,
        MyWord::class,
        MyKanji::class,
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyRecordDao(): DailyRecordDao
    abstract fun kanjiDao(): KanjiDao
    abstract fun wordDao(): WordDao
    abstract fun myWordDao(): MyWordDao
    abstract fun myKanjiDao(): MyKanjiDao

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

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

@Database(entities = [DailyRecord::class, Kanji::class, Word::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyRecordDao(): DailyRecordDao
    abstract fun kanjiDao(): KanjiDao // KanjiDao 추가
    abstract fun wordDao(): WordDao // WordDao 추가

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_db"
                )
                .fallbackToDestructiveMigration(true) // 기존 데이터베이스 삭제 후 재생성
                .build().also { instance = it }
            }
        }
    }
}
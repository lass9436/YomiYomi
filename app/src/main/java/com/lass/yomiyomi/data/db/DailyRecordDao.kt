package com.lass.yomiyomi.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lass.yomiyomi.data.model.DailyRecord
import java.time.LocalDate

@Dao
interface DailyRecordDao {
    @Query("SELECT * FROM daily_record ORDER BY date DESC LIMIT 5")
    fun getRecentRecords(): List<DailyRecord>

    @Query("SELECT * FROM daily_record WHERE date <= :today ORDER BY date DESC")
    fun getAllBefore(today: LocalDate): List<DailyRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: DailyRecord)
}

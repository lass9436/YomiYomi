package com.lass.yomiyomi.data.repository

import android.content.Context
import com.lass.yomiyomi.data.db.AppDatabase
import com.lass.yomiyomi.data.model.DailyRecord
import com.lass.yomiyomi.data.model.StudyStatus
import java.time.LocalDate

class StudyRepositoryImpl(context: Context) : StudyRepository {
    private val dao = AppDatabase.getInstance(context).dailyRecordDao()

    override fun getRecentRecords(): List<DailyRecord> {
        return dao.getRecentRecords().sortedBy { it.date }
    }

    override fun getCurrentStreak(): Int {
        val today = LocalDate.now()
        val records = dao.getAllBefore(today)

        var streak = 0
        var expectedDate = today

        for (record in records) {
            if (record.date != expectedDate) break

            if (record.status == StudyStatus.STUDIED || record.status == StudyStatus.ITEM_USED) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else break
        }

        return streak
    }
}

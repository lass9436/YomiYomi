package com.lass.yomiyomi.data.repository

import com.lass.yomiyomi.data.model.DailyRecord

interface StudyRepository {
    fun getRecentRecords(): List<DailyRecord>
    fun getCurrentStreak(): Int
}
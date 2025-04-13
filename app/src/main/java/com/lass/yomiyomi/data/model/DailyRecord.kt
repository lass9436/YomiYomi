package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_record")
data class DailyRecord(
    @PrimaryKey val date: LocalDate,
    val status: StudyStatus
)

package com.lass.yomiyomi.data.database

import androidx.room.TypeConverter
import com.lass.yomiyomi.data.model.StudyStatus
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromStatus(value: StudyStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): StudyStatus = StudyStatus.valueOf(value)

    @TypeConverter
    fun fromDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toDate(dateString: String): LocalDate = LocalDate.parse(dateString)
}

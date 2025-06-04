package com.lass.yomiyomi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "sentence",
    indices = [Index(value = ["paragraphId"])]
)
data class MySentence(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val japanese: String,           // "私[わたし]は学生[がくせい]です"
    val korean: String,             // "나는 학생입니다"
    val paragraphId: String? = null,
    val orderInParagraph: Int = 0,
    val category: String,
    val level: String,              // 🔥 difficulty -> level로 변경! Level enum의 value 저장 (N5, N4, N3, N2, N1)
    val learningProgress: Float = 0f,
    val reviewCount: Int = 0,
    val lastReviewedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) 

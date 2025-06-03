package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.domain.model.entity.WordItem

// Entity -> Domain Model (조회용)
fun Word.toWordItem(): WordItem = WordItem(
    id = id,
    word = word,
    reading = reading,
    type = type,
    meaning = meaning,
    level = level,
    learningWeight = learningWeight,
    timestamp = timestamp
)

// Domain Model -> Entity (CRUD용)
fun WordItem.toWord(): Word = Word(
    id = id,
    word = word,
    reading = reading,
    type = type,
    meaning = meaning,
    level = level,
    learningWeight = learningWeight,
    timestamp = timestamp
)

// List 변환 함수들
fun List<Word>.toWordItems(): List<WordItem> = map { it.toWordItem() }
fun List<WordItem>.toWords(): List<Word> = map { it.toWord() } 
package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.domain.model.entity.MyWordItem

// Entity -> Domain Model (조회용)
fun MyWord.toMyWordItem(): MyWordItem = MyWordItem(
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
fun MyWordItem.toMyWord(): MyWord = MyWord(
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
fun List<MyWord>.toMyWordItems(): List<MyWordItem> = map { it.toMyWordItem() }
fun List<MyWordItem>.toMyWords(): List<MyWord> = map { it.toMyWord() } 
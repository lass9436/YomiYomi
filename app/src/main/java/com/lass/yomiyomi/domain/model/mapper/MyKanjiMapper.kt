package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.domain.model.entity.MyKanjiItem

// Entity -> Domain Model (조회용)
fun MyKanji.toMyKanjiItem(): MyKanjiItem = MyKanjiItem(
    id = id,
    kanji = kanji,
    onyomi = onyomi,
    kunyomi = kunyomi,
    meaning = meaning,
    level = level,
    learningWeight = learningWeight,
    timestamp = timestamp
)

// Domain Model -> Entity (CRUD용)
fun MyKanjiItem.toMyKanji(): MyKanji = MyKanji(
    id = id,
    kanji = kanji,
    onyomi = onyomi,
    kunyomi = kunyomi,
    meaning = meaning,
    level = level,
    learningWeight = learningWeight,
    timestamp = timestamp
)

// List 변환 함수들
fun List<MyKanji>.toMyKanjiItems(): List<MyKanjiItem> = map { it.toMyKanjiItem() }
fun List<MyKanjiItem>.toMyKanjis(): List<MyKanji> = map { it.toMyKanji() } 
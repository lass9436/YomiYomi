package com.lass.yomiyomi.domain.model.mapper

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.domain.model.entity.KanjiItem

// Entity -> Domain Model (조회용)
fun Kanji.toKanjiItem(): KanjiItem = KanjiItem(
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
fun KanjiItem.toKanji(): Kanji = Kanji(
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
fun List<Kanji>.toKanjiItems(): List<KanjiItem> = map { it.toKanjiItem() }
fun List<KanjiItem>.toKanjis(): List<Kanji> = map { it.toKanji() } 
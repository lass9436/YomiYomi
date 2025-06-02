package com.lass.yomiyomi.domain.model

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.data.model.MyWord

// Entity -> Domain Model (조회용)
// Kanji -> KanjiItem
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

// Word -> WordItem
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

// MyKanji -> MyKanjiItem
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

// MyWord -> MyWordItem
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
// KanjiItem -> Kanji
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

// WordItem -> Word
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

// MyKanjiItem -> MyKanji
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

// MyWordItem -> MyWord
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

// List 변환 함수들 (Entity -> Domain)
fun List<Kanji>.toKanjiItems(): List<KanjiItem> = map { it.toKanjiItem() }
fun List<Word>.toWordItems(): List<WordItem> = map { it.toWordItem() }
fun List<MyKanji>.toMyKanjiItems(): List<MyKanjiItem> = map { it.toMyKanjiItem() }
fun List<MyWord>.toMyWordItems(): List<MyWordItem> = map { it.toMyWordItem() }

// List 변환 함수들 (Domain -> Entity)
fun List<KanjiItem>.toKanjis(): List<Kanji> = map { it.toKanji() }
fun List<WordItem>.toWords(): List<Word> = map { it.toWord() }
fun List<MyKanjiItem>.toMyKanjis(): List<MyKanji> = map { it.toMyKanji() }
fun List<MyWordItem>.toMyWords(): List<MyWord> = map { it.toMyWord() } 
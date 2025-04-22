package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz

class GenerateKanjiQuizByLevelUseCase(
    private val repository: KanjiRepository
) {
    suspend operator fun invoke(level: Level): KanjiQuiz {
        // 1. 레포지토리에서 모든 한자 데이터 가져오기
        val kanjiList = repository.getRandomKanjiByLevel(level.toString())

        // 2. 랜덤으로 정답 한자를 선택
        val correctKanji = kanjiList.random()

        // 3. 랜덤한 오답 선택지 3개를 추가
        val shuffledOptions = kanjiList
            .filter { (it) != (correctKanji) } // 정답 제외
            .shuffled()
            .take(3)
            .toMutableList()

        // 4. 오답 선택지에 정답 추가
        if (!shuffledOptions.contains(correctKanji)) {
            shuffledOptions.add(correctKanji)
        }

        // 5. 선택지 순서 섞기
        shuffledOptions.shuffle()

        // 6. KanjiQuiz 객체 생성 및 반환
        return KanjiQuiz(
            kanji = correctKanji.kanji,
            correctString = select(correctKanji),
            optionStrings = shuffledOptions.map{it -> select(it)},
            correctIndex = shuffledOptions.indexOf(correctKanji)
        )
    }

    private fun select(kanji: Kanji) : String {
        return "${kanji.kunyomi} / ${kanji.meaning}"
    }
}
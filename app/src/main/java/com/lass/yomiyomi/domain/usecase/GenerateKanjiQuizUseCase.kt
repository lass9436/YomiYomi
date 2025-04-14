package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz

class GenerateKanjiQuizUseCase(
    private val repository: KanjiRepository
) {
    suspend operator fun invoke(correctAttributeSelector: (Kanji) -> String): KanjiQuiz {
        // 1. 레포지토리에서 모든 한자 데이터 가져오기
        val kanjiList = repository.getAllKanji()

        // 2. 랜덤으로 정답 한자를 선택
        val correctKanji = kanjiList.random()

        // 3. 랜덤한 오답 선택지 3개를 추가
        val shuffledOptions = kanjiList
            .filter { correctAttributeSelector(it) != correctAttributeSelector(correctKanji) } // 정답 제외
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
            correctString = correctAttributeSelector(correctKanji),
            optionStrings = shuffledOptions.map(correctAttributeSelector),
            correctIndex = shuffledOptions.indexOf(correctKanji)
        )
    }
}
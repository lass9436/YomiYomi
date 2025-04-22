package com.lass.yomiyomi.viewmodel.kanjiQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizByLevelUseCase
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizUseCase

class KanjiQuizViewModelFactory(
    private val repository: KanjiRepository // KanjiRepository를 전달받음
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanjiQuizViewModel::class.java)) {
            // GenerateKanjiQuizUseCase를 먼저 생성
            val generateKanjiQuizUseCase = GenerateKanjiQuizUseCase(repository)
            val generateKanjiQuizByLevelUseCase = GenerateKanjiQuizByLevelUseCase(repository)
            return KanjiQuizViewModel(generateKanjiQuizUseCase, generateKanjiQuizByLevelUseCase) as T // 생성 후 전달
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
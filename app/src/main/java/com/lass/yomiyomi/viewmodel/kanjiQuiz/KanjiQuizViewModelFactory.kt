package com.lass.yomiyomi.viewmodel.kanjiQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizByLevelUseCase

class KanjiQuizViewModelFactory(
    private val repository: KanjiRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanjiQuizViewModel::class.java)) {
            val generateKanjiQuizByLevelUseCase = GenerateKanjiQuizByLevelUseCase(repository)
            return KanjiQuizViewModel(generateKanjiQuizByLevelUseCase, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
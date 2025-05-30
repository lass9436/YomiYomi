package com.lass.yomiyomi.viewmodel.wordQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.usecase.GenerateWordQuizRandomModeUseCase
import com.lass.yomiyomi.domain.usecase.GenerateWordQuizStudyModeUseCase

class WordQuizViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordQuizViewModel::class.java)) {
            val generateWordQuizRandomModeUseCase = GenerateWordQuizRandomModeUseCase(repository)
            val generateWordQuizStudyModeUseCase = GenerateWordQuizStudyModeUseCase(repository)
            return WordQuizViewModel(generateWordQuizRandomModeUseCase, generateWordQuizStudyModeUseCase, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

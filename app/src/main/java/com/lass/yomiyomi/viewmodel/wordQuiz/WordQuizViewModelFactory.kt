package com.lass.yomiyomi.viewmodel.wordQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.usecase.GenerateWordQuizByLevelUseCase

class WordQuizViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordQuizViewModel::class.java)) {
            val generateWordQuizByLevelUseCase = GenerateWordQuizByLevelUseCase(repository)
            return WordQuizViewModel(generateWordQuizByLevelUseCase, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

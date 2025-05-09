package com.lass.yomiyomi.viewmodel.kanjiQuiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.usecase.GenerateKanjiQuizByLevelUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KanjiQuizViewModel(
    private val generateKanjiQuizByLevelUseCase: GenerateKanjiQuizByLevelUseCase
) : ViewModel(), KanjiQuizViewModelInterface {

    // StateFlow로 퀴즈 데이터를 관리
    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> get() = _quizState

    // 퀴즈 로딩 상태를 관리
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    override fun loadQuizByLevel(level: Level) {
        // 비동기 작업을 viewModelScope에서 실행
        viewModelScope.launch {
            try {
                _isLoading.value = true // 로딩 시작
                val quiz = generateKanjiQuizByLevelUseCase(level) // UseCase 호출
                _quizState.value = quiz // 퀴즈 데이터 업데이트
            } catch (e: Exception) {
                // 오류 처리: 로그 출력 또는 UI와 연결
                e.printStackTrace()
            } finally {
                _isLoading.value = false // 로딩 종료
            }
        }
    }
}
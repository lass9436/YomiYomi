package com.lass.yomiyomi.viewmodel.mySentence.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.data.repository.MySentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySentenceRandomViewModel @Inject constructor(
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MySentenceRandomViewModelInterface {

    private val _randomSentence = MutableStateFlow<SentenceItem?>(null)
    override val randomSentence: StateFlow<SentenceItem?> = _randomSentence.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // 초기에는 전체 레벨에서 랜덤 가져오기
        fetchRandomSentenceByLevel(null)
    }

    override fun fetchRandomSentenceByLevel(level: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sentence = mySentenceRepository.getRandomSentenceByLevel(level)
                _randomSentence.value = sentence
            } catch (e: Exception) {
                // Handle error - 에러 시 null로 설정
                _randomSentence.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// 더미 ViewModel (프리뷰용)
class DummySentenceRandomViewModel : MySentenceRandomViewModelInterface {
    override val randomSentence: StateFlow<SentenceItem?> = MutableStateFlow(
        SentenceItem(
            id = 1,
            japanese = "今日{きょう}は良{よ}い天気{てんき}ですね。",
            korean = "오늘은 좋은 날씨네요.",
            category = "일상회화",
            level = com.lass.yomiyomi.domain.model.constant.Level.N4,
            learningProgress = 0.7f,
            createdAt = System.currentTimeMillis()
        )
    ).asStateFlow()
    
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    
    override fun fetchRandomSentenceByLevel(level: String?) {
        // 더미 구현 - 아무것도 하지 않음
    }
} 
package com.lass.yomiyomi.viewmodel.myParagraph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphDetailViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _paragraph = MutableStateFlow<ParagraphItem?>(null)
    val paragraph: StateFlow<ParagraphItem?> = _paragraph.asStateFlow()

    private val _sentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    val sentences: StateFlow<List<SentenceItem>> = _sentences.asStateFlow()

    // 동적 카테고리와 난이도 목록 (문장 추가/편집용)
    private val _availableCategories = MutableStateFlow<List<String>>(emptyList())
    val availableCategories: StateFlow<List<String>> = _availableCategories.asStateFlow()

    private val _availableDifficulties = MutableStateFlow<List<String>>(emptyList())
    val availableDifficulties: StateFlow<List<String>> = _availableDifficulties.asStateFlow()

    fun loadParagraphDetail(paragraphId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 문단 정보 로드
                val paragraphItem = myParagraphRepository.getParagraphById(paragraphId)
                _paragraph.value = paragraphItem

                // 해당 문단의 문장들 로드
                val sentenceList = mySentenceRepository.getSentencesByParagraph(paragraphId)
                _sentences.value = sentenceList.sortedBy { it.orderInParagraph }

                // 카테고리와 난이도 목록 로드 (새 문장 추가용)
                loadAvailableOptions()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadAvailableOptions() {
        try {
            val categories = mySentenceRepository.getDistinctCategories()
            _availableCategories.value = categories

            val difficulties = mySentenceRepository.getDistinctDifficulties()
            _availableDifficulties.value = difficulties
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun insertSentence(sentence: SentenceItem) {
        viewModelScope.launch {
            try {
                // 새 문장의 orderInParagraph 설정 (마지막 순서 + 1)
                val maxOrder = _sentences.value.maxOfOrNull { it.orderInParagraph } ?: 0
                val sentenceWithOrder = sentence.copy(orderInParagraph = maxOrder + 1)

                mySentenceRepository.insertSentence(sentenceWithOrder)

                // 문장 목록 새로고침
                sentence.paragraphId?.let { paragraphId ->
                    val updatedSentences = mySentenceRepository.getSentencesByParagraph(paragraphId)
                    _sentences.value = updatedSentences.sortedBy { it.orderInParagraph }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateSentence(sentence: SentenceItem) {
        viewModelScope.launch {
            try {
                mySentenceRepository.updateSentence(sentence)

                // 문장 목록 새로고침
                sentence.paragraphId?.let { paragraphId ->
                    val updatedSentences = mySentenceRepository.getSentencesByParagraph(paragraphId)
                    _sentences.value = updatedSentences.sortedBy { it.orderInParagraph }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteSentence(sentenceId: Int) {
        viewModelScope.launch {
            try {
                // 삭제할 문장의 문단 ID 미리 저장
                val currentParagraphId = _sentences.value.find { it.id == sentenceId }?.paragraphId

                mySentenceRepository.deleteSentenceById(sentenceId)

                // 문장 목록 새로고침
                currentParagraphId?.let { paragraphId ->
                    val updatedSentences = mySentenceRepository.getSentencesByParagraph(paragraphId)
                    _sentences.value = updatedSentences.sortedBy { it.orderInParagraph }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

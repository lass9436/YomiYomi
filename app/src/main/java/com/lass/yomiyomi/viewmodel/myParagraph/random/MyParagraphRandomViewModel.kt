package com.lass.yomiyomi.viewmodel.myParagraph.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphRandomViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository
) : ViewModel(), MyParagraphRandomViewModelInterface {

    private val _randomParagraph = MutableStateFlow<ParagraphItem?>(null)
    override val randomParagraph: StateFlow<ParagraphItem?> = _randomParagraph.asStateFlow()

    private val _sentences = MutableStateFlow<List<SentenceItem>>(emptyList())
    override val sentences: StateFlow<List<SentenceItem>> = _sentences.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // 초기에는 전체 레벨에서 랜덤 가져오기
        fetchRandomParagraphByLevel(null)
    }

    override fun fetchRandomParagraphByLevel(level: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val paragraph = myParagraphRepository.getRandomParagraphByLevel(level)
                _randomParagraph.value = paragraph
                
                // 문단에 속한 문장들도 가져오기
                if (paragraph != null) {
                    val paragraphSentences = mySentenceRepository.getSentencesByParagraph(paragraph.paragraphId)
                    _sentences.value = paragraphSentences
                } else {
                    _sentences.value = emptyList()
                }
            } catch (e: Exception) {
                // Handle error - 에러 시 null로 설정
                _randomParagraph.value = null
                _sentences.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// 더미 ViewModel (프리뷰용)
class DummyParagraphRandomViewModel : MyParagraphRandomViewModelInterface {
    override val randomParagraph: StateFlow<ParagraphItem?> = MutableStateFlow(
        ParagraphItem(
            paragraphId = 1,
            title = "일본 여행 준비하기",
            description = "일본 여행을 위한 기본 회화와 유용한 표현들을 배워봅시다.",
            category = "여행",
            level = com.lass.yomiyomi.domain.model.constant.Level.N4,
            totalSentences = 10,
            actualSentenceCount = 7,
            createdAt = System.currentTimeMillis()
        )
    ).asStateFlow()
    
    override val sentences: StateFlow<List<SentenceItem>> = MutableStateFlow(
        listOf(
            SentenceItem(
                id = 1,
                japanese = "空港{くうこう}はどこですか？",
                korean = "공항은 어디입니까?",
                category = "여행",
                level = com.lass.yomiyomi.domain.model.constant.Level.N4,
                learningProgress = 0.8f,
                paragraphId = 1,
                orderInParagraph = 1,
                createdAt = System.currentTimeMillis()
            ),
            SentenceItem(
                id = 2,
                japanese = "切符{きっぷ}を買{か}いたいです。",
                korean = "표를 사고 싶습니다.",
                category = "여행",
                level = com.lass.yomiyomi.domain.model.constant.Level.N4,
                learningProgress = 0.6f,
                paragraphId = 1,
                orderInParagraph = 2,
                createdAt = System.currentTimeMillis()
            )
        )
    ).asStateFlow()
    
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    
    override fun fetchRandomParagraphByLevel(level: String?) {
        // 더미 구현 - 아무것도 하지 않음
    }
} 
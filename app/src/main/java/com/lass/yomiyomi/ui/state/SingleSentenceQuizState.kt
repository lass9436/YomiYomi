package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType

data class SingleSentenceQuizState(
    val sentence: SentenceItem? = null,
    val quiz: ParagraphQuiz? = null,
    val isLoading: Boolean = false,
    val insufficientDataMessage: String? = null,
    // 음성 인식 관련 상태
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val isQuizCompleted: Boolean = false,
    // 한국어 번역 표시 토글
    val showKoreanTranslation: Boolean = true,
    // 퀴즈 타입 선택
    val quizTypes: List<String> = listOf("한국어", "일본어", "요미가나X"),
    val selectedQuizTypeIndex: Int = 0,
    // 학습 모드
    val isLearningMode: Boolean = false
)

data class SingleSentenceQuizCallbacks(
    val onStartListening: () -> Unit,
    val onStopListening: () -> Unit,
    val onProcessRecognition: (String) -> List<String>,
    val onResetQuiz: () -> Unit,
    val onShowAnswers: () -> Unit,
    val onToggleKoreanTranslation: () -> Unit,
    val onBackToSentenceList: () -> Unit,
    val onQuizTypeSelected: (Int) -> Unit,
    val onLearningModeChanged: (Boolean) -> Unit,
    val onRefresh: () -> Unit
) 
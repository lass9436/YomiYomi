package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.entity.SentenceItem

data class ParagraphQuizState(
    val selectedLevel: Level = Level.ALL,
    val isLoading: Boolean = false,
    val quiz: ParagraphQuiz? = null,
    val insufficientDataMessage: String? = null,
    // 음성 인식 관련 상태
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val isQuizCompleted: Boolean = false,
    val sentences: List<SentenceItem> = emptyList(),
    val availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL),
    // 한국어 번역 표시 토글
    val showKoreanTranslation: Boolean = true
)

data class ParagraphQuizCallbacks(
    val onLevelSelected: (Level) -> Unit,
    val onStartListening: () -> Unit,
    val onStopListening: () -> Unit,
    val onProcessRecognition: (String) -> List<String>,
    val onRefresh: () -> Unit,
    val onResetQuiz: () -> Unit,
    val onShowAnswers: () -> Unit,
    val onToggleKoreanTranslation: () -> Unit
) 
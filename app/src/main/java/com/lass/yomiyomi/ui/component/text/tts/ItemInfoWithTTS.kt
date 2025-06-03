package com.lass.yomiyomi.ui.component.text.tts

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.Item
import com.lass.yomiyomi.speech.SpeechManager

/**
 * Item 인터페이스를 활용한 통합 TTS 컴포넌트
 * Random에서 Item의 정보를 자동으로 TTS와 함께 표시
 */
@Composable
fun ItemInfoWithTTS(
    item: Item,
    speechManager: SpeechManager,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        item.toInfoRows().forEach { infoRow ->
            if (infoRow.isJapanese) {
                InfoRowWithTTS(
                    label = infoRow.label,
                    value = infoRow.value,
                    speechManager = speechManager,
                    labelWidth = 60.dp
                )
            } else {
                InfoRowWithTTS(
                    label = infoRow.label,
                    value = infoRow.value,
                    speechManager = speechManager,
                    labelWidth = 60.dp,
                    showTTS = false // 일본어가 아닌 경우 TTS 숨김
                )
            }
        }
    }
} 
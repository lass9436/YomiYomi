package com.lass.yomiyomi.ui.component.text.furigana

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.util.FuriganaParser

@Composable
fun FuriganaText(
    japaneseText: String,
    displayMode: DisplayMode = DisplayMode.FULL,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    furiganaSize: TextUnit = (fontSize.value * 0.6).sp
) {
    val segments = remember(japaneseText) { 
        FuriganaParser.parse(japaneseText) 
    }
    
    Row(modifier = modifier) {
        segments.forEach { segment ->
            when {
                // 한자 + 요미가나 세그먼트
                segment.furigana != null -> {
                    when (displayMode) {
                        DisplayMode.FULL -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 1.dp)
                            ) {
                                Text(
                                    text = segment.furigana,
                                    fontSize = furiganaSize,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = segment.text,
                                    fontSize = fontSize,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        DisplayMode.JAPANESE_ONLY -> {
                            Text(
                                text = segment.text,
                                fontSize = fontSize,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                        DisplayMode.FURIGANA_ONLY -> {
                            Text(
                                text = segment.furigana,
                                fontSize = fontSize,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                        DisplayMode.KANJI_ONLY -> {
                            // 한자만 표시 (요미가나 숨김)
                            Text(
                                text = segment.text.filter { FuriganaParser.isKanji(it) },
                                fontSize = fontSize,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
                // 일반 텍스트 (히라가나/카타카나)
                else -> {
                    when (displayMode) {
                        DisplayMode.FURIGANA_ONLY, DisplayMode.KANJI_ONLY -> {
                            // 이 모드들에서는 히라가나/카타카나 숨김
                        }
                        else -> {
                            Text(
                                text = segment.text,
                                fontSize = fontSize,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
            }
        }
    }
}

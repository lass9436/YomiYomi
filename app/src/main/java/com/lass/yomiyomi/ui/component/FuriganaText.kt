package com.lass.yomiyomi.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 텍스트 세그먼트 데이터 클래스
data class TextSegment(
    val text: String,
    val furigana: String? = null  // null이면 일반 텍스트
)

// 표시 모드 열거형
enum class DisplayMode {
    FULL,           // 전체 표시 (한자 + 요미가나 + 히라가나)
    JAPANESE_ONLY,  // 일본어만 (한자 + 히라가나, 요미가나 숨김)
    FURIGANA_ONLY,  // 요미가나만 (한자 숨김)
    KANJI_ONLY      // 한자만 (요미가나 + 히라가나 숨김)
}

object FuriganaParser {
    fun parse(text: String): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()
        val pattern = """([^[\]]+)(?:\[([^\]]+)\])?""".toRegex()
        
        pattern.findAll(text).forEach { match ->
            val baseText = match.groupValues[1]
            val furigana = match.groupValues[2].takeIf { it.isNotEmpty() }
            segments.add(TextSegment(baseText, furigana))
        }
        
        return segments
    }
    
    // 한자인지 판별 (간단한 유니코드 범위 체크)
    fun isKanji(char: Char): Boolean {
        return char.code in 0x4E00..0x9FAF || // CJK Unified Ideographs
               char.code in 0x3400..0x4DBF    // CJK Extension A
    }
    
    fun hasKanji(text: String): Boolean {
        return text.any { isKanji(it) }
    }
}

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
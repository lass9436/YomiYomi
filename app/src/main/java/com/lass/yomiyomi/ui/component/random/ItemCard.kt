package com.lass.yomiyomi.ui.component.random

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.lass.yomiyomi.domain.model.*
import com.lass.yomiyomi.ui.component.tts.MainTextWithTTS
import com.lass.yomiyomi.ui.component.tts.ItemInfoWithTTS
import com.lass.yomiyomi.util.rememberSpeechManager

@Composable
fun ItemCard(
    item: Item,
    onCardClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val speechManager = rememberSpeechManager()
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 480.dp)
            .padding(8.dp)
            .then(
                if (onCardClick != null) {
                    Modifier.clickable(onClick = onCardClick)
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 통일된 메인 텍스트 + TTS 컴포넌트 사용
            MainTextWithTTS(
                text = item.getMainText(),
                speechManager = speechManager,
                onTextClick = {
                    val searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=${item.getMainText()}"
                    val intent = Intent(Intent.ACTION_VIEW, searchUrl.toUri())
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 통일된 Item 정보 + TTS 컴포넌트 사용
            ItemInfoWithTTS(
                item = item,
                speechManager = speechManager
            )
        }
    }
} 
package com.lass.yomiyomi.ui.component

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Word

@Composable
fun ItemCard(
    item: Any,
    onCardClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
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
                } else if (item is Kanji) {
                    Modifier.clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${item.kanji}".toUri()
                        )
                        context.startActivity(intent)
                    }
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
            when (item) {
                is Kanji -> {
                    Text(
                        text = item.kanji,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "음독 :", value = item.onyomi)
                    InfoRow(label = "훈독 :", value = item.kunyomi)
                    InfoRow(label = "의미 :", value = item.meaning)
                    InfoRow(label = "레벨 :", value = item.level)
                }
                is Word -> {
                    Text(
                        text = item.word,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "읽기 :", value = item.reading)
                    InfoRow(label = "품사 :", value = item.type)
                    InfoRow(label = "의미 :", value = item.meaning)
                    InfoRow(label = "레벨 :", value = item.level)
                }
            }
        }
    }
} 
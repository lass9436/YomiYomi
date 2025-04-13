package com.lass.yomiyomi.data.database

import android.content.Context
import com.lass.yomiyomi.R
import com.lass.yomiyomi.data.model.Kanji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object KanjiDataImporter {

    suspend fun importKanjiFromCsv(context: Context): List<Kanji> {
        return withContext(Dispatchers.IO) {
            val kanjiList = mutableListOf<Kanji>()
            val inputStream = context.resources.openRawResource(R.raw.kanji_list)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.forEachLine { line ->
                val parts = line.split(",") // 쉼표 기준으로 값 나누기
                if (parts.size >= 6 && parts[0] != "#") { // 첫 라인(#) 제외
                    kanjiList.add(
                        Kanji(
                            kanji = parts[1].trim(),
                            onyomi = parts[2].trim(),
                            kunyomi = parts[3].trim(),
                            meaning = parts[4].trim(),
                            level = parts[5].trim()
                        )
                    )
                }
            }
            reader.close()
            kanjiList
        }
    }
}
package com.lass.yomiyomi.data.database

import android.content.Context
import com.lass.yomiyomi.R
import com.lass.yomiyomi.data.model.Kanji
import java.io.BufferedReader
import java.io.InputStreamReader

object KanjiDataImporter {

    /**
     * raw 리소스 폴더에서 kanji_list 데이터를 읽어와 Kanji 리스트로 변환
     */
    fun importKanjiFromCsv(context: Context): List<Kanji> {
        val kanjiList = mutableListOf<Kanji>()

        // raw 폴더의 리소스 파일 열기
        val inputStream = context.resources.openRawResource(R.raw.kanji_list)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val regex = Regex("""((?<=^|,)"(?:[^"]|"")*"|[^",]+)""")

        reader.forEachLine { line ->
            if (line.startsWith("#")) return@forEachLine // 주석 행 (#으로 시작) 건너뛰기

            // 정규식을 사용해 데이터를 분리
            val parts = regex.findAll(line).map { it.value.trim('"') }.toList()

            if (parts.size >= 6) { // 데이터 유효성 검사 (6개 이상의 항목이 있어야 함)
                kanjiList.add(
                    Kanji(
                        id = Integer.parseInt(parts[0].trim()),
                        kanji = parts[1].trim(),
                        onyomi = parts[2].trim(),
                        kunyomi = parts[3].trim(),
                        meaning = parts[4].trim(),
                        level = parts[5].trim(),
                        learningWeight = 0.5f,
                        timestamp = 0L
                    )
                )
            }
        }

        reader.close() // BufferedReader 닫기

        return kanjiList
    }
}
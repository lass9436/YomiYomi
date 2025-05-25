package com.lass.yomiyomi.data.database

import android.content.Context
import com.lass.yomiyomi.R
import com.lass.yomiyomi.data.model.Word
import java.io.BufferedReader
import java.io.InputStreamReader

object WordDataImporter {

    /**
     * raw 리소스 폴더에서 word_list 데이터를 읽어와 Word 리스트로 변환
     */
    fun importWordsFromCsv(context: Context): List<Word> {
        val wordList = mutableListOf<Word>()

        // raw 폴더의 리소스 파일 열기
        val inputStream = context.resources.openRawResource(R.raw.word_list)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val regex = Regex("""((?<=^|,)"(?:[^"]|"")*"|[^",]+)""") // CSV 데이터 분리용 정규식

        reader.forEachLine { line ->
            if (line.startsWith("#")) return@forEachLine // 주석 행 (#으로 시작) 건너뛰기

            // 정규식을 사용해 데이터를 분리
            val parts = regex.findAll(line).map { it.value.trim('"') }.toList()

            if (parts.size >= 6) { // 데이터 유효성 검사 (6개 이상의 항목이 있어야 함)
                wordList.add(
                    Word(
                        id = Integer.parseInt(parts[0].trim()), // ID
                        word = parts[1].trim(), // 단어
                        reading = parts[2].trim(), // 읽는법(발음)
                        type = parts[3].trim(), // 단어 유형
                        meaning = parts[4].trim(), // 뜻
                        level = parts[5].trim() // 수준 (레벨)
                    )
                )
            }
        }

        reader.close() // BufferedReader 닫기

        return wordList
    }
}
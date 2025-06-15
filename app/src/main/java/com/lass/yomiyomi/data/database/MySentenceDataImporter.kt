package com.lass.yomiyomi.data.database

import android.content.Context
import com.lass.yomiyomi.R
import com.lass.yomiyomi.data.model.MySentence
import java.io.BufferedReader
import java.io.InputStreamReader

object MySentenceDataImporter {

    /**
     * raw 리소스 폴더에서 sentence_list 데이터를 읽어와 Sentence 리스트로 변환
     */
    fun importSentencesFromCsv(context: Context): List<MySentence> {
        val mySentenceList = mutableListOf<MySentence>()

        // raw 폴더의 리소스 파일 열기
        val inputStream = context.resources.openRawResource(R.raw.sentence_list)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val regex = Regex("""\"([^\"]*)\"|([^,]+)""")
        //val regex = Regex("""((?<=^|,)"(?:[^"]|"")*"|[^",]+)""") // CSV 데이터 분리용 정규식

        reader.forEachLine { line ->
            if (line.startsWith("#") || line.startsWith("id,")) return@forEachLine // 주석 행과 헤더 행 건너뛰기

            // 정규식을 사용해 데이터를 분리
            val parts = regex.findAll(line).map { it.value.trim('"') }.toList()

            if (parts.size >= 7) { // 데이터 유효성 검사 (7개 이상의 항목이 있어야 함)
                mySentenceList.add(
                    MySentence(
                        id = Integer.parseInt(parts[0].trim()), // ID
                        japanese = parts[1].trim(), // 일본어 문장
                        korean = parts[2].trim(), // 한국어 번역
                        paragraphId = parts[3].trim().takeIf { it.isNotEmpty() }?.toInt(), // paragraphId (Int)
                        orderInParagraph = Integer.parseInt(parts[4].trim()), // 단락 내 순서
                        category = parts[5].trim(), // 카테고리
                        level = parts[6].trim(), // 레벨
                        learningProgress = 0f,
                        reviewCount = 0,
                        lastReviewedAt = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }

        reader.close() // BufferedReader 닫기

        return mySentenceList
    }
} 

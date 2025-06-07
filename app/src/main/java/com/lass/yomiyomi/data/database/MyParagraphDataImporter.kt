package com.lass.yomiyomi.data.database

import android.content.Context
import com.lass.yomiyomi.R
import com.lass.yomiyomi.data.model.MyParagraph
import java.io.BufferedReader
import java.io.InputStreamReader

object MyParagraphDataImporter {

    /**
     * raw 리소스 폴더에서 paragraph_list 데이터를 읽어와 Paragraph 리스트로 변환
     */
    fun importParagraphsFromCsv(context: Context): List<MyParagraph> {
        val myParagraphList = mutableListOf<MyParagraph>()

        // raw 폴더의 리소스 파일 열기
        val inputStream = context.resources.openRawResource(R.raw.paragraph_list)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val regex = Regex("""((?<=^|,)"(?:[^"]|"")*"|[^",]+)""") // CSV 데이터 분리용 정규식

        reader.forEachLine { line ->
            if (line.startsWith("#")) return@forEachLine // 주석 행 (#으로 시작) 건너뛰기

            // 정규식을 사용해 데이터를 분리
            val parts = regex.findAll(line).map { it.value.trim('"') }.toList()

            if (parts.size >= 6) { // 데이터 유효성 검사 (6개 이상의 항목이 있어야 함)
                myParagraphList.add(
                    MyParagraph(
                        paragraphId = parts[0].trim().toInt(), // paragraphId (Int)
                        title = parts[1].trim(), // 제목
                        description = parts[2].trim(), // 설명
                        category = parts[3].trim(), // 카테고리
                        level = parts[4].trim(), // 레벨 (CSV에서는 difficulty 컬럼이지만 실제로는 level)
                        totalSentences = parts[5].trim().toInt(), // 총 문장 수
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }

        reader.close() // BufferedReader 닫기

        return myParagraphList
    }
} 

package com.lass.yomiyomi.data.database

import android.content.Context
import com.lass.yomiyomi.R
import com.lass.yomiyomi.data.model.MyWord
import java.io.BufferedReader
import java.io.InputStreamReader

object MyWordDataImporter {

    /**
     * raw 리소스 폴더에서 my_word_list 데이터를 읽어와 MyWord 리스트로 변환
     */
    fun importMyWordFromCsv(context: Context): List<MyWord> {
        val myWordList = mutableListOf<MyWord>()

        try {
            // raw 폴더의 리소스 파일 열기
            val inputStream = context.resources.openRawResource(R.raw.my_word_list)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val regex = Regex("""((?<=^|,)"(?:[^"]|"")*"|[^",]+)""")

            reader.forEachLine { line ->
                if (line.startsWith("#")) return@forEachLine // 주석 행 (#으로 시작) 건너뛰기

                // 정규식을 사용해 데이터를 분리
                val parts = regex.findAll(line).map { it.value.trim('"') }.toList()

                if (parts.size >= 6) { // 데이터 유효성 검사 (6개 이상의 항목이 있어야 함)
                    myWordList.add(
                        MyWord(
                            id = Integer.parseInt(parts[0].trim()),
                            word = parts[1].trim(),
                            reading = parts[2].trim(),
                            type = parts[3].trim(),
                            meaning = parts[4].trim(),
                            level = parts[5].trim(),
                            learningWeight = 1.0f, // 초기값은 학습이 필요한 상태
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            reader.close() // BufferedReader 닫기
        } catch (e: Exception) {
            // 파일이 없거나 읽기 실패 시 빈 리스트 반환
            e.printStackTrace()
        }

        return myWordList
    }
} 
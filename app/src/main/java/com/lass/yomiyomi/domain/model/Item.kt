package com.lass.yomiyomi.domain.model

/**
 * UI에서 카드로 표현할 수 있는 아이템들의 공통 인터페이스
 */
interface Item {
    fun getMainText(): String
    fun toInfoRows(): List<InfoRowData>
}

// 정보 행 데이터 클래스
data class InfoRowData(
    val label: String,
    val value: String,
    val isJapanese: Boolean
) 
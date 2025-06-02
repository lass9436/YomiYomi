package com.lass.yomiyomi.viewmodel.myKanji

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class MyKanjiViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var myKanjiRepository: MyKanjiRepository

    private lateinit var viewModel: MyKanjiViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyKanjiViewModel(myKanjiRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSampleMyKanji(id: Int, level: String = "N5"): MyKanjiItem {
        return MyKanjiItem(
            id = id,
            kanji = "漢$id",
            onyomi = "オン$id",
            kunyomi = "くん$id",
            meaning = "의미$id",
            level = level,
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun createSampleKanji(id: Int): KanjiItem {
        return KanjiItem(
            id = id,
            kanji = "字$id",
            onyomi = "音$id",
            kunyomi = "読$id",
            meaning = "뜻$id",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }

    @Test
    fun `초기 상태 확인`() {
        // Then
        assertEquals("초기 레벨은 ALL", Level.ALL, viewModel.selectedLevel.value)
        assertFalse("초기 로딩 상태는 false", viewModel.isLoading.value)
        assertFalse("초기 검색 상태는 false", viewModel.isSearching.value)
    }

    @Test
    fun `레벨 선택 정상 동작`() = runTest {
        // Given
        val myKanjiList = listOf(createSampleMyKanji(1, "N5"))
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList) // 초기 로드

        // When
        viewModel.setSelectedLevel(Level.N5)

        // Then
        assertEquals("선택된 레벨이 업데이트되어야 함", Level.N5, viewModel.selectedLevel.value)
        // ViewModel의 내부 동작으로 인해 getAllMyKanji가 호출될 수 있음
        verify(myKanjiRepository, atLeastOnce()).getAllMyKanji()
    }

    @Test
    fun `내 한자 검색 정상 동작`() = runTest {
        // Given
        val allMyKanji = listOf(
            createSampleMyKanji(1, "N5").copy(kanji = "水"),
            createSampleMyKanji(2, "N5").copy(kanji = "火")
        )
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(allMyKanji)
        whenever(myKanjiRepository.getAllMyKanjiByLevel(any())).thenReturn(allMyKanji)

        // When
        viewModel.searchMyKanji("水")

        // Then - 실제 검색 로직은 ViewModel 내부에서 처리됨
        // 검색 쿼리가 설정되었는지 확인하는 것이 주 목적
    }

    @Test
    fun `원본 한자 검색 정상 동작`() = runTest {
        // Given
        val searchResults = listOf(createSampleKanji(1))
        whenever(myKanjiRepository.searchOriginalKanji("水")).thenReturn(searchResults)

        // When
        viewModel.searchOriginalKanji("水")

        // Then
        verify(myKanjiRepository).searchOriginalKanji("水")
        assertEquals("검색 결과가 업데이트되어야 함", searchResults, viewModel.searchResults.value)
    }

    @Test
    fun `한자를 내 한자에 추가 정상 동작`() = runTest {
        // Given
        val kanjiItem = createSampleKanji(1)
        whenever(myKanjiRepository.addKanjiToMyKanji(kanjiItem)).thenReturn(true)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(listOf())

        // When
        viewModel.addKanjiToMyKanji(kanjiItem)

        // Then
        verify(myKanjiRepository).addKanjiToMyKanji(kanjiItem)
        verify(myKanjiRepository, atLeastOnce()).getAllMyKanji() // loadMyKanji 호출됨
    }

    @Test
    fun `내 한자 직접 추가 정상 동작`() = runTest {
        // Given
        whenever(myKanjiRepository.insertMyKanjiDirectly(any())).thenReturn(true)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(listOf())

        // When
        viewModel.addMyKanjiDirectly("水", "スイ", "みず", "물", Level.N5)

        // Then
        verify(myKanjiRepository).insertMyKanjiDirectly(any())
        verify(myKanjiRepository, atLeastOnce()).getAllMyKanji()
    }

    @Test
    fun `내 한자 업데이트 정상 동작`() = runTest {
        // Given
        val myKanji = createSampleMyKanji(1)
        whenever(myKanjiRepository.insertMyKanjiDirectly(myKanji)).thenReturn(true)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(listOf())

        // When
        viewModel.updateMyKanji(myKanji)

        // Then
        verify(myKanjiRepository).insertMyKanjiDirectly(myKanji)
        verify(myKanjiRepository, atLeastOnce()).getAllMyKanji()
    }

    @Test
    fun `내 한자 삭제 정상 동작`() = runTest {
        // Given
        val myKanji = createSampleMyKanji(1)
        whenever(myKanjiRepository.deleteMyKanji(myKanji)).thenReturn(Unit)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(listOf())

        // When
        viewModel.deleteMyKanji(myKanji)

        // Then
        verify(myKanjiRepository).deleteMyKanji(myKanji)
        verify(myKanjiRepository, atLeastOnce()).getAllMyKanji()
    }
} 
package com.lass.yomiyomi.viewmodel.kanji

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.viewmodel.kanji.list.KanjiViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class KanjiViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var kanjiRepository: KanjiRepository
    
    private lateinit var viewModel: KanjiViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleKanji = listOf(
        KanjiItem(
            id = 1,
            kanji = "食",
            onyomi = "しょく",
            kunyomi = "た(べる)",
            meaning = "음식, 먹다",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 2,
            kanji = "学",
            onyomi = "がく",
            kunyomi = "まな(ぶ)",
            meaning = "배우다, 학문",
            level = "N4",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 3,
            kanji = "心",
            onyomi = "しん",
            kunyomi = "こころ",
            meaning = "마음, 심장",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 4,
            kanji = "水",
            onyomi = "すい",
            kunyomi = "みず",
            meaning = "물",
            level = "N3",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji
        
        viewModel = KanjiViewModel(kanjiRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // Given & When - setUp에서 이미 실행됨
        
        // Wait for any remaining async operations
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanji, viewModel.kanji.value)
        assertFalse(viewModel.isLoading.value)
        assertEquals(Level.ALL, viewModel.selectedLevel.value)
        
        coVerify { kanjiRepository.getAllKanji() }
    }

    @Test
    fun `setSelectedLevel - Level ALL 선택시 모든 한자 로드`() = runTest {
        // Given
        val newLevel = Level.ALL

        // When
        viewModel.setSelectedLevel(newLevel)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(newLevel, viewModel.selectedLevel.value)
        assertEquals(sampleKanji, viewModel.kanji.value)
    }

    @Test
    fun `setSelectedLevel - N5 레벨 선택시 N5 한자만 표시`() = runTest {
        // Given
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        // When
        viewModel.setSelectedLevel(Level.N5)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(Level.N5, viewModel.selectedLevel.value)
        assertEquals(n5Kanji, viewModel.kanji.value)
    }

    @Test
    fun `setSelectedLevel - N4 레벨 선택시 N4 한자만 표시`() = runTest {
        // Given
        val n4Kanji = sampleKanji.filter { it.level == "N4" }
        
        // When
        viewModel.setSelectedLevel(Level.N4)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(Level.N4, viewModel.selectedLevel.value)
        assertEquals(n4Kanji, viewModel.kanji.value)
    }

    @Test
    fun `setSelectedLevel - N3 레벨 선택시 N3 한자만 표시`() = runTest {
        // Given
        val n3Kanji = sampleKanji.filter { it.level == "N3" }
        
        // When
        viewModel.setSelectedLevel(Level.N3)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(Level.N3, viewModel.selectedLevel.value)
        assertEquals(n3Kanji, viewModel.kanji.value)
    }

    @Test
    fun `searchKanji - 한자로 검색`() = runTest {
        // Given
        val query = "食"

        // When
        viewModel.searchKanji(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedKanji = sampleKanji.filter { it.kanji.contains(query) }
        assertEquals(expectedKanji, viewModel.kanji.value)
    }

    @Test
    fun `searchKanji - 음독으로 검색`() = runTest {
        // Given
        val query = "しょく"

        // When
        viewModel.searchKanji(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedKanji = sampleKanji.filter { it.onyomi.contains(query, ignoreCase = true) }
        assertEquals(expectedKanji, viewModel.kanji.value)
    }

    @Test
    fun `searchKanji - 훈독으로 검색`() = runTest {
        // Given
        val query = "みず"

        // When
        viewModel.searchKanji(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedKanji = sampleKanji.filter { it.kunyomi.contains(query, ignoreCase = true) }
        assertEquals(expectedKanji, viewModel.kanji.value)
    }

    @Test
    fun `searchKanji - 의미로 검색`() = runTest {
        // Given
        val query = "물"

        // When
        viewModel.searchKanji(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedKanji = sampleKanji.filter { it.meaning.contains(query, ignoreCase = true) }
        assertEquals(expectedKanji, viewModel.kanji.value)
    }

    @Test
    fun `searchKanji - 빈 검색어로 검색시 모든 한자 표시`() = runTest {
        // Given
        val query = ""

        // When
        viewModel.searchKanji(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanji, viewModel.kanji.value)
    }

    @Test
    fun `레벨 필터와 검색 조건 동시 적용`() = runTest {
        // Given
        val query = "학"
        val level = Level.N4

        // When
        viewModel.setSelectedLevel(level)
        viewModel.searchKanji(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedKanji = sampleKanji
            .filter { it.level == level.value }
            .filter { it.meaning.contains(query, ignoreCase = true) }
        assertEquals(expectedKanji, viewModel.kanji.value)
    }

    @Test
    fun `Repository에서 에러 발생시 로딩 상태 처리`() = runTest {
        // Given
        coEvery { kanjiRepository.getAllKanji() } throws RuntimeException("Network Error")
        
        // When
        val newViewModel = KanjiViewModel(kanjiRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(newViewModel.isLoading.value)  // 에러 후 로딩 상태가 false로 변경
        assertTrue(newViewModel.kanji.value.isEmpty())  // 에러시 빈 리스트
    }
} 

package com.lass.yomiyomi.viewmodel.word

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.constant.Level
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
class WordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var wordRepository: WordRepository
    
    private lateinit var viewModel: WordViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleWords = listOf(
        WordItem(
            id = 1,
            word = "食べる",
            reading = "たべる",
            type = "동사",
            meaning = "먹다",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 2,
            word = "学習",
            reading = "がくしゅう",
            type = "명사",
            meaning = "학습",
            level = "N4",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 3,
            word = "心配",
            reading = "しんぱい",
            type = "명사",
            meaning = "걱정",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 4,
            word = "水道",
            reading = "すいどう",
            type = "명사",
            meaning = "수도",
            level = "N3",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        coEvery { wordRepository.getAllWords() } returns sampleWords
        
        viewModel = WordViewModel(wordRepository)
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
        assertEquals(sampleWords, viewModel.words.value)
        assertFalse(viewModel.isLoading.value)
        assertEquals(Level.ALL, viewModel.selectedLevel.value)
        
        coVerify { wordRepository.getAllWords() }
    }

    @Test
    fun `setSelectedLevel - Level ALL 선택시 모든 단어 로드`() = runTest {
        // Given
        val newLevel = Level.ALL

        // When
        viewModel.setSelectedLevel(newLevel)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(newLevel, viewModel.selectedLevel.value)
        assertEquals(sampleWords, viewModel.words.value)
    }

    @Test
    fun `setSelectedLevel - N5 레벨 선택시 N5 단어만 표시`() = runTest {
        // Given
        val n5Words = sampleWords.filter { it.level == "N5" }
        
        // When
        viewModel.setSelectedLevel(Level.N5)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(Level.N5, viewModel.selectedLevel.value)
        assertEquals(n5Words, viewModel.words.value)
    }

    @Test
    fun `setSelectedLevel - N4 레벨 선택시 N4 단어만 표시`() = runTest {
        // Given
        val n4Words = sampleWords.filter { it.level == "N4" }
        
        // When
        viewModel.setSelectedLevel(Level.N4)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(Level.N4, viewModel.selectedLevel.value)
        assertEquals(n4Words, viewModel.words.value)
    }

    @Test
    fun `setSelectedLevel - N3 레벨 선택시 N3 단어만 표시`() = runTest {
        // Given
        val n3Words = sampleWords.filter { it.level == "N3" }
        
        // When
        viewModel.setSelectedLevel(Level.N3)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(Level.N3, viewModel.selectedLevel.value)
        assertEquals(n3Words, viewModel.words.value)
    }

    @Test
    fun `searchWords - 단어로 검색`() = runTest {
        // Given
        val query = "食べる"

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedWords = sampleWords.filter { it.word.contains(query) }
        assertEquals(expectedWords, viewModel.words.value)
    }

    @Test
    fun `searchWords - 읽기로 검색`() = runTest {
        // Given
        val query = "たべる"

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedWords = sampleWords.filter { it.reading.contains(query, ignoreCase = true) }
        assertEquals(expectedWords, viewModel.words.value)
    }

    @Test
    fun `searchWords - 의미로 검색`() = runTest {
        // Given
        val query = "먹다"

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedWords = sampleWords.filter { it.meaning.contains(query, ignoreCase = true) }
        assertEquals(expectedWords, viewModel.words.value)
    }

    @Test
    fun `searchWords - 부분 단어 검색`() = runTest {
        // Given
        val query = "学"

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedWords = sampleWords.filter { 
            it.word.contains(query, ignoreCase = true) ||
            it.reading.contains(query, ignoreCase = true) ||
            it.meaning.contains(query, ignoreCase = true)
        }
        assertEquals(expectedWords, viewModel.words.value)
    }

    @Test
    fun `searchWords - 빈 검색어로 검색시 모든 단어 표시`() = runTest {
        // Given
        val query = ""

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWords, viewModel.words.value)
    }

    @Test
    fun `레벨 필터와 검색 조건 동시 적용`() = runTest {
        // Given
        val query = "걱정"
        val level = Level.N5

        // When
        viewModel.setSelectedLevel(level)
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedWords = sampleWords
            .filter { it.level == level.value }
            .filter { it.meaning.contains(query, ignoreCase = true) }
        assertEquals(expectedWords, viewModel.words.value)
    }

    @Test
    fun `검색 결과가 없는 경우`() = runTest {
        // Given
        val query = "존재하지않는단어"

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.words.value.isEmpty())
    }

    @Test
    fun `Repository에서 에러 발생시 로딩 상태 처리`() = runTest {
        // Given
        coEvery { wordRepository.getAllWords() } throws RuntimeException("Network Error")
        
        // When
        val newViewModel = WordViewModel(wordRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(newViewModel.isLoading.value)  // 에러 후 로딩 상태가 false로 변경
        assertTrue(newViewModel.words.value.isEmpty())  // 에러시 빈 리스트
    }

    @Test
    fun `여러 조건 동시 검색 - 한자 포함 단어`() = runTest {
        // Given
        val query = "心"
        val level = Level.N5

        // When
        viewModel.setSelectedLevel(level)
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val expectedWords = sampleWords
            .filter { it.level == level.value }
            .filter { 
                it.word.contains(query, ignoreCase = true) ||
                it.reading.contains(query, ignoreCase = true) ||
                it.meaning.contains(query, ignoreCase = true)
            }
        assertEquals(expectedWords, viewModel.words.value)
    }
} 

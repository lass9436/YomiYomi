package com.lass.yomiyomi.viewmodel.word.random

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.entity.WordItem
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
class WordRandomViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: WordRepository
    
    private lateinit var viewModel: WordRandomViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleWordItem = WordItem(
        id = 1,
        word = "学校",
        reading = "がっこう",
        meaning = "학교",
        type = "명사",
        level = "N5",
        learningWeight = 0.5f,
        timestamp = System.currentTimeMillis()
    )

    private val sampleWordItemN4 = WordItem(
        id = 2,
        word = "勉強",
        reading = "べんきょう",
        meaning = "공부",
        type = "명사",
        level = "N4",
        learningWeight = 0.3f,
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = WordRandomViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // Given & When - 초기 상태

        // Then
        assertNull(viewModel.randomWord.value)
    }

    @Test
    fun `fetchRandomWord - 랜덤 단어 가져오기 성공`() = runTest {
        // Given
        coEvery { repository.getRandomWord() } returns sampleWordItem

        // When
        viewModel.fetchRandomWord()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordItem, viewModel.randomWord.value)
        coVerify { repository.getRandomWord() }
    }

    @Test
    fun `fetchRandomWord - 랜덤 단어 가져오기 실패 (null 반환)`() = runTest {
        // Given
        coEvery { repository.getRandomWord() } returns null

        // When
        viewModel.fetchRandomWord()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.randomWord.value)
        coVerify { repository.getRandomWord() }
    }

    @Test
    fun `fetchRandomWordByLevel - 특정 레벨의 랜덤 단어 가져오기 성공`() = runTest {
        // Given
        val level = "N5"
        coEvery { repository.getRandomWordByLevel(level) } returns sampleWordItem

        // When
        viewModel.fetchRandomWordByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordItem, viewModel.randomWord.value)
        coVerify { repository.getRandomWordByLevel(level) }
    }

    @Test
    fun `fetchRandomWordByLevel - 특정 레벨의 랜덤 단어 가져오기 실패 (null 반환)`() = runTest {
        // Given
        val level = "N1"
        coEvery { repository.getRandomWordByLevel(level) } returns null

        // When
        viewModel.fetchRandomWordByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.randomWord.value)
        coVerify { repository.getRandomWordByLevel(level) }
    }

    @Test
    fun `fetchRandomWordByLevel - null 레벨로 호출`() = runTest {
        // Given
        coEvery { repository.getRandomWordByLevel(null) } returns sampleWordItem

        // When
        viewModel.fetchRandomWordByLevel(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordItem, viewModel.randomWord.value)
        coVerify { repository.getRandomWordByLevel(null) }
    }

    @Test
    fun `여러 번 호출시 각각 독립적으로 동작`() = runTest {
        // Given
        coEvery { repository.getRandomWord() } returns sampleWordItem
        coEvery { repository.getRandomWordByLevel("N4") } returns sampleWordItemN4

        // When
        viewModel.fetchRandomWord()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(sampleWordItem, viewModel.randomWord.value)

        viewModel.fetchRandomWordByLevel("N4")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordItemN4, viewModel.randomWord.value)
        
        coVerify { repository.getRandomWord() }
        coVerify { repository.getRandomWordByLevel("N4") }
    }

    @Test
    fun `repository 예외 발생시 처리`() = runTest {
        // Given
        coEvery { repository.getRandomWord() } throws Exception("Database error")

        // When
        viewModel.fetchRandomWord()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - 예외가 발생해도 앱이 크래시되지 않고 상태가 null로 유지됨
        assertNull(viewModel.randomWord.value)
        
        coVerify { repository.getRandomWord() }
    }

    private fun assertDoesNotThrow(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            fail("예외가 발생하지 않아야 함: ${e.message}")
        }
    }
} 

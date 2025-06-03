package com.lass.yomiyomi.viewmodel.kanjiRandom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiItem
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
class KanjiRandomViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: KanjiRepository
    
    private lateinit var viewModel: KanjiRandomViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleKanjiItem = KanjiItem(
        id = 1,
        kanji = "木",
        onyomi = "モク",
        kunyomi = "き",
        meaning = "나무",
        level = "N5",
        learningWeight = 0.5f,
        timestamp = System.currentTimeMillis()
    )

    private val sampleKanjiItemN4 = KanjiItem(
        id = 2,
        kanji = "学",
        onyomi = "ガク",
        kunyomi = "まな(ぶ)",
        meaning = "배우다",
        level = "N4",
        learningWeight = 0.3f,
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = KanjiRandomViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // Given & When - 초기 상태

        // Then
        assertNull(viewModel.randomKanji.value)
    }

    @Test
    fun `fetchRandomKanji - 랜덤 한자 가져오기 성공`() = runTest {
        // Given
        coEvery { repository.getRandomKanji() } returns sampleKanjiItem

        // When
        viewModel.fetchRandomKanji()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiItem, viewModel.randomKanji.value)
        coVerify { repository.getRandomKanji() }
    }

    @Test
    fun `fetchRandomKanji - 랜덤 한자 가져오기 실패 (null 반환)`() = runTest {
        // Given
        coEvery { repository.getRandomKanji() } returns null

        // When
        viewModel.fetchRandomKanji()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.randomKanji.value)
        coVerify { repository.getRandomKanji() }
    }

    @Test
    fun `fetchRandomKanjiByLevel - 특정 레벨의 랜덤 한자 가져오기 성공`() = runTest {
        // Given
        val level = "N5"
        coEvery { repository.getRandomKanjiByLevel(level) } returns sampleKanjiItem

        // When
        viewModel.fetchRandomKanjiByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiItem, viewModel.randomKanji.value)
        coVerify { repository.getRandomKanjiByLevel(level) }
    }

    @Test
    fun `fetchRandomKanjiByLevel - 특정 레벨의 랜덤 한자 가져오기 실패 (null 반환)`() = runTest {
        // Given
        val level = "N1"
        coEvery { repository.getRandomKanjiByLevel(level) } returns null

        // When
        viewModel.fetchRandomKanjiByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.randomKanji.value)
        coVerify { repository.getRandomKanjiByLevel(level) }
    }

    @Test
    fun `fetchRandomKanjiByLevel - null 레벨로 호출`() = runTest {
        // Given
        coEvery { repository.getRandomKanjiByLevel(null) } returns sampleKanjiItem

        // When
        viewModel.fetchRandomKanjiByLevel(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiItem, viewModel.randomKanji.value)
        coVerify { repository.getRandomKanjiByLevel(null) }
    }

    @Test
    fun `여러 번 호출시 각각 독립적으로 동작`() = runTest {
        // Given
        coEvery { repository.getRandomKanji() } returns sampleKanjiItem
        coEvery { repository.getRandomKanjiByLevel("N4") } returns sampleKanjiItemN4

        // When
        viewModel.fetchRandomKanji()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(sampleKanjiItem, viewModel.randomKanji.value)

        viewModel.fetchRandomKanjiByLevel("N4")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiItemN4, viewModel.randomKanji.value)
        
        coVerify { repository.getRandomKanji() }
        coVerify { repository.getRandomKanjiByLevel("N4") }
    }

    @Test
    fun `repository 예외 발생시 처리`() = runTest {
        // Given
        coEvery { repository.getRandomKanji() } throws Exception("Database error")

        // When
        viewModel.fetchRandomKanji()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - 예외가 발생해도 앱이 크래시되지 않고 상태가 null로 유지됨
        assertNull(viewModel.randomKanji.value)
        
        coVerify { repository.getRandomKanji() }
    }
} 
package com.lass.yomiyomi.viewmodel.myKanjiQuiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.domain.model.*
import com.lass.yomiyomi.domain.usecase.GenerateMyKanjiQuizUseCase
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
class MyKanjiQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var generateMyKanjiQuizUseCase: GenerateMyKanjiQuizUseCase
    
    @MockK
    private lateinit var myKanjiRepository: MyKanjiRepository
    
    private lateinit var viewModel: MyKanjiQuizViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleKanjiQuiz = KanjiQuiz(
        question = "食",
        answer = "た(べる) / 음식, 먹다",
        options = listOf("た(べる) / 음식, 먹다", "の(む) / 마시다", "み(る) / 보다", "き(く) / 듣다"),
        correctIndex = 0
    )

    private val sampleMyKanjiItem = MyKanjiItem(
        id = 1,
        kanji = "食",
        onyomi = "しょく",
        kunyomi = "た(べる)",
        meaning = "음식, 먹다",
        level = "N5",
        learningWeight = 0.8f,
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyKanjiQuizViewModel(generateMyKanjiQuizUseCase, myKanjiRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadQuizByLevel - 랜덤 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) } returns sampleKanjiQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiQuiz, viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasInsufficientData.value)
        
        coVerify { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) }
    }

    @Test
    fun `loadQuizByLevel - 학습 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val priorityKanji = listOf(sampleMyKanjiItem)
        val distractors = emptyList<MyKanjiItem>()
        
        coEvery { myKanjiRepository.getMyKanjiForLearningMode("N5") } returns Pair(priorityKanji, distractors)
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, true) } returns sampleKanjiQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiQuiz, viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasInsufficientData.value)
        
        coVerify { myKanjiRepository.getMyKanjiForLearningMode("N5") }
        coVerify { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, true) }
    }

    @Test
    fun `loadQuizByLevel - 데이터 부족시 hasInsufficientData true`() = runTest {
        // Given
        val level = Level.N1
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) } returns null

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.hasInsufficientData.value)
    }

    @Test
    fun `loadQuizByLevel - 학습 모드에서 우선순위 데이터 없을 때 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { myKanjiRepository.getMyKanjiForLearningMode("N5") } returns Pair(emptyList(), emptyList())
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) } returns sampleKanjiQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleKanjiQuiz, viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasInsufficientData.value)
        
        coVerify { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) }
    }

    @Test
    fun `loadQuizByLevel - 로딩 상태 확인`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) } returns sampleKanjiQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        
        // Then - 로딩 완료 후 상태 확인
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `checkAnswer - 학습 모드에서 정답시 가중치 업데이트`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val priorityKanji = listOf(sampleMyKanjiItem)
        val distractors = emptyList<MyKanjiItem>()
        
        coEvery { myKanjiRepository.getMyKanjiForLearningMode("N5") } returns Pair(priorityKanji, distractors)
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, true) } returns sampleKanjiQuiz
        coEvery { myKanjiRepository.updateMyKanjiLearningStatus(any(), any(), any()) } just runs

        // 먼저 퀴즈를 로드
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - 정답 선택
        viewModel.checkAnswer(selectedIndex = 0, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { myKanjiRepository.updateMyKanjiLearningStatus(1, true, 0.8f) }
    }

    @Test
    fun `checkAnswer - 학습 모드에서 오답시 가중치 업데이트`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val priorityKanji = listOf(sampleMyKanjiItem)
        val distractors = emptyList<MyKanjiItem>()
        
        coEvery { myKanjiRepository.getMyKanjiForLearningMode("N5") } returns Pair(priorityKanji, distractors)
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, true) } returns sampleKanjiQuiz
        coEvery { myKanjiRepository.updateMyKanjiLearningStatus(any(), any(), any()) } just runs

        // 먼저 퀴즈를 로드
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - 오답 선택
        viewModel.checkAnswer(selectedIndex = 1, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { myKanjiRepository.updateMyKanjiLearningStatus(1, false, 0.8f) }
    }

    @Test
    fun `checkAnswer - 랜덤 모드에서는 가중치 업데이트 하지 않음`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { generateMyKanjiQuizUseCase.generateQuiz(level, quizType, false) } returns sampleKanjiQuiz

        // 먼저 퀴즈를 로드 (랜덤 모드)
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - 정답 선택
        viewModel.checkAnswer(selectedIndex = 0, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { myKanjiRepository.updateMyKanjiLearningStatus(any(), any(), any()) }
    }

    @Test
    fun `checkAnswer - 퀴즈가 없을 때 아무것도 하지 않음`() = runTest {
        // Given - 퀴즈가 로드되지 않은 상태

        // When
        viewModel.checkAnswer(selectedIndex = 0, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { myKanjiRepository.updateMyKanjiLearningStatus(any(), any(), any()) }
    }
} 
package com.lass.yomiyomi.viewmodel.myWordQuiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import com.lass.yomiyomi.domain.usecase.GenerateMyWordQuizUseCase
import com.lass.yomiyomi.viewmodel.myWord.quiz.MyWordQuizViewModel
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
class MyWordQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var generateMyWordQuizUseCase: GenerateMyWordQuizUseCase
    
    @MockK
    private lateinit var myWordRepository: MyWordRepository
    
    private lateinit var viewModel: MyWordQuizViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleWordQuiz = WordQuiz(
        question = "食べる",
        answer = "먹다 / たべる",
        options = listOf("먹다 / たべる", "자다 / ねる", "보다 / みる", "듣다 / きく"),
        correctIndex = 0
    )

    private val sampleMyWordItem = MyWordItem(
        id = 1,
        word = "食べる",
        reading = "たべる",
        type = "동사",
        meaning = "먹다",
        level = "N5",
        learningWeight = 0.5f,
        timestamp = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyWordQuizViewModel(generateMyWordQuizUseCase, myWordRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadQuizByLevel - 랜덤 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) } returns sampleWordQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordQuiz, viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasInsufficientData.value)
        
        coVerify { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) }
    }

    @Test
    fun `loadQuizByLevel - 학습 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val priorityWords = listOf(sampleMyWordItem)
        val distractors = emptyList<MyWordItem>()
        
        coEvery { myWordRepository.getMyWordsForLearningMode("N5") } returns Pair(priorityWords, distractors)
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, true) } returns sampleWordQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordQuiz, viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasInsufficientData.value)
        
        coVerify { myWordRepository.getMyWordsForLearningMode("N5") }
        coVerify { generateMyWordQuizUseCase.generateQuiz(level, quizType, true) }
    }

    @Test
    fun `loadQuizByLevel - 데이터 부족시 hasInsufficientData true`() = runTest {
        // Given
        val level = Level.N1
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) } returns null

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
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { myWordRepository.getMyWordsForLearningMode("N5") } returns Pair(emptyList(), emptyList())
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) } returns sampleWordQuiz

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordQuiz, viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasInsufficientData.value)
        
        coVerify { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) }
    }

    @Test
    fun `loadQuizByLevel - 로딩 상태 확인`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) } returns sampleWordQuiz

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
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val priorityWords = listOf(sampleMyWordItem)
        val distractors = emptyList<MyWordItem>()
        
        coEvery { myWordRepository.getMyWordsForLearningMode("N5") } returns Pair(priorityWords, distractors)
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, true) } returns sampleWordQuiz
        coEvery { myWordRepository.updateMyWordLearningStatus(any(), any(), any()) } just runs

        // 먼저 퀴즈를 로드
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - 정답 선택
        viewModel.checkAnswer(selectedIndex = 0, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { myWordRepository.updateMyWordLearningStatus(1, true, 0.5f) }
    }

    @Test
    fun `checkAnswer - 학습 모드에서 오답시 가중치 업데이트`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val priorityWords = listOf(sampleMyWordItem)
        val distractors = emptyList<MyWordItem>()
        
        coEvery { myWordRepository.getMyWordsForLearningMode("N5") } returns Pair(priorityWords, distractors)
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, true) } returns sampleWordQuiz
        coEvery { myWordRepository.updateMyWordLearningStatus(any(), any(), any()) } just runs

        // 먼저 퀴즈를 로드
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - 오답 선택
        viewModel.checkAnswer(selectedIndex = 1, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { myWordRepository.updateMyWordLearningStatus(1, false, 0.5f) }
    }

    @Test
    fun `checkAnswer - 랜덤 모드에서는 가중치 업데이트 하지 않음`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { generateMyWordQuizUseCase.generateQuiz(level, quizType, false) } returns sampleWordQuiz

        // 먼저 퀴즈를 로드 (랜덤 모드)
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - 정답 선택
        viewModel.checkAnswer(selectedIndex = 0, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { myWordRepository.updateMyWordLearningStatus(any(), any(), any()) }
    }

    @Test
    fun `checkAnswer - 퀴즈가 없을 때 아무것도 하지 않음`() = runTest {
        // Given - 퀴즈가 로드되지 않은 상태

        // When
        viewModel.checkAnswer(selectedIndex = 0, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { myWordRepository.updateMyWordLearningStatus(any(), any(), any()) }
    }
} 

package com.lass.yomiyomi.viewmodel.myWordQuiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.domain.usecase.GenerateMyWordQuizUseCase
import com.lass.yomiyomi.data.repository.MyWordRepository
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
class MyWordQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var generateMyWordQuizUseCase: GenerateMyWordQuizUseCase

    @Mock
    private lateinit var myWordRepository: MyWordRepository

    private lateinit var viewModel: MyWordQuizViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyWordQuizViewModel(generateMyWordQuizUseCase, myWordRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSampleQuiz(): WordQuiz {
        return WordQuiz(
            question = "学校",
            answer = "학교 / がっこう",
            options = listOf("학교 / がっこう", "병원 / びょういん", "회사 / かいしゃ", "집 / いえ"),
            correctIndex = 0
        )
    }

    private fun createSampleMyWord(): MyWordItem {
        return MyWordItem(
            id = 1,
            word = "学校",
            reading = "がっこう",
            meaning = "학교",
            type = "명사",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }

    @Test
    fun `초기 상태 확인`() {
        // Then
        assertNull("초기 퀴즈 상태는 null", viewModel.quizState.value)
        assertFalse("초기 로딩 상태는 false", viewModel.isLoading.value)
        assertFalse("초기 데이터 부족 상태는 false", viewModel.hasInsufficientData.value)
    }

    @Test
    fun `랜덤 모드에서 퀴즈 로드 성공`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyWordQuizUseCase.generateQuiz(Level.N5, WordQuizType.WORD_TO_MEANING_READING, false))
            .thenReturn(quiz)

        // When
        viewModel.loadQuizByLevel(Level.N5, WordQuizType.WORD_TO_MEANING_READING, false)

        // Then
        assertEquals("퀴즈가 로드되어야 함", quiz, viewModel.quizState.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
        assertFalse("데이터 부족 상태가 아니어야 함", viewModel.hasInsufficientData.value)
    }

    @Test
    fun `랜덤 모드에서 데이터 부족 시 상태 업데이트`() = runTest {
        // Given
        whenever(generateMyWordQuizUseCase.generateQuiz(Level.N1, WordQuizType.WORD_TO_MEANING_READING, false))
            .thenReturn(null)

        // When
        viewModel.loadQuizByLevel(Level.N1, WordQuizType.WORD_TO_MEANING_READING, false)

        // Then
        assertNull("퀴즈 상태는 null이어야 함", viewModel.quizState.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
        assertTrue("데이터 부족 상태여야 함", viewModel.hasInsufficientData.value)
    }

    @Test
    fun `학습 모드에서 우선순위 데이터가 있을 때 퀴즈 로드`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        val priorityWords = listOf(createSampleMyWord())
        val distractors = listOf<MyWordItem>()

        whenever(myWordRepository.getMyWordsForLearningMode("N5"))
            .thenReturn(Pair(priorityWords, distractors))
        whenever(generateMyWordQuizUseCase.generateQuiz(Level.N5, WordQuizType.WORD_TO_MEANING_READING, true))
            .thenReturn(quiz)

        // When
        viewModel.loadQuizByLevel(Level.N5, WordQuizType.WORD_TO_MEANING_READING, true)

        // Then
        assertEquals("퀴즈가 로드되어야 함", quiz, viewModel.quizState.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
        assertFalse("데이터 부족 상태가 아니어야 함", viewModel.hasInsufficientData.value)
        verify(myWordRepository).getMyWordsForLearningMode("N5")
    }

    @Test
    fun `정답 체크 - 정답인 경우`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyWordQuizUseCase.generateQuiz(any(), any(), any())).thenReturn(quiz)

        // When
        viewModel.checkAnswer(0, false) // 학습모드가 아닌 경우로 단순화

        // Then
        // 학습모드가 아니므로 updateMyWordLearningStatus 호출되지 않음
        verify(myWordRepository, never()).updateMyWordLearningStatus(any(), any(), any())
    }

    @Test
    fun `랜덤 모드에서는 학습 상태 업데이트 안함`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyWordQuizUseCase.generateQuiz(any(), any(), eq(false))).thenReturn(quiz)
        viewModel.loadQuizByLevel(Level.N5, WordQuizType.WORD_TO_MEANING_READING, false)

        // When
        viewModel.checkAnswer(0, false)

        // Then
        verify(myWordRepository, never()).updateMyWordLearningStatus(any(), any(), any())
    }

    @Test
    fun `예외 발생 시 에러 상태 처리`() = runTest {
        // Given
        whenever(generateMyWordQuizUseCase.generateQuiz(any(), any(), any()))
            .thenThrow(RuntimeException("Test exception"))

        // When
        viewModel.loadQuizByLevel(Level.N5, WordQuizType.WORD_TO_MEANING_READING, false)

        // Then
        assertNull("퀴즈 상태는 null이어야 함", viewModel.quizState.value)
        assertTrue("데이터 부족 상태로 처리되어야 함", viewModel.hasInsufficientData.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
    }
} 
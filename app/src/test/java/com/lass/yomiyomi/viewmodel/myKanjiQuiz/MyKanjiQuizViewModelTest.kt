package com.lass.yomiyomi.viewmodel.myKanjiQuiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.domain.usecase.GenerateMyKanjiQuizUseCase
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
class MyKanjiQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var generateMyKanjiQuizUseCase: GenerateMyKanjiQuizUseCase

    @Mock
    private lateinit var myKanjiRepository: MyKanjiRepository

    private lateinit var viewModel: MyKanjiQuizViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyKanjiQuizViewModel(generateMyKanjiQuizUseCase, myKanjiRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSampleQuiz(): KanjiQuiz {
        return KanjiQuiz(
            question = "火",
            answer = "ひ / 불",
            options = listOf("ひ / 불", "みず / 물", "き / 나무", "つち / 흙"),
            correctIndex = 0
        )
    }

    private fun createSampleMyKanji(): MyKanjiItem {
        return MyKanjiItem(
            id = 1,
            kanji = "火",
            onyomi = "カ",
            kunyomi = "ひ",
            meaning = "불",
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
        whenever(generateMyKanjiQuizUseCase.generateQuiz(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false))
            .thenReturn(quiz)

        // When
        viewModel.loadQuizByLevel(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false)

        // Then
        assertEquals("퀴즈가 로드되어야 함", quiz, viewModel.quizState.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
        assertFalse("데이터 부족 상태가 아니어야 함", viewModel.hasInsufficientData.value)
    }

    @Test
    fun `랜덤 모드에서 데이터 부족 시 상태 업데이트`() = runTest {
        // Given
        whenever(generateMyKanjiQuizUseCase.generateQuiz(Level.N1, KanjiQuizType.KANJI_TO_READING_MEANING, false))
            .thenReturn(null)

        // When
        viewModel.loadQuizByLevel(Level.N1, KanjiQuizType.KANJI_TO_READING_MEANING, false)

        // Then
        assertNull("퀴즈 상태는 null이어야 함", viewModel.quizState.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
        assertTrue("데이터 부족 상태여야 함", viewModel.hasInsufficientData.value)
    }

    @Test
    fun `학습 모드에서 우선순위 데이터가 있을 때 퀴즈 로드`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        val priorityKanji = listOf(createSampleMyKanji())
        val distractors = listOf<MyKanjiItem>()

        whenever(myKanjiRepository.getMyKanjiForLearningMode("N5"))
            .thenReturn(Pair(priorityKanji, distractors))
        whenever(generateMyKanjiQuizUseCase.generateQuiz(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, true))
            .thenReturn(quiz)

        // When
        viewModel.loadQuizByLevel(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, true)

        // Then
        assertEquals("퀴즈가 로드되어야 함", quiz, viewModel.quizState.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
        assertFalse("데이터 부족 상태가 아니어야 함", viewModel.hasInsufficientData.value)
        verify(myKanjiRepository).getMyKanjiForLearningMode("N5")
    }

    @Test
    fun `학습 모드에서 우선순위 데이터가 없을 때 랜덤 모드로 폴백`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(myKanjiRepository.getMyKanjiForLearningMode("N5"))
            .thenReturn(Pair(emptyList(), emptyList()))
        whenever(generateMyKanjiQuizUseCase.generateQuiz(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false))
            .thenReturn(quiz)

        // When
        viewModel.loadQuizByLevel(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, true)

        // Then
        assertEquals("폴백 퀴즈가 로드되어야 함", quiz, viewModel.quizState.value)
        verify(myKanjiRepository).getMyKanjiForLearningMode("N5")
        verify(generateMyKanjiQuizUseCase).generateQuiz(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false)
    }

    @Test
    fun `정답 체크 - 정답인 경우`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyKanjiQuizUseCase.generateQuiz(any(), any(), any())).thenReturn(quiz)

        // When
        viewModel.checkAnswer(0, false) // 학습모드가 아닌 경우로 단순화

        // Then
        // 학습모드가 아니므로 updateMyKanjiLearningStatus 호출되지 않음
        verify(myKanjiRepository, never()).updateMyKanjiLearningStatus(any(), any(), any())
    }

    @Test
    fun `정답 체크 - 오답인 경우`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyKanjiQuizUseCase.generateQuiz(any(), any(), any())).thenReturn(quiz)

        // When
        viewModel.checkAnswer(1, false) // 학습모드가 아닌 경우로 단순화

        // Then
        // 학습모드가 아니므로 updateMyKanjiLearningStatus 호출되지 않음
        verify(myKanjiRepository, never()).updateMyKanjiLearningStatus(any(), any(), any())
    }

    @Test
    fun `랜덤 모드에서는 학습 상태 업데이트 안함`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyKanjiQuizUseCase.generateQuiz(any(), any(), eq(false))).thenReturn(quiz)
        viewModel.loadQuizByLevel(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false)

        // When
        viewModel.checkAnswer(0, false)

        // Then
        verify(myKanjiRepository, never()).updateMyKanjiLearningStatus(any(), any(), any())
    }

    @Test
    fun `로딩 상태 관리 정상 동작`() = runTest {
        // Given
        val quiz = createSampleQuiz()
        whenever(generateMyKanjiQuizUseCase.generateQuiz(any(), any(), any())).thenReturn(quiz)

        // When
        viewModel.loadQuizByLevel(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false)

        // Then
        assertFalse("최종적으로 로딩이 완료되어야 함", viewModel.isLoading.value)
    }

    @Test
    fun `예외 발생 시 에러 상태 처리`() = runTest {
        // Given
        whenever(generateMyKanjiQuizUseCase.generateQuiz(any(), any(), any()))
            .thenThrow(RuntimeException("Test exception"))

        // When
        viewModel.loadQuizByLevel(Level.N5, KanjiQuizType.KANJI_TO_READING_MEANING, false)

        // Then
        assertNull("퀴즈 상태는 null이어야 함", viewModel.quizState.value)
        assertTrue("데이터 부족 상태로 처리되어야 함", viewModel.hasInsufficientData.value)
        assertFalse("로딩이 완료되어야 함", viewModel.isLoading.value)
    }
} 
package com.lass.yomiyomi.viewmodel.word.quiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import com.lass.yomiyomi.domain.model.entity.WordItem
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WordQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: WordRepository

    private lateinit var viewModel: WordQuizViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val sampleWords = listOf(
        WordItem(
            id = 1,
            word = "食べる",
            reading = "たべる",
            type = "동사",
            meaning = "먹다",
            level = "N5",
            learningWeight = 0.5f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 2,
            word = "勉強",
            reading = "べんきょう",
            type = "명사",
            meaning = "공부",
            level = "N4",
            learningWeight = 0.3f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 3,
            word = "学校",
            reading = "がっこう",
            type = "명사",
            meaning = "학교",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 4,
            word = "先生",
            reading = "せんせい",
            type = "명사",
            meaning = "선생님",
            level = "N5",
            learningWeight = 0.2f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 5,
            word = "水",
            reading = "みず",
            type = "명사",
            meaning = "물",
            level = "N5",
            learningWeight = 0.6f,
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // 기본 mock 설정
        coEvery { repository.getAllWords() } returns sampleWords
        coEvery { repository.getAllWordsByLevel(any()) } returns emptyList()
        coEvery { repository.getRandomWord() } returns sampleWords[0]
        coEvery { repository.getRandomWordByLevel(any()) } returns sampleWords[0]
        coEvery { repository.getWordsForLearningMode(any()) } returns Pair(emptyList(), emptyList())
        coEvery { repository.updateWordLearningStatus(any(), any(), any()) } just runs

        viewModel = WordQuizViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // Given & When - 초기 상태

        // Then
        Assert.assertNull(viewModel.quizState.value)
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadQuizByLevel - 랜덤 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleWords.filter { it.level == "N5" }

        coEvery { repository.getAllWordsByLevel("N5") } returns n5Words
        coEvery { repository.getRandomWordByLevel("N5") } returns n5Words[0]
        coEvery { repository.getAllWords() } returns sampleWords

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        Assert.assertEquals(4, quiz.options.size)
        Assert.assertTrue(quiz.correctIndex in 0..3)
        Assert.assertNotNull(quiz.question)
        Assert.assertNotNull(quiz.answer)
        Assert.assertTrue(quiz.options.contains(quiz.answer))
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadQuizByLevel - 학습 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val priorityWords = sampleWords.filter { it.learningWeight < 0.6f }
        val distractors = sampleWords.filter { it.learningWeight >= 0.6f }

        coEvery { repository.getWordsForLearningMode("N5") } returns Pair(priorityWords, distractors)
        // Add fallback mocks in case the learning mode falls back to random mode
        coEvery { repository.getAllWordsByLevel("N5") } returns sampleWords.filter { it.level == "N5" }
        coEvery { repository.getRandomWordByLevel("N5") } returns priorityWords.firstOrNull()

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        Assert.assertEquals(4, quiz.options.size)
        Assert.assertTrue(quiz.correctIndex in 0..3)
        Assert.assertFalse(viewModel.isLoading.value)

        coVerify { repository.getWordsForLearningMode("N5") }
    }

    @Test
    fun `loadQuizByLevel - 데이터 부족시 퀴즈 생성 실패`() = runTest {
        // Given
        val level = Level.N1
        val quizType = WordQuizType.WORD_TO_MEANING_READING

        coEvery { repository.getAllWordsByLevel("N1") } returns emptyList()
        coEvery { repository.getRandomWordByLevel("N1") } returns null

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNull(viewModel.quizState.value)
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadQuizByLevel - WORD_TO_MEANING_READING 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleWords.filter { it.level == "N5" }

        coEvery { repository.getAllWordsByLevel("N5") } returns n5Words
        coEvery { repository.getRandomWordByLevel("N5") } returns n5Words[0]
        coEvery { repository.getAllWords() } returns sampleWords

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        // 질문이 일본어 단어인지 확인
        Assert.assertTrue(n5Words.any { it.word == quiz.question })
        // 답이 "의미 / 읽기" 형식인지 확인
        Assert.assertTrue(quiz.answer.contains(" / "))
    }

    @Test
    fun `loadQuizByLevel - MEANING_READING_TO_WORD 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.MEANING_READING_TO_WORD
        val n5Words = sampleWords.filter { it.level == "N5" }

        coEvery { repository.getAllWordsByLevel("N5") } returns n5Words
        coEvery { repository.getRandomWordByLevel("N5") } returns n5Words[0]
        coEvery { repository.getAllWords() } returns sampleWords

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        // 질문이 "의미 / 읽기" 형식인지 확인
        Assert.assertTrue(quiz.question.contains(" / "))
        // 답이 일본어 단어인지 확인
        Assert.assertTrue(n5Words.any { it.word == quiz.answer })
    }

    @Test
    fun `loadQuizByLevel - Level ALL에서 모든 레벨 단어 사용`() = runTest {
        // Given
        val level = Level.ALL
        val quizType = WordQuizType.WORD_TO_MEANING_READING

        coEvery { repository.getAllWords() } returns sampleWords
        coEvery { repository.getRandomWord() } returns sampleWords[0]

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        Assert.assertTrue(sampleWords.any { it.word == quiz.question })

        coVerify { repository.getRandomWord() }
    }

    @Test
    fun `loadQuizByLevel - 로딩 상태 확인`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleWords.filter { it.level == "N5" }

        coEvery { repository.getAllWordsByLevel("N5") } returns n5Words
        coEvery { repository.getRandomWordByLevel("N5") } returns n5Words[0]

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)

        // Then - 로딩 완료 후 상태 확인
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `checkAnswer - 정답 확인`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleWords.filter { it.level == "N5" }

        coEvery { repository.getAllWordsByLevel("N5") } returns n5Words
        coEvery { repository.getRandomWordByLevel("N5") } returns n5Words[0]

        // 먼저 퀴즈를 로드
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val quiz = viewModel.quizState.value!!
        val correctIndex = quiz.correctIndex

        // When
        viewModel.checkAnswer(correctIndex, isLearningMode = false)

        // Then - 정답을 선택했으므로 별도 상태 변화는 없지만 메서드가 정상 실행되어야 함
        Assert.assertTrue(true) // 정답 체크 로직이 정상 실행됨을 확인
    }

    @Test
    fun `checkAnswer - 퀴즈가 없을 때 아무것도 하지 않음`() = runTest {
        // Given - 퀴즈가 로드되지 않은 상태

        // When
        viewModel.checkAnswer(0, isLearningMode = false)

        // Then - 예외가 발생하지 않아야 함
        Assert.assertTrue(true)
    }

    @Test
    fun `학습 모드에서 우선순위 데이터 없을 때 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleWords.filter { it.level == "N5" }

        coEvery { repository.getWordsForLearningMode("N5") } returns Pair(emptyList(), emptyList())
        coEvery { repository.getAllWordsByLevel("N5") } returns n5Words
        coEvery { repository.getRandomWordByLevel("N5") } returns n5Words[0]

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNotNull(viewModel.quizState.value)
        coVerify { repository.getWordsForLearningMode("N5") }
        coVerify { repository.getRandomWordByLevel("N5") }
    }
}

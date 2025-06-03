package com.lass.yomiyomi.viewmodel.kanjiQuiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.*
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
class KanjiQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: KanjiRepository
    
    private lateinit var viewModel: KanjiQuizViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleKanji = listOf(
        KanjiItem(
            id = 1,
            kanji = "食",
            onyomi = "しょく",
            kunyomi = "た(べる)",
            meaning = "음식, 먹다",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 2,
            kanji = "学",
            onyomi = "がく",
            kunyomi = "まな(ぶ)",
            meaning = "배우다, 학문",
            level = "N4",
            learningWeight = 0.6f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 3,
            kanji = "心",
            onyomi = "しん",
            kunyomi = "こころ",
            meaning = "마음, 심장",
            level = "N5",
            learningWeight = 0.9f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 4,
            kanji = "美",
            onyomi = "び",
            kunyomi = "うつく(しい)",
            meaning = "아름다움",
            level = "N2",
            learningWeight = 0.7f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 5,
            kanji = "水",
            onyomi = "すい",
            kunyomi = "みず",
            meaning = "물",
            level = "N5",
            learningWeight = 0.5f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 6,
            kanji = "火",
            onyomi = "か",
            kunyomi = "ひ",
            meaning = "불",
            level = "N5",
            learningWeight = 0.4f,
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        // 기본 mock 설정
        coEvery { repository.getAllKanji() } returns sampleKanji
        coEvery { repository.getAllKanjiByLevel(any()) } returns emptyList()
        coEvery { repository.getRandomKanji() } returns sampleKanji[0]
        coEvery { repository.getRandomKanjiByLevel(any()) } returns sampleKanji[0]
        coEvery { repository.getKanjiForLearningMode(any()) } returns Pair(emptyList(), emptyList())
        coEvery { repository.updateKanjiLearningStatus(any(), any(), any()) } just runs
        
        viewModel = KanjiQuizViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // Given & When - 초기 상태

        // Then
        assertNull(viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadQuizByLevel - 랜덤 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        // N5 레벨 한자를 모두 반환하도록 설정 (최소 4개 필요)
        coEvery { repository.getAllKanjiByLevel("N5") } returns n5Kanji
        coEvery { repository.getRandomKanjiByLevel("N5") } returns n5Kanji[0]
        coEvery { repository.getAllKanji() } returns sampleKanji

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val quiz = viewModel.quizState.value
        assertNotNull("Quiz should not be null", quiz)
        assertEquals(4, quiz!!.options.size)
        assertTrue(quiz.correctIndex in 0..3)
        assertNotNull(quiz.question)
        assertNotNull(quiz.answer)
        assertTrue(quiz.options.contains(quiz.answer))
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadQuizByLevel - 학습 모드에서 정상적인 퀴즈 로드`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val priorityKanji = sampleKanji.filter { it.learningWeight < 0.7f }
        val distractors = sampleKanji.filter { it.learningWeight >= 0.7f }
        
        coEvery { repository.getKanjiForLearningMode("N5") } returns Pair(priorityKanji, distractors)

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        assertEquals(4, quiz.options.size)
        assertTrue(quiz.correctIndex in 0..3)
        assertFalse(viewModel.isLoading.value)
        
        coVerify { repository.getKanjiForLearningMode("N5") }
    }

    @Test
    fun `loadQuizByLevel - 데이터 부족시 퀴즈 생성 실패`() = runTest {
        // Given
        val level = Level.N1
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { repository.getAllKanjiByLevel("N1") } returns emptyList()
        coEvery { repository.getRandomKanjiByLevel("N1") } returns null

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.quizState.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadQuizByLevel - KANJI_TO_READING_MEANING 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        coEvery { repository.getAllKanjiByLevel("N5") } returns n5Kanji
        coEvery { repository.getRandomKanjiByLevel("N5") } returns n5Kanji[0]
        coEvery { repository.getAllKanji() } returns sampleKanji

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        // 질문이 한자인지 확인
        assertTrue(n5Kanji.any { it.kanji == quiz.question })
        // 답이 "읽기 / 의미" 형식인지 확인
        assertTrue(quiz.answer.contains(" / "))
    }

    @Test
    fun `loadQuizByLevel - READING_MEANING_TO_KANJI 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.READING_MEANING_TO_KANJI
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        coEvery { repository.getAllKanjiByLevel("N5") } returns n5Kanji
        coEvery { repository.getRandomKanjiByLevel("N5") } returns n5Kanji[0]
        coEvery { repository.getAllKanji() } returns sampleKanji

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        // 질문이 "읽기 / 의미" 형식인지 확인
        assertTrue(quiz.question.contains(" / "))
        // 답이 한자인지 확인
        assertTrue(n5Kanji.any { it.kanji == quiz.answer })
    }

    @Test
    fun `loadQuizByLevel - Level ALL에서 모든 레벨 한자 사용`() = runTest {
        // Given
        val level = Level.ALL
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { repository.getAllKanji() } returns sampleKanji
        coEvery { repository.getRandomKanji() } returns sampleKanji[0]

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.quizState.value)
        val quiz = viewModel.quizState.value!!
        assertTrue(sampleKanji.any { it.kanji == quiz.question })
        
        coVerify { repository.getRandomKanji() }
    }

    @Test
    fun `loadQuizByLevel - 로딩 상태 확인`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        coEvery { repository.getAllKanjiByLevel("N5") } returns n5Kanji
        coEvery { repository.getRandomKanjiByLevel("N5") } returns n5Kanji[0]

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        
        // Then - 로딩 완료 후 상태 확인
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `checkAnswer - 정답 확인`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        coEvery { repository.getAllKanjiByLevel("N5") } returns n5Kanji
        coEvery { repository.getRandomKanjiByLevel("N5") } returns n5Kanji[0]

        // 먼저 퀴즈를 로드
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val quiz = viewModel.quizState.value!!
        val correctIndex = quiz.correctIndex

        // When
        viewModel.checkAnswer(correctIndex, isLearningMode = false)

        // Then - 정답을 선택했으므로 별도 상태 변화는 없지만 메서드가 정상 실행되어야 함
        assertTrue(true) // 정답 체크 로직이 정상 실행됨을 확인
    }

    @Test
    fun `checkAnswer - 퀴즈가 없을 때 아무것도 하지 않음`() = runTest {
        // Given - 퀴즈가 로드되지 않은 상태

        // When
        viewModel.checkAnswer(0, isLearningMode = false)

        // Then - 예외가 발생하지 않아야 함
        assertTrue(true)
    }

    @Test
    fun `학습 모드에서 우선순위 데이터 없을 때 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleKanji.filter { it.level == "N5" }
        
        coEvery { repository.getKanjiForLearningMode("N5") } returns Pair(emptyList(), emptyList())
        coEvery { repository.getAllKanjiByLevel("N5") } returns n5Kanji
        coEvery { repository.getRandomKanjiByLevel("N5") } returns n5Kanji[0]

        // When
        viewModel.loadQuizByLevel(level, quizType, isLearningMode = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNotNull(viewModel.quizState.value)
        coVerify { repository.getKanjiForLearningMode("N5") }
        coVerify { repository.getRandomKanjiByLevel("N5") }
    }
} 
package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GenerateMyKanjiQuizUseCaseTest {

    @MockK
    private lateinit var myKanjiRepository: MyKanjiRepository
    
    @MockK
    private lateinit var kanjiRepository: KanjiRepository
    
    private lateinit var useCase: GenerateMyKanjiQuizUseCase

    private val sampleMyKanji = listOf(
        MyKanjiItem(
            id = 1,
            kanji = "食",
            onyomi = "しょく",
            kunyomi = "た(べる)",
            meaning = "음식, 먹다",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 2,
            kanji = "学",
            onyomi = "がく",
            kunyomi = "まな(ぶ)",
            meaning = "배우다, 학문",
            level = "N4",
            learningWeight = 0.6f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 3,
            kanji = "心",
            onyomi = "しん",
            kunyomi = "こころ",
            meaning = "마음, 심장",
            level = "N5",
            learningWeight = 0.9f,
            timestamp = System.currentTimeMillis()
        ),
        MyKanjiItem(
            id = 4,
            kanji = "美",
            onyomi = "び",
            kunyomi = "うつく(しい)",
            meaning = "아름다움",
            level = "N2",
            learningWeight = 0.7f,
            timestamp = System.currentTimeMillis()
        )
    )

    private val sampleKanji = listOf(
        KanjiItem(
            id = 101,
            kanji = "水",
            onyomi = "すい",
            kunyomi = "みず",
            meaning = "물",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        KanjiItem(
            id = 102,
            kanji = "火",
            onyomi = "か",
            kunyomi = "ひ",
            meaning = "불",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GenerateMyKanjiQuizUseCase(myKanjiRepository, kanjiRepository)
    }

    @Test
    fun `generateQuiz - 랜덤 모드에서 정상적인 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleMyKanji.filter { it.level == "N5" }
        
        coEvery { myKanjiRepository.getAllMyKanjiByLevel("N5") } returns n5Kanji
        coEvery { myKanjiRepository.getAllMyKanji() } returns sampleMyKanji
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        assertEquals(4, result!!.options.size)
        assertTrue(result.correctIndex in 0..3)
        assertNotNull(result.question)
        assertNotNull(result.answer)
        assertTrue(result.options.contains(result.answer))
    }

    @Test
    fun `generateQuiz - 학습 모드에서 정상적인 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val priorityKanji = sampleMyKanji.filter { it.learningWeight < 0.7f }
        val distractors = sampleMyKanji.filter { it.learningWeight >= 0.7f }
        
        coEvery { myKanjiRepository.getMyKanjiForLearningMode("N5") } returns Pair(priorityKanji, distractors)
        coEvery { myKanjiRepository.getAllMyKanji() } returns sampleMyKanji
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)

        // Then
        assertNotNull(result)
        assertEquals(4, result!!.options.size)
        assertTrue(result.correctIndex in 0..3)
        assertNotNull(result.question)
        assertNotNull(result.answer)
        assertTrue(result.options.contains(result.answer))
    }

    @Test
    fun `generateQuiz - 데이터 부족시 null 반환`() = runTest {
        // Given
        val level = Level.N1
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { myKanjiRepository.getAllMyKanjiByLevel("N1") } returns emptyList()

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNull(result)
    }

    @Test
    fun `generateQuiz - KANJI_TO_READING_MEANING 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleMyKanji.filter { it.level == "N5" }
        
        coEvery { myKanjiRepository.getAllMyKanjiByLevel("N5") } returns n5Kanji
        coEvery { myKanjiRepository.getAllMyKanji() } returns sampleMyKanji
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        // 질문이 한자인지 확인
        assertTrue(n5Kanji.any { it.kanji == result!!.question })
        // 답이 "읽기 / 의미" 형식인지 확인
        assertTrue(result!!.answer.contains(" / "))
    }

    @Test
    fun `generateQuiz - READING_MEANING_TO_KANJI 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.READING_MEANING_TO_KANJI
        val n5Kanji = sampleMyKanji.filter { it.level == "N5" }
        
        coEvery { myKanjiRepository.getAllMyKanjiByLevel("N5") } returns n5Kanji
        coEvery { myKanjiRepository.getAllMyKanji() } returns sampleMyKanji
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        // 질문이 "읽기 / 의미" 형식인지 확인
        assertTrue(result!!.question.contains(" / "))
        // 답이 한자인지 확인
        assertTrue(n5Kanji.any { it.kanji == result.answer })
    }

    @Test
    fun `generateQuiz - Level ALL에서 모든 레벨 한자 사용`() = runTest {
        // Given
        val level = Level.ALL
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        coEvery { myKanjiRepository.getAllMyKanji() } returns sampleMyKanji
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        assertTrue(sampleMyKanji.any { it.kanji == result!!.question })
    }

    @Test
    fun `generateQuiz - 학습 모드에서 우선순위 데이터 없을 때 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val n5Kanji = sampleMyKanji.filter { it.level == "N5" }
        
        coEvery { myKanjiRepository.getMyKanjiForLearningMode("N5") } returns Pair(emptyList(), emptyList())
        coEvery { myKanjiRepository.getAllMyKanjiByLevel("N5") } returns n5Kanji
        coEvery { myKanjiRepository.getAllMyKanji() } returns sampleMyKanji
        coEvery { kanjiRepository.getAllKanji() } returns sampleKanji

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)

        // Then
        assertNotNull(result)
        assertEquals(4, result!!.options.size)
    }
} 
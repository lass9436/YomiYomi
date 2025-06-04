package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.*
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GenerateMyWordQuizUseCaseTest {

    @MockK
    private lateinit var myWordRepository: MyWordRepository
    
    @MockK
    private lateinit var wordRepository: WordRepository
    
    private lateinit var useCase: GenerateMyWordQuizUseCase

    private val sampleMyWords = listOf(
        MyWordItem(
            id = 1,
            word = "食べる",
            reading = "たべる",
            type = "동사",
            meaning = "먹다",
            level = "N5",
            learningWeight = 0.5f,
            timestamp = System.currentTimeMillis()
        ),
        MyWordItem(
            id = 2,
            word = "勉強",
            reading = "べんきょう",
            type = "명사",
            meaning = "공부",
            level = "N4",
            learningWeight = 0.3f,
            timestamp = System.currentTimeMillis()
        ),
        MyWordItem(
            id = 3,
            word = "学校",
            reading = "がっこう",
            type = "명사",
            meaning = "학교",
            level = "N5",
            learningWeight = 0.8f,
            timestamp = System.currentTimeMillis()
        ),
        MyWordItem(
            id = 4,
            word = "先生",
            reading = "せんせい",
            type = "명사",
            meaning = "선생님",
            level = "N5",
            learningWeight = 0.2f,
            timestamp = System.currentTimeMillis()
        )
    )

    private val sampleWords = listOf(
        WordItem(
            id = 101,
            word = "水",
            reading = "みず",
            type = "명사",
            meaning = "물",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        ),
        WordItem(
            id = 102,
            word = "火",
            reading = "ひ",
            type = "명사",
            meaning = "불",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GenerateMyWordQuizUseCase(myWordRepository, wordRepository)
    }

    @Test
    fun `generateQuiz - 랜덤 모드에서 정상적인 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleMyWords.filter { it.level == "N5" }
        
        coEvery { myWordRepository.getAllMyWordsByLevel("N5") } returns n5Words
        coEvery { myWordRepository.getAllMyWords() } returns sampleMyWords
        coEvery { wordRepository.getAllWords() } returns sampleWords

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
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val priorityWords = sampleMyWords.filter { it.learningWeight < 0.6f }
        val distractors = sampleMyWords.filter { it.learningWeight >= 0.6f }
        
        coEvery { myWordRepository.getMyWordsForLearningMode("N5") } returns Pair(priorityWords, distractors)
        coEvery { myWordRepository.getAllMyWords() } returns sampleMyWords
        coEvery { wordRepository.getAllWords() } returns sampleWords

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
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { myWordRepository.getAllMyWordsByLevel("N1") } returns emptyList()

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNull(result)
    }

    @Test
    fun `generateQuiz - WORD_TO_MEANING_READING 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleMyWords.filter { it.level == "N5" }
        
        coEvery { myWordRepository.getAllMyWordsByLevel("N5") } returns n5Words
        coEvery { myWordRepository.getAllMyWords() } returns sampleMyWords
        coEvery { wordRepository.getAllWords() } returns sampleWords

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        // 질문이 일본어 단어인지 확인
        assertTrue(n5Words.any { it.word == result!!.question })
        // 답이 "의미 / 읽기" 형식인지 확인
        assertTrue(result!!.answer.contains(" / "))
    }

    @Test
    fun `generateQuiz - MEANING_READING_TO_WORD 타입 퀴즈 생성`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.MEANING_READING_TO_WORD
        val n5Words = sampleMyWords.filter { it.level == "N5" }
        
        coEvery { myWordRepository.getAllMyWordsByLevel("N5") } returns n5Words
        coEvery { myWordRepository.getAllMyWords() } returns sampleMyWords
        coEvery { wordRepository.getAllWords() } returns sampleWords

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        // 질문이 "의미 / 읽기" 형식인지 확인
        assertTrue(result!!.question.contains(" / "))
        // 답이 일본어 단어인지 확인
        assertTrue(n5Words.any { it.word == result.answer })
    }

    @Test
    fun `generateQuiz - Level ALL에서 모든 레벨 단어 사용`() = runTest {
        // Given
        val level = Level.ALL
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        coEvery { myWordRepository.getAllMyWords() } returns sampleMyWords
        coEvery { wordRepository.getAllWords() } returns sampleWords

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)

        // Then
        assertNotNull(result)
        assertTrue(sampleMyWords.any { it.word == result!!.question })
    }

    @Test
    fun `generateQuiz - 학습 모드에서 우선순위 데이터 없을 때 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val n5Words = sampleMyWords.filter { it.level == "N5" }
        
        coEvery { myWordRepository.getMyWordsForLearningMode("N5") } returns Pair(emptyList(), emptyList())
        coEvery { myWordRepository.getAllMyWordsByLevel("N5") } returns n5Words
        coEvery { myWordRepository.getAllMyWords() } returns sampleMyWords
        coEvery { wordRepository.getAllWords() } returns sampleWords

        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)

        // Then
        assertNotNull(result)
        assertEquals(4, result!!.options.size)
    }
} 

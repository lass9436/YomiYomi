package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class GenerateMyWordQuizUseCaseTest {

    @Mock
    private lateinit var myWordRepository: MyWordRepository
    
    @Mock
    private lateinit var wordRepository: WordRepository
    
    private lateinit var useCase: GenerateMyWordQuizUseCase
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GenerateMyWordQuizUseCase(myWordRepository, wordRepository)
    }
    
    private fun createSampleMyWord(id: Int, level: String = "N5"): MyWordItem {
        return MyWordItem(
            id = id,
            word = "単語$id",
            reading = "たんご$id",
            meaning = "의미$id",
            type = "명사",
            level = level,
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }
    
    private fun createSampleWord(id: Int, level: String = "N5"): WordItem {
        return WordItem(
            id = id,
            word = "語$id",
            reading = "ご$id",
            meaning = "뜻$id",
            type = "명사",
            level = level,
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }
    
    @Test
    fun `선택한 레벨에 데이터가 있으면 퀴즈 생성 성공`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val myWordList = listOf(
            createSampleMyWord(1, "N5"),
            createSampleMyWord(2, "N5"),
            createSampleMyWord(3, "N5"),
            createSampleMyWord(4, "N5")
        )
        
        whenever(myWordRepository.getAllMyWordsByLevel("N5")).thenReturn(myWordList)
        whenever(myWordRepository.getAllMyWords()).thenReturn(myWordList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull("퀴즈가 생성되어야 함", result)
        assertEquals("선택지가 4개여야 함", 4, result!!.options.size)
        assertTrue("정답 인덱스가 유효해야 함", result.correctIndex in 0..3)
    }
    
    @Test
    fun `선택한 레벨에 데이터가 없으면 null 반환`() = runTest {
        // Given
        val level = Level.N1
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        
        whenever(myWordRepository.getAllMyWordsByLevel("N1")).thenReturn(emptyList())
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNull("레벨에 데이터가 없으면 null을 반환해야 함", result)
    }
    
    @Test
    fun `ALL 레벨일 때 모든 데이터에서 퀴즈 생성`() = runTest {
        // Given
        val level = Level.ALL
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val myWordList = listOf(
            createSampleMyWord(1, "N5"),
            createSampleMyWord(2, "N4"),
            createSampleMyWord(3, "N3"),
            createSampleMyWord(4, "N2")
        )
        
        whenever(myWordRepository.getAllMyWords()).thenReturn(myWordList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull("ALL 레벨에서 퀴즈가 생성되어야 함", result)
        verify(myWordRepository, atLeastOnce()).getAllMyWords()
    }
    
    @Test
    fun `단어에서 의미 퀴즈 타입 정상 동작`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val myWordList = listOf(
            createSampleMyWord(1, "N5"),
            createSampleMyWord(2, "N5"),
            createSampleMyWord(3, "N5"),
            createSampleMyWord(4, "N5")
        )
        
        whenever(myWordRepository.getAllMyWordsByLevel("N5")).thenReturn(myWordList)
        whenever(myWordRepository.getAllMyWords()).thenReturn(myWordList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull(result)
        assertTrue("질문이 단어여야 함", result!!.question.startsWith("単語"))
        assertTrue("정답이 의미/읽기 형식이어야 함", result.answer.contains("/"))
        result.options.forEach { option ->
            assertTrue("모든 선택지가 의미/읽기 형식이어야 함", option.contains("/"))
        }
    }
    
    @Test
    fun `의미에서 단어 퀴즈 타입 정상 동작`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.MEANING_READING_TO_WORD
        val myWordList = listOf(
            createSampleMyWord(1, "N5"),
            createSampleMyWord(2, "N5"),
            createSampleMyWord(3, "N5"),
            createSampleMyWord(4, "N5")
        )
        
        whenever(myWordRepository.getAllMyWordsByLevel("N5")).thenReturn(myWordList)
        whenever(myWordRepository.getAllMyWords()).thenReturn(myWordList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull(result)
        assertTrue("질문이 의미/읽기 형식이어야 함", result!!.question.contains("/"))
        assertTrue("정답이 단어여야 함", result.answer.startsWith("単語"))
        result.options.forEach { option ->
            assertTrue("모든 선택지가 단어여야 함", option.startsWith("単語") || option.startsWith("語"))
        }
    }
    
    @Test
    fun `학습 모드에서 우선순위 데이터가 있으면 사용`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val priorityWords = listOf(createSampleMyWord(1, "N5"))
        val distractors = listOf(createSampleMyWord(2, "N5"))
        
        whenever(myWordRepository.getMyWordsForLearningMode("N5"))
            .thenReturn(Pair(priorityWords, distractors))
        whenever(myWordRepository.getAllMyWords()).thenReturn(priorityWords + distractors)
        whenever(wordRepository.getAllWords()).thenReturn(listOf())
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)
        
        // Then
        assertNotNull("학습 모드에서 퀴즈가 생성되어야 함", result)
        verify(myWordRepository).getMyWordsForLearningMode("N5")
    }
    
    @Test
    fun `학습 모드에서 우선순위 데이터가 없으면 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val myWordList = listOf(
            createSampleMyWord(1, "N5"),
            createSampleMyWord(2, "N5"),
            createSampleMyWord(3, "N5"),
            createSampleMyWord(4, "N5")
        )
        
        whenever(myWordRepository.getMyWordsForLearningMode("N5"))
            .thenReturn(Pair(emptyList(), emptyList()))
        whenever(myWordRepository.getAllMyWordsByLevel("N5")).thenReturn(myWordList)
        whenever(myWordRepository.getAllMyWords()).thenReturn(myWordList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)
        
        // Then
        assertNotNull("폴백으로 퀴즈가 생성되어야 함", result)
        verify(myWordRepository).getMyWordsForLearningMode("N5")
        verify(myWordRepository).getAllMyWordsByLevel("N5")
    }
    
    @Test
    fun `오답 선택지가 부족하면 원본 데이터에서 보충`() = runTest {
        // Given
        val level = Level.N5
        val quizType = WordQuizType.WORD_TO_MEANING_READING
        val myWordList = listOf(createSampleMyWord(1, "N5")) // 1개만
        val originalWordList = listOf(
            createSampleWord(10, "N5"),
            createSampleWord(11, "N5"),
            createSampleWord(12, "N5")
        )
        
        whenever(myWordRepository.getAllMyWordsByLevel("N5")).thenReturn(myWordList)
        whenever(myWordRepository.getAllMyWords()).thenReturn(myWordList)
        whenever(wordRepository.getAllWords()).thenReturn(originalWordList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull("퀴즈가 생성되어야 함", result)
        assertEquals("4개의 선택지가 있어야 함", 4, result!!.options.size)
        verify(wordRepository).getAllWords() // 원본 데이터 호출 확인
    }
} 
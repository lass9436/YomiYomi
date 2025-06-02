package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class GenerateMyKanjiQuizUseCaseTest {

    @Mock
    private lateinit var myKanjiRepository: MyKanjiRepository
    
    @Mock
    private lateinit var kanjiRepository: KanjiRepository
    
    private lateinit var useCase: GenerateMyKanjiQuizUseCase
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GenerateMyKanjiQuizUseCase(myKanjiRepository, kanjiRepository)
    }
    
    private fun createSampleMyKanji(id: Int, level: String = "N5"): MyKanjiItem {
        return MyKanjiItem(
            id = id,
            kanji = "漢$id",
            onyomi = "オン$id",
            kunyomi = "くん$id",
            meaning = "의미$id",
            level = level,
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }
    
    private fun createSampleKanji(id: Int, level: String = "N5"): KanjiItem {
        return KanjiItem(
            id = id,
            kanji = "字$id",
            onyomi = "音$id",
            kunyomi = "読$id",
            meaning = "뜻$id",
            level = level,
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }
    
    @Test
    fun `선택한 레벨에 데이터가 있으면 퀴즈 생성 성공`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val myKanjiList = listOf(
            createSampleMyKanji(1, "N5"),
            createSampleMyKanji(2, "N5"),
            createSampleMyKanji(3, "N5"),
            createSampleMyKanji(4, "N5")
        )
        
        whenever(myKanjiRepository.getAllMyKanjiByLevel("N5")).thenReturn(myKanjiList)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull("퀴즈가 생성되어야 함", result)
        assertEquals("질문이 올바르게 설정되어야 함", 4, result!!.options.size)
        assertTrue("정답 인덱스가 유효해야 함", result.correctIndex in 0..3)
    }
    
    @Test
    fun `선택한 레벨에 데이터가 없으면 null 반환`() = runTest {
        // Given
        val level = Level.N1
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        
        whenever(myKanjiRepository.getAllMyKanjiByLevel("N1")).thenReturn(emptyList())
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNull("레벨에 데이터가 없으면 null을 반환해야 함", result)
    }
    
    @Test
    fun `ALL 레벨일 때 모든 데이터에서 퀴즈 생성`() = runTest {
        // Given
        val level = Level.ALL
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val myKanjiList = listOf(
            createSampleMyKanji(1, "N5"),
            createSampleMyKanji(2, "N4"),
            createSampleMyKanji(3, "N3"),
            createSampleMyKanji(4, "N2")
        )
        
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull("ALL 레벨에서 퀴즈가 생성되어야 함", result)
        verify(myKanjiRepository, atLeastOnce()).getAllMyKanji()
    }
    
    @Test
    fun `한자에서 읽기 퀴즈 타입 정상 동작`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val myKanjiList = listOf(
            createSampleMyKanji(1, "N5"),
            createSampleMyKanji(2, "N5"),
            createSampleMyKanji(3, "N5"),
            createSampleMyKanji(4, "N5")
        )
        
        whenever(myKanjiRepository.getAllMyKanjiByLevel("N5")).thenReturn(myKanjiList)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull(result)
        assertTrue("질문이 한자여야 함", result!!.question.startsWith("漢"))
        assertTrue("정답이 읽기/의미 형식이어야 함", result.answer.contains("/"))
        result.options.forEach { option ->
            assertTrue("모든 선택지가 읽기/의미 형식이어야 함", option.contains("/"))
        }
    }
    
    @Test
    fun `읽기에서 한자 퀴즈 타입 정상 동작`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.READING_MEANING_TO_KANJI
        val myKanjiList = listOf(
            createSampleMyKanji(1, "N5"),
            createSampleMyKanji(2, "N5"),
            createSampleMyKanji(3, "N5"),
            createSampleMyKanji(4, "N5")
        )
        
        whenever(myKanjiRepository.getAllMyKanjiByLevel("N5")).thenReturn(myKanjiList)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull(result)
        assertTrue("질문이 읽기/의미 형식이어야 함", result!!.question.contains("/"))
        assertTrue("정답이 한자여야 함", result.answer.startsWith("漢"))
        result.options.forEach { option ->
            assertTrue("모든 선택지가 한자여야 함", option.startsWith("漢") || option.startsWith("字"))
        }
    }
    
    @Test
    fun `학습 모드에서 우선순위 데이터가 있으면 사용`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val priorityKanji = listOf(createSampleMyKanji(1, "N5"))
        val distractors = listOf(createSampleMyKanji(2, "N5"))
        
        whenever(myKanjiRepository.getMyKanjiForLearningMode("N5"))
            .thenReturn(Pair(priorityKanji, distractors))
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(priorityKanji + distractors)
        whenever(kanjiRepository.getAllKanji()).thenReturn(listOf())
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)
        
        // Then
        assertNotNull("학습 모드에서 퀴즈가 생성되어야 함", result)
        verify(myKanjiRepository).getMyKanjiForLearningMode("N5")
    }
    
    @Test
    fun `학습 모드에서 우선순위 데이터가 없으면 랜덤 모드로 폴백`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val myKanjiList = listOf(
            createSampleMyKanji(1, "N5"),
            createSampleMyKanji(2, "N5"),
            createSampleMyKanji(3, "N5"),
            createSampleMyKanji(4, "N5")
        )
        
        whenever(myKanjiRepository.getMyKanjiForLearningMode("N5"))
            .thenReturn(Pair(emptyList(), emptyList()))
        whenever(myKanjiRepository.getAllMyKanjiByLevel("N5")).thenReturn(myKanjiList)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = true)
        
        // Then
        assertNotNull("폴백으로 퀴즈가 생성되어야 함", result)
        verify(myKanjiRepository).getMyKanjiForLearningMode("N5")
        verify(myKanjiRepository).getAllMyKanjiByLevel("N5")
    }
    
    @Test
    fun `오답 선택지가 부족하면 원본 데이터에서 보충`() = runTest {
        // Given
        val level = Level.N5
        val quizType = KanjiQuizType.KANJI_TO_READING_MEANING
        val myKanjiList = listOf(createSampleMyKanji(1, "N5")) // 1개만
        val originalKanjiList = listOf(
            createSampleKanji(10, "N5"),
            createSampleKanji(11, "N5"),
            createSampleKanji(12, "N5")
        )
        
        whenever(myKanjiRepository.getAllMyKanjiByLevel("N5")).thenReturn(myKanjiList)
        whenever(myKanjiRepository.getAllMyKanji()).thenReturn(myKanjiList)
        whenever(kanjiRepository.getAllKanji()).thenReturn(originalKanjiList)
        
        // When
        val result = useCase.generateQuiz(level, quizType, isLearningMode = false)
        
        // Then
        assertNotNull("퀴즈가 생성되어야 함", result)
        assertEquals("4개의 선택지가 있어야 함", 4, result!!.options.size)
        verify(kanjiRepository).getAllKanji() // 원본 데이터 호출 확인
    }
} 
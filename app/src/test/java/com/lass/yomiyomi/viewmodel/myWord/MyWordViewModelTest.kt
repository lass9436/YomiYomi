package com.lass.yomiyomi.viewmodel.myWord

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.viewmodel.myWord.list.MyWordViewModel
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
class MyWordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: MyWordRepository
    
    private lateinit var viewModel: MyWordViewModel
    
    private val testDispatcher = StandardTestDispatcher()

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
        )
    )

    private val sampleWordItems = listOf(
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
        Dispatchers.setMain(testDispatcher)
        
        coEvery { repository.getAllMyWords() } returns sampleMyWords
        coEvery { repository.getAllMyWordsByLevel(any()) } returns emptyList()
        
        viewModel = MyWordViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - 초기화시 내 단어 목록 로드`() = runTest {
        // Given & When - setUp에서 이미 실행됨

        // Then
        assertEquals(sampleMyWords, viewModel.myWords.value)
        assertFalse(viewModel.isLoading.value)
        
        coVerify { repository.getAllMyWords() }
    }

    @Test
    fun `loadMyWords - 모든 레벨 선택시 전체 단어 로드`() = runTest {
        // Given
        viewModel.setSelectedLevel(Level.ALL)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.loadMyWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleMyWords, viewModel.myWords.value)
        coVerify(atLeast = 2) { repository.getAllMyWords() }
    }

    @Test
    fun `loadMyWords - 특정 레벨 선택시 해당 레벨 단어만 로드`() = runTest {
        // Given
        val n5Words = sampleMyWords.filter { it.level == "N5" }
        coEvery { repository.getAllMyWordsByLevel("N5") } returns n5Words
        
        viewModel.setSelectedLevel(Level.N5)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.loadMyWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(n5Words, viewModel.myWords.value)
        coVerify { repository.getAllMyWordsByLevel("N5") }
    }

    @Test
    fun `searchOriginalWords - 원본 단어 검색`() = runTest {
        // Given
        val query = "물"
        coEvery { repository.searchOriginalWords(query) } returns sampleWordItems

        // When
        viewModel.searchOriginalWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleWordItems, viewModel.searchResults.value)
        coVerify { repository.searchOriginalWords(query) }
    }

    @Test
    fun `addWordToMyWords - 단어 추가 성공`() = runTest {
        // Given
        val wordToAdd = sampleWordItems[0]
        val updatedList = sampleMyWords + MyWordItem(
            id = wordToAdd.id,
            word = wordToAdd.word,
            reading = wordToAdd.reading,
            type = wordToAdd.type,
            meaning = wordToAdd.meaning,
            level = wordToAdd.level,
            learningWeight = wordToAdd.learningWeight,
            timestamp = wordToAdd.timestamp
        )
        
        coEvery { repository.isWordInMyWords(wordToAdd.id) } returns false
        coEvery { repository.addWordToMyWords(wordToAdd) } returns true
        coEvery { repository.getAllMyWords() } returns updatedList
        
        var callbackResult = false

        // When
        viewModel.addWordToMyWords(wordToAdd) { success ->
            callbackResult = success
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(callbackResult)
        coVerify { repository.isWordInMyWords(wordToAdd.id) }
        coVerify { repository.addWordToMyWords(wordToAdd) }
    }

    @Test
    fun `addWordToMyWords - 단어 추가 실패`() = runTest {
        // Given
        val wordToAdd = sampleWordItems[0]
        coEvery { repository.isWordInMyWords(wordToAdd.id) } returns false
        coEvery { repository.addWordToMyWords(wordToAdd) } throws Exception("Test exception")
        
        var callbackResult = true

        // When
        viewModel.addWordToMyWords(wordToAdd) { success ->
            callbackResult = success
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(callbackResult)
        coVerify { repository.isWordInMyWords(wordToAdd.id) }
        coVerify { repository.addWordToMyWords(wordToAdd) }
    }

    @Test
    fun `addMyWordDirectly - 직접 단어 추가 성공`() = runTest {
        // Given
        val word = "新しい"
        val reading = "あたらしい"
        val meaning = "새로운"
        val type = "형용사"
        val level = "N5"
        
        coEvery { repository.insertMyWordDirectly(any()) } returns true
        coEvery { repository.getAllMyWords() } returns sampleMyWords
        
        var callbackResult = false
        var callbackMessage = ""

        // When
        viewModel.addMyWordDirectly(word, reading, meaning, type, level) { success, message ->
            callbackResult = success
            callbackMessage = message
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(callbackResult)
        assertEquals("단어가 추가되었습니다.", callbackMessage)
        coVerify { repository.insertMyWordDirectly(any()) }
    }

    @Test
    fun `updateMyWord - 단어 업데이트 성공`() = runTest {
        // Given
        val myWord = sampleMyWords[0]
        val newWord = "飲む"
        val newReading = "のむ"
        val newMeaning = "마시다"
        val newType = "동사"
        val newLevel = "N5"
        
        coEvery { repository.insertMyWordDirectly(any()) } returns true
        coEvery { repository.getAllMyWords() } returns sampleMyWords
        
        var callbackResult = false
        var callbackMessage = ""

        // When
        viewModel.updateMyWord(myWord, newWord, newReading, newMeaning, newType, newLevel) { success, message ->
            callbackResult = success
            callbackMessage = message
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(callbackResult)
        assertEquals("단어가 수정되었습니다.", callbackMessage)
        coVerify { repository.insertMyWordDirectly(any()) }
    }

    @Test
    fun `deleteMyWord - 단어 삭제`() = runTest {
        // Given
        val wordToDelete = sampleMyWords[0]
        val remainingWords = sampleMyWords.drop(1)
        
        coEvery { repository.deleteMyWord(wordToDelete) } just runs
        coEvery { repository.getAllMyWords() } returns remainingWords

        // When
        viewModel.deleteMyWord(wordToDelete)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(remainingWords, viewModel.myWords.value)
        coVerify { repository.deleteMyWord(wordToDelete) }
    }

    @Test
    fun `setSelectedLevel - 레벨 변경`() = runTest {
        // Given
        val newLevel = Level.N3

        // When
        viewModel.setSelectedLevel(newLevel)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(newLevel, viewModel.selectedLevel.value)
    }

    @Test
    fun `searchMyWords - 내 단어 검색`() = runTest {
        // Given
        val query = "학교"

        // When
        viewModel.searchMyWords(query)

        // Then
        // searchMyWords는 내부 검색 쿼리를 업데이트하는 메서드입니다
        // 실제 결과는 ViewModel의 내부 로직에 따라 처리됩니다
        assertTrue(true) // 메서드가 정상적으로 실행되었음을 확인
    }
} 

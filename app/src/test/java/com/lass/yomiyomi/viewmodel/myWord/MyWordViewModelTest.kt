package com.lass.yomiyomi.viewmodel.myWord

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
class MyWordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: MyWordRepository

    private lateinit var viewModel: MyWordViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyWordViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
        assertEquals("초기 레벨은 ALL", Level.ALL, viewModel.selectedLevel.value)
        assertFalse("초기 로딩 상태는 false", viewModel.isLoading.value)
    }

    @Test
    fun `레벨 선택 정상 동작`() = runTest {
        // Given
        whenever(repository.getAllMyWordsByLevel("N5")).thenReturn(listOf())

        // When
        viewModel.setSelectedLevel(Level.N5)

        // Then
        assertEquals("선택된 레벨이 업데이트되어야 함", Level.N5, viewModel.selectedLevel.value)
    }

    @Test
    fun `단어 삭제 정상 동작`() = runTest {
        // Given
        val myWord = createSampleMyWord()
        whenever(repository.getAllMyWords()).thenReturn(listOf())

        // When
        viewModel.deleteMyWord(myWord)

        // Then
        verify(repository).deleteMyWord(myWord)
    }
} 
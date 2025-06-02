package com.lass.yomiyomi.viewmodel.kanjiRandom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiItem
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
class KanjiRandomRandomViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: KanjiRepository

    private lateinit var viewModel: KanjiRandomRandomViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = KanjiRandomRandomViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSampleKanji(): KanjiItem {
        return KanjiItem(
            id = 1,
            kanji = "水",
            onyomi = "スイ",
            kunyomi = "みず",
            meaning = "물",
            level = "N5",
            learningWeight = 1.0f,
            timestamp = System.currentTimeMillis()
        )
    }

    @Test
    fun `초기 상태 확인`() {
        // Then
        assertNull("초기 랜덤 한자는 null", viewModel.randomKanji.value)
    }

    @Test
    fun `랜덤 한자 가져오기 정상 동작`() = runTest {
        // Given
        val sampleKanji = createSampleKanji()
        whenever(repository.getRandomKanji()).thenReturn(sampleKanji)

        // When
        viewModel.fetchRandomKanji()

        // Then
        verify(repository).getRandomKanji()
        assertEquals("랜덤 한자가 설정되어야 함", sampleKanji, viewModel.randomKanji.value)
    }

    @Test
    fun `레벨별 랜덤 한자 가져오기 정상 동작`() = runTest {
        // Given
        val sampleKanji = createSampleKanji()
        whenever(repository.getRandomKanjiByLevel("N5")).thenReturn(sampleKanji)

        // When
        viewModel.fetchRandomKanjiByLevel("N5")

        // Then
        verify(repository).getRandomKanjiByLevel("N5")
        assertEquals("레벨별 랜덤 한자가 설정되어야 함", sampleKanji, viewModel.randomKanji.value)
    }

    @Test
    fun `null 레벨로 호출시 정상 동작`() = runTest {
        // Given
        val sampleKanji = createSampleKanji()
        whenever(repository.getRandomKanjiByLevel(null)).thenReturn(sampleKanji)

        // When
        viewModel.fetchRandomKanjiByLevel(null)

        // Then
        verify(repository).getRandomKanjiByLevel(null)
        assertEquals("null 레벨 처리 후 한자가 설정되어야 함", sampleKanji, viewModel.randomKanji.value)
    }
} 
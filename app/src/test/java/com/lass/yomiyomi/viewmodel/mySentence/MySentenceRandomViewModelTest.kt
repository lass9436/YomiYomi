package com.lass.yomiyomi.viewmodel.mySentence

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.viewmodel.mySentence.random.MySentenceRandomViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MySentenceRandomViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mySentenceRepository: MySentenceRepository
    
    private lateinit var viewModel: MySentenceRandomViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    private val sampleSentenceN5 = SentenceItem(
        id = 1,
        japanese = "今日{きょう}は良{よ}い天気{てんき}ですね。",
        korean = "오늘은 좋은 날씨네요.",
        category = "일상회화",
        level = Level.N5,
        learningProgress = 0.7f,
        paragraphId = null,
        orderInParagraph = 0,
        reviewCount = 3,
        lastReviewedAt = null,
        createdAt = System.currentTimeMillis()
    )

    private val sampleSentenceN4 = SentenceItem(
        id = 2,
        japanese = "私{わたし}は昨日{きのう}友達{ともだち}と映画{えいが}を見{み}ました。",
        korean = "저는 어제 친구와 영화를 봤습니다.",
        category = "일상회화",
        level = Level.N4,
        learningProgress = 0.5f,
        paragraphId = null,
        orderInParagraph = 0,
        reviewCount = 1,
        lastReviewedAt = null,
        createdAt = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        // 기본적으로 전체 레벨 조회 시 N5 문장 반환하도록 설정
        coEvery { mySentenceRepository.getRandomSentenceByLevel(null) } returns sampleSentenceN5
        
        viewModel = MySentenceRandomViewModel(mySentenceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // When - setUp에서 이미 생성자 호출됨
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleSentenceN5, viewModel.randomSentence.value)
        assertFalse(viewModel.isLoading.value)
        coVerify { mySentenceRepository.getRandomSentenceByLevel(null) }
    }

    @Test
    fun `fetchRandomSentenceByLevel - 전체 레벨에서 랜덤 문장 가져오기`() = runTest {
        // Given
        coEvery { mySentenceRepository.getRandomSentenceByLevel(null) } returns sampleSentenceN4

        // When
        viewModel.fetchRandomSentenceByLevel(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleSentenceN4, viewModel.randomSentence.value)
        assertFalse(viewModel.isLoading.value)
        coVerify { mySentenceRepository.getRandomSentenceByLevel(null) }
    }

    @Test
    fun `fetchRandomSentenceByLevel - N5 레벨에서 랜덤 문장 가져오기`() = runTest {
        // Given
        val level = "N5"
        coEvery { mySentenceRepository.getRandomSentenceByLevel(level) } returns sampleSentenceN5

        // When
        viewModel.fetchRandomSentenceByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleSentenceN5, viewModel.randomSentence.value)
        assertFalse(viewModel.isLoading.value)
        coVerify { mySentenceRepository.getRandomSentenceByLevel(level) }
    }

    @Test
    fun `fetchRandomSentenceByLevel - N4 레벨에서 랜덤 문장 가져오기`() = runTest {
        // Given
        val level = "N4"
        coEvery { mySentenceRepository.getRandomSentenceByLevel(level) } returns sampleSentenceN4

        // When
        viewModel.fetchRandomSentenceByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleSentenceN4, viewModel.randomSentence.value)
        assertFalse(viewModel.isLoading.value)
        coVerify { mySentenceRepository.getRandomSentenceByLevel(level) }
    }

    @Test
    fun `fetchRandomSentenceByLevel - 해당 레벨에 문장이 없는 경우`() = runTest {
        // Given
        val level = "N1"
        coEvery { mySentenceRepository.getRandomSentenceByLevel(level) } returns null

        // When
        viewModel.fetchRandomSentenceByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.randomSentence.value)
        assertFalse(viewModel.isLoading.value)
        coVerify { mySentenceRepository.getRandomSentenceByLevel(level) }
    }

    @Test
    fun `fetchRandomSentenceByLevel - Repository에서 에러 발생 시 처리`() = runTest {
        // Given
        val level = "N5"
        coEvery { mySentenceRepository.getRandomSentenceByLevel(level) } throws RuntimeException("Database Error")

        // When
        viewModel.fetchRandomSentenceByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.randomSentence.value)
        assertFalse(viewModel.isLoading.value)
        coVerify { mySentenceRepository.getRandomSentenceByLevel(level) }
    }

    @Test
    fun `로딩 상태 변화 확인`() = runTest {
        // Given
        val level = "N5"
        val loadingStates = mutableListOf<Boolean>()
        
        // Repository 호출이 느리게 되도록 설정
        coEvery { mySentenceRepository.getRandomSentenceByLevel(level) } coAnswers {
            delay(100)
            sampleSentenceN5
        }

        // When
        val job = launch {
            viewModel.isLoading.collect { loadingStates.add(it) }
        }
        
        viewModel.fetchRandomSentenceByLevel(level)
        testDispatcher.scheduler.advanceTimeBy(50) // 중간 시점
        
        assertTrue("로딩 중이어야 함", viewModel.isLoading.value)
        
        testDispatcher.scheduler.advanceUntilIdle() // 완료 대기
        
        assertFalse("로딩 완료되어야 함", viewModel.isLoading.value)
        assertEquals(sampleSentenceN5, viewModel.randomSentence.value)
        
        job.cancel()
    }

    @Test
    fun `여러 번 연속 호출 시 마지막 결과만 반영`() = runTest {
        // Given
        coEvery { mySentenceRepository.getRandomSentenceByLevel("N5") } returns sampleSentenceN5
        coEvery { mySentenceRepository.getRandomSentenceByLevel("N4") } returns sampleSentenceN4

        // When
        viewModel.fetchRandomSentenceByLevel("N5")
        viewModel.fetchRandomSentenceByLevel("N4") // 바로 다시 호출
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(sampleSentenceN4, viewModel.randomSentence.value) // 마지막 호출 결과
        assertFalse(viewModel.isLoading.value)
    }
} 
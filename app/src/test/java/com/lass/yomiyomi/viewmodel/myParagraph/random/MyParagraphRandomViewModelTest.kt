package com.lass.yomiyomi.viewmodel.myParagraph.random

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
class MyParagraphRandomViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var myParagraphRepository: MyParagraphRepository

    @MockK
    private lateinit var mySentenceRepository: MySentenceRepository

    private lateinit var viewModel: MyParagraphRandomViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val sampleParagraphN5 = ParagraphItem(
        paragraphId = "1",
        title = "일본 여행 준비하기",
        description = "일본 여행을 위한 기본 회화와 유용한 표현들을 배워봅시다.",
        category = "여행",
        level = Level.N5,
        totalSentences = 3,
        actualSentenceCount = 3,
        createdAt = System.currentTimeMillis()
    )

    private val sampleParagraphN4 = ParagraphItem(
        paragraphId = "2",
        title = "일본 요리 체험",
        description = "일본 요리를 만들어보면서 다양한 표현을 익혀봅시다.",
        category = "요리",
        level = Level.N4,
        totalSentences = 5,
        actualSentenceCount = 5,
        createdAt = System.currentTimeMillis()
    )

    private val sampleSentencesN5 = listOf(
        SentenceItem(
            id = 1,
            japanese = "空港{くうこう}はどこですか？",
            korean = "공항은 어디입니까?",
            category = "여행",
            level = Level.N5,
            learningProgress = 0.8f,
            paragraphId = "1",
            orderInParagraph = 1,
            reviewCount = 2,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        ),
        SentenceItem(
            id = 2,
            japanese = "切符{きっぷ}を買{か}いたいです。",
            korean = "표를 사고 싶습니다.",
            category = "여행",
            level = Level.N5,
            learningProgress = 0.6f,
            paragraphId = "1",
            orderInParagraph = 2,
            reviewCount = 1,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        ),
        SentenceItem(
            id = 3,
            japanese = "ありがとうございます。",
            korean = "감사합니다.",
            category = "여행",
            level = Level.N5,
            learningProgress = 0.9f,
            paragraphId = "1",
            orderInParagraph = 3,
            reviewCount = 5,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        )
    )

    private val sampleSentencesN4 = listOf(
        SentenceItem(
            id = 4,
            japanese = "今日{きょう}は日本{にほん}料理{りょうり}を作{つく}ります。",
            korean = "오늘은 일본 요리를 만듭니다.",
            category = "요리",
            level = Level.N4,
            learningProgress = 0.4f,
            paragraphId = "2",
            orderInParagraph = 1,
            reviewCount = 0,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        ),
        SentenceItem(
            id = 5,
            japanese = "材料{ざいりょう}を準備{じゅんび}しましょう。",
            korean = "재료를 준비합시다.",
            category = "요리",
            level = Level.N4,
            learningProgress = 0.3f,
            paragraphId = "2",
            orderInParagraph = 2,
            reviewCount = 1,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // 기본적으로 전체 레벨 조회 시 N5 문단과 문장들 반환하도록 설정
        coEvery { myParagraphRepository.getRandomParagraphByLevel(null) } returns sampleParagraphN5
        coEvery { mySentenceRepository.getSentencesByParagraph("1") } returns sampleSentencesN5

        viewModel = MyParagraphRandomViewModel(myParagraphRepository, mySentenceRepository)
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
        Assert.assertEquals(sampleParagraphN5, viewModel.randomParagraph.value)
        Assert.assertEquals(sampleSentencesN5, viewModel.sentences.value)
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(null) }
        coVerify { mySentenceRepository.getSentencesByParagraph("1") }
    }

    @Test
    fun `fetchRandomParagraphByLevel - 전체 레벨에서 랜덤 문단 가져오기`() = runTest {
        // Given
        coEvery { myParagraphRepository.getRandomParagraphByLevel(null) } returns sampleParagraphN4
        coEvery { mySentenceRepository.getSentencesByParagraph("2") } returns sampleSentencesN4

        // When
        viewModel.fetchRandomParagraphByLevel(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(sampleParagraphN4, viewModel.randomParagraph.value)
        Assert.assertEquals(sampleSentencesN4, viewModel.sentences.value)
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(null) }
        coVerify { mySentenceRepository.getSentencesByParagraph("2") }
    }

    @Test
    fun `fetchRandomParagraphByLevel - N5 레벨에서 랜덤 문단 가져오기`() = runTest {
        // Given
        val level = "N5"
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } returns sampleParagraphN5
        coEvery { mySentenceRepository.getSentencesByParagraph("1") } returns sampleSentencesN5

        // When
        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(sampleParagraphN5, viewModel.randomParagraph.value)
        Assert.assertEquals(sampleSentencesN5, viewModel.sentences.value)
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(level) }
        coVerify { mySentenceRepository.getSentencesByParagraph("1") }
    }

    @Test
    fun `fetchRandomParagraphByLevel - N4 레벨에서 랜덤 문단 가져오기`() = runTest {
        // Given
        val level = "N4"
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } returns sampleParagraphN4
        coEvery { mySentenceRepository.getSentencesByParagraph("2") } returns sampleSentencesN4

        // When
        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(sampleParagraphN4, viewModel.randomParagraph.value)
        Assert.assertEquals(sampleSentencesN4, viewModel.sentences.value)
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(level) }
        coVerify { mySentenceRepository.getSentencesByParagraph("2") }
    }

    @Test
    fun `fetchRandomParagraphByLevel - 해당 레벨에 문단이 없는 경우`() = runTest {
        // Given
        val level = "N1"
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } returns null

        // When
        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNull(viewModel.randomParagraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(level) }
        coVerify(exactly = 1) { mySentenceRepository.getSentencesByParagraph("1") }
    }

    @Test
    fun `fetchRandomParagraphByLevel - 문단은 있지만 문장이 없는 경우`() = runTest {
        // Given
        val level = "N3"
        val emptyParagraph = sampleParagraphN5.copy(paragraphId = "empty", level = Level.N3)
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } returns emptyParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph("empty") } returns emptyList()

        // When
        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(emptyParagraph, viewModel.randomParagraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(level) }
        coVerify { mySentenceRepository.getSentencesByParagraph("empty") }
    }

    @Test
    fun `fetchRandomParagraphByLevel - Repository에서 에러 발생 시 처리`() = runTest {
        // Given
        val level = "N5"
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } throws RuntimeException("Database Error")

        // When
        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNull(viewModel.randomParagraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(level) }
    }

    @Test
    fun `fetchRandomParagraphByLevel - 문장 Repository에서 에러 발생 시 처리`() = runTest {
        // Given
        val level = "N5"
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } returns sampleParagraphN5
        coEvery { mySentenceRepository.getSentencesByParagraph("1") } throws RuntimeException("Database Error")

        // When
        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNull(viewModel.randomParagraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getRandomParagraphByLevel(level) }
        coVerify { mySentenceRepository.getSentencesByParagraph("1") }
    }

    @Test
    fun `로딩 상태 변화 확인`() = runTest {
        // Given
        val level = "N5"

        // Repository 호출이 느리게 되도록 설정
        coEvery { myParagraphRepository.getRandomParagraphByLevel(level) } coAnswers {
            delay(100)
            sampleParagraphN5
        }
        coEvery { mySentenceRepository.getSentencesByParagraph("1") } coAnswers {
            delay(50)
            sampleSentencesN5
        }

        // When
        val job = launch {
            viewModel.isLoading.collect { }
        }

        viewModel.fetchRandomParagraphByLevel(level)
        testDispatcher.scheduler.advanceTimeBy(50) // 중간 시점

        Assert.assertTrue("로딩 중이어야 함", viewModel.isLoading.value)

        testDispatcher.scheduler.advanceUntilIdle() // 완료 대기

        Assert.assertFalse("로딩 완료되어야 함", viewModel.isLoading.value)
        Assert.assertEquals(sampleParagraphN5, viewModel.randomParagraph.value)
        Assert.assertEquals(sampleSentencesN5, viewModel.sentences.value)

        job.cancel()
    }

    @Test
    fun `여러 번 연속 호출 시 마지막 결과만 반영`() = runTest {
        // Given
        coEvery { myParagraphRepository.getRandomParagraphByLevel("N5") } returns sampleParagraphN5
        coEvery { mySentenceRepository.getSentencesByParagraph("1") } returns sampleSentencesN5
        coEvery { myParagraphRepository.getRandomParagraphByLevel("N4") } returns sampleParagraphN4
        coEvery { mySentenceRepository.getSentencesByParagraph("2") } returns sampleSentencesN4

        // When
        viewModel.fetchRandomParagraphByLevel("N5")
        viewModel.fetchRandomParagraphByLevel("N4") // 바로 다시 호출
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(sampleParagraphN4, viewModel.randomParagraph.value) // 마지막 호출 결과
        Assert.assertEquals(sampleSentencesN4, viewModel.sentences.value) // 마지막 호출 결과
        Assert.assertFalse(viewModel.isLoading.value)
    }
}

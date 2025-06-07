package com.lass.yomiyomi.viewmodel.myParagraph.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
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
class MyParagraphDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var myParagraphRepository: MyParagraphRepository

    @MockK
    private lateinit var mySentenceRepository: MySentenceRepository

    private lateinit var viewModel: MyParagraphDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val sampleParagraph = ParagraphItem(
        paragraphId = 1,
        title = "일본 여행 준비하기",
        description = "일본 여행을 위한 기본 회화와 유용한 표현들을 배워봅시다.",
        category = "여행",
        level = Level.N5,
        totalSentences = 3,
        actualSentenceCount = 3,
        createdAt = System.currentTimeMillis()
    )

    private val sampleSentences = listOf(
        SentenceItem(
            id = 1,
            japanese = "空港{くうこう}はどこですか？",
            korean = "공항은 어디입니까?",
            category = "여행",
            level = Level.N5,
            learningProgress = 0.8f,
            paragraphId = 1,
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
            paragraphId = 1,
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
            paragraphId = 1,
            orderInParagraph = 3,
            reviewCount = 5,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        )
    )

    private val sampleCategories = listOf("여행", "요리", "일상회화", "비즈니스")
    private val sampleLevels = listOf("N5", "N4", "N3", "N2", "N1")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        viewModel = MyParagraphDetailViewModel(myParagraphRepository, mySentenceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태 확인`() = runTest {
        // Given & When - 초기 상태

        // Then
        Assert.assertNull(viewModel.paragraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertTrue(viewModel.availableCategories.value.isEmpty())
        Assert.assertEquals(Level.values().toList(), viewModel.availableLevels.value)
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadParagraphDetail - 문단 상세 정보 로드 성공`() = runTest {
        // Given
        val paragraphId = 1
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels

        // When
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(sampleParagraph, viewModel.paragraph.value)
        Assert.assertEquals(sampleSentences.sortedBy { it.orderInParagraph }, viewModel.sentences.value)
        Assert.assertEquals(sampleCategories, viewModel.availableCategories.value)
        Assert.assertFalse(viewModel.isLoading.value)

        coVerify { myParagraphRepository.getParagraphById(paragraphId) }
        coVerify { mySentenceRepository.getSentencesByParagraph(paragraphId) }
        coVerify { mySentenceRepository.getDistinctCategories() }
        coVerify { mySentenceRepository.getDistinctLevels() }
    }

    @Test
    fun `loadParagraphDetail - 문단이 존재하지 않는 경우`() = runTest {
        // Given
        val paragraphId = 999
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns null
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns emptyList()
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels

        // When
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNull(viewModel.paragraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertEquals(sampleCategories, viewModel.availableCategories.value)
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `insertSentence - 새 문장 추가 성공`() = runTest {
        // Given
        val paragraphId = 1
        val newSentence = SentenceItem(
            id = 0, // insert 시 DB에서 자동 생성
            japanese = "新{あら}しい文章{ぶんしょう}です。",
            korean = "새로운 문장입니다.",
            category = "여행",
            level = Level.N5,
            learningProgress = 0.0f,
            paragraphId = paragraphId,
            orderInParagraph = 0, // ViewModel에서 자동 계산
            reviewCount = 0,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        )

        // 기존 문장들 설정 (orderInParagraph 최대값이 3)
        viewModel.loadParagraphDetail(paragraphId)
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels
        testDispatcher.scheduler.advanceUntilIdle()

        // insertSentence 호출 후의 문장 목록 (새 문장 추가됨)
        val updatedSentences = sampleSentences + newSentence.copy(orderInParagraph = 4)
        coEvery { mySentenceRepository.insertSentence(any()) } returns 1L
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns updatedSentences

        // When
        viewModel.insertSentence(newSentence)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(updatedSentences.sortedBy { it.orderInParagraph }, viewModel.sentences.value)
        coVerify { mySentenceRepository.insertSentence(match { it.orderInParagraph == 4 }) }
        coVerify { mySentenceRepository.getSentencesByParagraph(paragraphId) }
    }

    @Test
    fun `insertSentence - 첫 번째 문장 추가 (기존 문장이 없는 경우)`() = runTest {
        // Given
        val paragraphId = 1
        val firstSentence = SentenceItem(
            id = 0,
            japanese = "最初{さいしょ}の文章{ぶんしょう}です。",
            korean = "첫 번째 문장입니다.",
            category = "테스트",
            level = Level.N5,
            learningProgress = 0.0f,
            paragraphId = paragraphId,
            orderInParagraph = 0,
            reviewCount = 0,
            lastReviewedAt = null,
            createdAt = System.currentTimeMillis()
        )

        // 빈 문단 설정
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph.copy(paragraphId = paragraphId)
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns emptyList()
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels

        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        // 첫 문장 추가 후 결과
        val resultSentences = listOf(firstSentence.copy(orderInParagraph = 1))
        coEvery { mySentenceRepository.insertSentence(any()) } returns 1L
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns resultSentences

        // When
        viewModel.insertSentence(firstSentence)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(resultSentences, viewModel.sentences.value)
        coVerify { mySentenceRepository.insertSentence(match { it.orderInParagraph == 1 }) }
    }

    @Test
    fun `updateSentence - 문장 수정 성공`() = runTest {
        // Given
        val paragraphId = 1

        // 기존 데이터 로드
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        // 수정할 문장
        val updatedSentence = sampleSentences[0].copy(
            korean = "공항이 어디에 있나요?", // 한국어 번역 수정
            learningProgress = 0.9f
        )

        // 수정 후 결과
        val updatedSentences = sampleSentences.map {
            if (it.id == updatedSentence.id) updatedSentence else it
        }
        coEvery { mySentenceRepository.updateSentence(updatedSentence) } just Runs
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns updatedSentences

        // When
        viewModel.updateSentence(updatedSentence)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(updatedSentences.sortedBy { it.orderInParagraph }, viewModel.sentences.value)
        coVerify { mySentenceRepository.updateSentence(updatedSentence) }
        coVerify { mySentenceRepository.getSentencesByParagraph(paragraphId) }
    }

    @Test
    fun `deleteSentence - 문장 삭제 성공`() = runTest {
        // Given
        val paragraphId = 1

        // 기존 데이터 로드
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        // 삭제할 문장 ID
        val sentenceIdToDelete = sampleSentences[1].id // 두 번째 문장 삭제

        // 삭제 후 결과 (두 번째 문장 제거됨)
        val remainingSentences = sampleSentences.filter { it.id != sentenceIdToDelete }
        coEvery { mySentenceRepository.deleteSentenceById(sentenceIdToDelete) } just Runs
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns remainingSentences

        // When
        viewModel.deleteSentence(sentenceIdToDelete)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(remainingSentences.sortedBy { it.orderInParagraph }, viewModel.sentences.value)
        coVerify { mySentenceRepository.deleteSentenceById(sentenceIdToDelete) }
        coVerify { mySentenceRepository.getSentencesByParagraph(paragraphId) }
    }

    @Test
    fun `deleteSentence - 존재하지 않는 문장 삭제 시도`() = runTest {
        // Given
        val paragraphId = 1

        // 기존 데이터 로드
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        val nonExistentSentenceId = 999
        coEvery { mySentenceRepository.deleteSentenceById(nonExistentSentenceId) } just Runs
        // 삭제 후에도 같은 목록 반환 (변화 없음)
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences

        // When
        viewModel.deleteSentence(nonExistentSentenceId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(sampleSentences.sortedBy { it.orderInParagraph }, viewModel.sentences.value)
        coVerify { mySentenceRepository.deleteSentenceById(nonExistentSentenceId) }
    }

    @Test
    fun `Repository 에러 처리 - loadParagraphDetail`() = runTest {
        // Given
        val paragraphId = 999
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } throws RuntimeException("Database Error")

        // When
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertNull(viewModel.paragraph.value)
        Assert.assertTrue(viewModel.sentences.value.isEmpty())
        Assert.assertFalse(viewModel.isLoading.value)
        coVerify { myParagraphRepository.getParagraphById(paragraphId) }
    }

    @Test
    fun `Repository 에러 처리 - insertSentence`() = runTest {
        // Given
        val paragraphId = 1
        val newSentence = sampleSentences[0].copy(id = 0, paragraphId = paragraphId)

        // 기본 상태 설정
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } returns sampleParagraph
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } returns sampleSentences
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels
        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceUntilIdle()

        val originalSentences = viewModel.sentences.value
        coEvery { mySentenceRepository.insertSentence(any()) } throws RuntimeException("Insert Error")

        // When
        viewModel.insertSentence(newSentence)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - 에러 발생 시 기존 상태 유지
        Assert.assertEquals(originalSentences, viewModel.sentences.value)
        coVerify { mySentenceRepository.insertSentence(any()) }
    }

    @Test
    fun `로딩 상태 변화 확인`() = runTest {
        // Given
        val paragraphId = 1

        // Repository 호출이 느리게 되도록 설정
        coEvery { myParagraphRepository.getParagraphById(paragraphId) } coAnswers {
            delay(100)
            sampleParagraph
        }
        coEvery { mySentenceRepository.getSentencesByParagraph(paragraphId) } coAnswers {
            delay(50)
            sampleSentences
        }
        coEvery { mySentenceRepository.getDistinctCategories() } returns sampleCategories
        coEvery { mySentenceRepository.getDistinctLevels() } returns sampleLevels

        // When
        val job = launch {
            viewModel.isLoading.collect { }
        }

        viewModel.loadParagraphDetail(paragraphId)
        testDispatcher.scheduler.advanceTimeBy(50) // 중간 시점

        Assert.assertTrue("로딩 중이어야 함", viewModel.isLoading.value)

        testDispatcher.scheduler.advanceUntilIdle() // 완료 대기

        Assert.assertFalse("로딩 완료되어야 함", viewModel.isLoading.value)
        Assert.assertEquals(sampleParagraph, viewModel.paragraph.value)
        Assert.assertEquals(sampleSentences.sortedBy { it.orderInParagraph }, viewModel.sentences.value)

        job.cancel()
    }
}

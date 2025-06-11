package com.lass.yomiyomi.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.util.JapaneseTextFilter

/**
 * 백그라운드 TTS 전용 매니저
 * Foreground Service와 함께 사용하여 백그라운드에서도 TTS 유지
 */
@Singleton
class BackgroundTTSManager @Inject constructor(
    private val context: Context,
    private val speechManager: SpeechManager // 충돌 방지를 위해 주입
) {
    private var textToSpeech: TextToSpeech? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // TTS 큐 및 상태
    private var currentQueue = mutableListOf<TTSItem>()
    private var currentIndex = 0
    
    // 상태 Flow들
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentText = MutableStateFlow("")
    val currentText: StateFlow<String> = _currentText.asStateFlow()
    
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    
    private val _progress = MutableStateFlow(TTSProgress())
    val progress: StateFlow<TTSProgress> = _progress.asStateFlow()
    
    // 설정
    private val _settings = MutableStateFlow(BackgroundTTSSettings())
    val settings: StateFlow<BackgroundTTSSettings> = _settings.asStateFlow()

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.JAPANESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 오류 처리
                } else {
                    textToSpeech?.setSpeechRate(0.8f)
                    textToSpeech?.setPitch(1.0f)
                    
                    textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isPlaying.value = true
                        }
                        
                        override fun onDone(utteranceId: String?) {
                            _isPlaying.value = false
                            // 다음 아이템 재생
                            coroutineScope.launch {
                                delay(1000) // 1초 간격
                                playNextItem()
                            }
                        }
                        
                        override fun onError(utteranceId: String?) {
                            _isPlaying.value = false
                            // 다음 아이템으로 넘어가기
                            coroutineScope.launch {
                                delay(500)
                                playNextItem()
                            }
                        }
                    })
                    
                    _isReady.value = true
                }
            }
        }
    }

    /**
     * 문장 리스트로 백그라운드 학습 시작 (랜덤)
     */
    fun startSentenceLearning(sentences: List<SentenceItem>) {
        if (!_isReady.value || sentences.isEmpty()) return
        
        // 기존 SpeechManager TTS 정지하여 충돌 방지
        speechManager.stopSpeaking()
        
        currentQueue.clear()
        currentIndex = 0
        
        // 랜덤 순서로 섞기
        val shuffledSentences = sentences.shuffled()
        
        // TTSItem으로 변환
        shuffledSentences.forEach { sentence ->
            val settings = _settings.value
            
            if (settings.includeJapanese) {
                currentQueue.add(
                    TTSItem(
                        text = sentence.japanese,
                        processedText = JapaneseTextFilter.prepareTTSText(sentence.japanese),
                        isJapanese = true,
                        source = sentence
                    )
                )
            }
            
            if (settings.includeKorean) {
                currentQueue.add(
                    TTSItem(
                        text = sentence.korean,
                        processedText = sentence.korean,
                        isJapanese = false,
                        source = sentence
                    )
                )
            }
        }
        
        updateProgress()
        startService()
        playCurrentItem()
    }

    /**
     * 문단 리스트로 백그라운드 학습 시작 (문단별 랜덤, 문단 내 순차)
     */
    fun startParagraphLearning(paragraphs: List<ParagraphItem>, sentencesMap: Map<Int, List<SentenceItem>>) {
        if (!_isReady.value || paragraphs.isEmpty()) {
            println("BackgroundTTS Debug: TTS not ready or paragraphs empty. Ready=${_isReady.value}, Paragraphs=${paragraphs.size}")
            return
        }
        
        // 기존 SpeechManager TTS 정지하여 충돌 방지
        speechManager.stopSpeaking()
        
        println("BackgroundTTS Debug: Starting paragraph learning with ${paragraphs.size} paragraphs")
        println("BackgroundTTS Debug: SentencesMap size: ${sentencesMap.size}")
        
        currentQueue.clear()
        currentIndex = 0
        
        // 문단을 랜덤으로 섞기
        val shuffledParagraphs = paragraphs.shuffled()
        
        shuffledParagraphs.forEach { paragraph ->
            val sentences = sentencesMap[paragraph.paragraphId]?.sortedBy { it.orderInParagraph } ?: emptyList()
            
            println("BackgroundTTS Debug: Paragraph ${paragraph.paragraphId} has ${sentences.size} sentences")
            
            sentences.forEach { sentence ->
                val settings = _settings.value
                
                if (settings.includeJapanese) {
                    currentQueue.add(
                        TTSItem(
                            text = sentence.japanese,
                            processedText = JapaneseTextFilter.prepareTTSText(sentence.japanese),
                            isJapanese = true,
                            source = sentence,
                            paragraphTitle = paragraph.title
                        )
                    )
                }
                
                if (settings.includeKorean) {
                    currentQueue.add(
                        TTSItem(
                            text = sentence.korean,
                            processedText = sentence.korean,
                            isJapanese = false,
                            source = sentence,
                            paragraphTitle = paragraph.title
                        )
                    )
                }
            }
        }
        
        println("BackgroundTTS Debug: Total queue size: ${currentQueue.size}")
        
        if (currentQueue.isEmpty()) {
            println("BackgroundTTS Debug: Queue is empty, cannot start")
            return
        }
        
        updateProgress()
        startService()
        playCurrentItem()
    }

    private fun playCurrentItem() {
        if (currentIndex >= currentQueue.size) {
            // 큐 끝에 도달
            if (_settings.value.isRepeat) {
                currentIndex = 0 // 처음부터 다시
            } else {
                stop()
                return
            }
        }
        
        val item = currentQueue[currentIndex]
        _currentText.value = item.text
        
        val textToSpeak = if (item.processedText.isNotBlank()) item.processedText else item.text
        
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "background_tts_${currentIndex}")
        }
        
        textToSpeech?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "background_tts_${currentIndex}")
        updateProgress()
    }

    private fun playNextItem() {
        currentIndex++
        playCurrentItem()
    }

    private fun updateProgress() {
        _progress.value = TTSProgress(
            currentIndex = currentIndex,
            totalCount = currentQueue.size,
            currentItem = if (currentIndex < currentQueue.size) currentQueue[currentIndex] else null
        )
    }

    private fun startService() {
        val intent = Intent(context, BackgroundTTSService::class.java).apply {
            action = BackgroundTTSService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    /**
     * 재생/일시정지 토글
     */
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    /**
     * 일시정지
     */
    fun pause() {
        textToSpeech?.stop()
        _isPlaying.value = false
    }

    /**
     * 재개
     */
    fun resume() {
        if (currentQueue.isNotEmpty() && currentIndex < currentQueue.size) {
            playCurrentItem()
        }
    }

    /**
     * 다음 항목
     */
    fun skipNext() {
        textToSpeech?.stop()
        playNextItem()
    }

    /**
     * 이전 항목  
     */
    fun skipPrevious() {
        textToSpeech?.stop()
        if (currentIndex > 0) {
            currentIndex--
            playCurrentItem()
        }
    }

    /**
     * 정지 및 서비스 종료
     */
    fun stop() {
        textToSpeech?.stop()
        _isPlaying.value = false
        _currentText.value = ""
        currentQueue.clear()
        currentIndex = 0
        updateProgress()
        
        // 서비스 정지는 서비스 자체에서 관리하도록 함
        // 백그라운드 제한을 피하기 위해 직접 서비스 종료 시도하지 않음
    }

    /**
     * 설정 업데이트
     */
    fun updateSettings(newSettings: BackgroundTTSSettings) {
        _settings.value = newSettings
        
        // 속도와 피치 즉시 적용
        textToSpeech?.setSpeechRate(newSettings.speechRate)
        textToSpeech?.setPitch(newSettings.pitch)
    }

    /**
     * 리소스 정리
     */
    fun destroy() {
        coroutineScope.cancel()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}

/**
 * TTS 아이템 데이터 클래스
 */
data class TTSItem(
    val text: String,
    val processedText: String,
    val isJapanese: Boolean,
    val source: SentenceItem,
    val paragraphTitle: String? = null
)

/**
 * TTS 진행상황
 */
data class TTSProgress(
    val currentIndex: Int = 0,
    val totalCount: Int = 0,
    val currentItem: TTSItem? = null
) {
    val progressPercent: Float
        get() = if (totalCount > 0) currentIndex.toFloat() / totalCount.toFloat() else 0f
}

/**
 * 백그라운드 TTS 설정
 */
data class BackgroundTTSSettings(
    val includeJapanese: Boolean = true,
    val includeKorean: Boolean = false,
    val isRepeat: Boolean = true,
    val speechRate: Float = 0.8f,
    val pitch: Float = 1.0f,
    val pauseInterval: Long = 1000L // 문장 간 간격 (ms)
) 
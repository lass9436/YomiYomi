package com.lass.yomiyomi.media

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Provider

@Singleton
class ForegroundTTSManager @Inject constructor(
    private val context: Context,
    private val backgroundTTSManagerProvider: Provider<BackgroundTTSManager>
) : DefaultLifecycleObserver {
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    private val _foregroundTTSState = MutableStateFlow(ForegroundTTSState())
    val foregroundTTSState: StateFlow<ForegroundTTSState> = _foregroundTTSState.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // 현재 재생 중인 텍스트 추적
    private val _currentSpeakingText = MutableStateFlow("")
    val currentSpeakingText: StateFlow<String> = _currentSpeakingText.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    // 코루틴 스코프 추가
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var stopListeningJob: Job? = null

    init {
        initializeTTS()
        initializeSpeechRecognizer()

        // 🚀 앱 라이프사이클 관찰자 등록 - 백그라운드 시 TTS 자동 정지
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // 🎯 앱이 백그라운드로 갈 때 TTS 자동 정지
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopSpeaking() // 홈 버튼, 최근 앱 버튼 등으로 백그라운드 갈 때 즉시 정지
        stopListening() // 🔥 음성 인식도 중지
    }

    // 🔥 앱이 다시 foreground로 돌아올 때 SpeechRecognizer 재초기화
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        reinitializeSpeechRecognizer() // 음성 인식기 재초기화
    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.JAPANESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    _foregroundTTSState.value = _foregroundTTSState.value.copy(
                        error = "일본어 TTS가 지원되지 않습니다"
                    )
                } else {
                    // TTS 설정
                    textToSpeech?.setSpeechRate(0.8f) // 조금 느리게
                    textToSpeech?.setPitch(1.0f)

                    textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                            _currentSpeakingText.value = ""
                        }

                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                            _currentSpeakingText.value = ""
                            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                                error = "음성 재생 중 오류가 발생했습니다"
                            )
                        }
                    })

                    _foregroundTTSState.value = _foregroundTTSState.value.copy(
                        isTTSReady = true
                    )
                }
            } else {
                _foregroundTTSState.value = _foregroundTTSState.value.copy(
                    error = "TTS 초기화에 실패했습니다"
                )
            }
        }
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _isListening.value = true
                    _foregroundTTSState.value = _foregroundTTSState.value.copy(
                        error = null
                    )
                }

                override fun onBeginningOfSpeech() {
                    // 사용자가 말하기 시작함
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // 음성 레벨 변화 (시각적 피드백용)
                    _foregroundTTSState.value = _foregroundTTSState.value.copy(
                        audioLevel = rmsdB
                    )
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // 오디오 버퍼 수신
                }

                override fun onEndOfSpeech() {
                    _isListening.value = false
                }

                override fun onError(error: Int) {
                    _isListening.value = false
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 시간 초과"
                        SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
                        SpeechRecognizer.ERROR_AUDIO -> "오디오 오류"
                        SpeechRecognizer.ERROR_SERVER -> "서버 오류"
                        SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 시간 초과"
                        SpeechRecognizer.ERROR_NO_MATCH -> "일치하는 결과 없음"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기 사용 중"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 부족"
                        else -> "알 수 없는 오류"
                    }
                    _foregroundTTSState.value = _foregroundTTSState.value.copy(
                        error = errorMessage
                    )
                }

                override fun onResults(results: Bundle?) {
                    _isListening.value = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val recognizedText = matches[0]
                        _recognizedText.value = recognizedText
                        _foregroundTTSState.value = _foregroundTTSState.value.copy(
                            lastRecognizedText = recognizedText,
                            error = null
                        )
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        _foregroundTTSState.value = _foregroundTTSState.value.copy(
                            partialText = matches[0]
                        )
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    // 기타 이벤트
                }
            })

            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                isSpeechRecognitionAvailable = true
            )
        } else {
            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                error = "음성 인식을 사용할 수 없습니다"
            )
        }
    }

    /**
     * 음성 인식 시작
     */
    fun startListening() {
        // 🔥 SpeechRecognizer가 null이면 재초기화
        if (speechRecognizer == null) {
            reinitializeSpeechRecognizer()
        }

        if (!_foregroundTTSState.value.isSpeechRecognitionAvailable) {
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP") // 일본어 설정
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ja-JP")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        speechRecognizer?.startListening(intent)
    }

    /**
     * 음성 인식 중지 (0.5초 딜레이 후 실제 중지)
     */
    fun stopListening() {
        // UI에서는 즉시 중지된 것처럼 보이게 함
        _isListening.value = false

        // 기존 중지 작업이 있으면 취소
        stopListeningJob?.cancel()

        // 0.5초 후에 실제로 음성 인식 중지
        stopListeningJob = coroutineScope.launch {
            delay(500L) // 0.5초 딜레이
            speechRecognizer?.stopListening()
        }
    }

    /**
     * 인식된 텍스트 초기화
     */
    fun clearRecognizedText() {
        _recognizedText.value = ""
        _foregroundTTSState.value = _foregroundTTSState.value.copy(
            lastRecognizedText = "",
            partialText = ""
        )
    }

    /**
     * 텍스트를 일본어로 읽기 (원본 텍스트 추적 지원)
     */
    fun speakWithOriginalText(originalText: String, processedText: String, utteranceId: String = "yomiyomi_speech") {
        if (!_foregroundTTSState.value.isTTSReady) return

        // Provider를 통해 안전하게 참조
        if (backgroundTTSManagerProvider.get().isPlaying.value == true) {
            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                error = "백그라운드 학습이 진행 중입니다"
            )
            return
        }

        // 텍스트 검증 - 둘 다 비어있으면 실행하지 않음
        if (originalText.isBlank() && processedText.isBlank()) return

        try {
            // 원본 텍스트를 저장 (버튼 매칭용)
            _currentSpeakingText.value = originalText

            // 실제 TTS에 사용할 텍스트 결정 (processedText가 비어있으면 originalText 사용)
            val textToSpeak = if (processedText.isNotBlank()) processedText else originalText

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }

            textToSpeech?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        } catch (e: Exception) {
            // TTS 실패시 상태 초기화
            _isSpeaking.value = false
            _currentSpeakingText.value = ""
            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                error = "음성 재생 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 텍스트를 일본어로 읽기
     */
    fun speak(text: String, utteranceId: String = "yomiyomi_speech") {
        if (!_foregroundTTSState.value.isTTSReady) return
        if (text.isBlank()) return

        // Provider를 통해 안전하게 참조
        if (backgroundTTSManagerProvider.get().isPlaying.value == true) {
            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                error = "백그라운드 학습이 진행 중입니다"
            )
            return
        }

        try {
            // 원본 텍스트를 저장 (버튼 매칭용)
            _currentSpeakingText.value = text

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }

            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        } catch (e: Exception) {
            // TTS 실패시 상태 초기화
            _isSpeaking.value = false
            _currentSpeakingText.value = ""
            _foregroundTTSState.value = _foregroundTTSState.value.copy(
                error = "음성 재생 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * TTS 중지
     */
    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            // 중지 실패는 무시하고 상태만 초기화
        } finally {
            _isSpeaking.value = false
            _currentSpeakingText.value = ""
        }
    }

    /**
     * TTS 설정 변경
     */
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch)
    }

    /**
     * 특정 텍스트가 현재 재생 중인지 확인
     */
    fun isSpeakingText(text: String): Boolean {
        return _isSpeaking.value && _currentSpeakingText.value == text
    }

    /**
     * 리소스 정리
     */
    fun destroy() {
        // 🧹 라이프사이클 관찰자 해제
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)

        // 코루틴 스코프 정리
        stopListeningJob?.cancel()
        coroutineScope.cancel()

        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
        speechRecognizer = null
        textToSpeech = null
    }

    /**
     * SpeechRecognizer 재초기화 (백그라운드에서 돌아왔을 때)
     */
    private fun reinitializeSpeechRecognizer() {
        // 기존 SpeechRecognizer 정리
        speechRecognizer?.destroy()
        speechRecognizer = null

        // 에러 상태 초기화
        _foregroundTTSState.value = _foregroundTTSState.value.copy(
            error = null,
            isSpeechRecognitionAvailable = false
        )

        // 새로 초기화
        initializeSpeechRecognizer()
    }
}

data class ForegroundTTSState(
    val isSpeechRecognitionAvailable: Boolean = false,
    val isTTSReady: Boolean = false,
    val lastRecognizedText: String = "",
    val partialText: String = "",
    val audioLevel: Float = 0f,
    val error: String? = null
)

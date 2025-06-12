package com.lass.yomiyomi.media

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.media.BackgroundTTSSettings

@Singleton
class MediaManager @Inject constructor(
    private val foregroundTTSManager: ForegroundTTSManager,
    private val backgroundTTSManager: BackgroundTTSManager,
    private val speechRecognitionManager: SpeechRecognitionManager
) {
    val isListening: StateFlow<Boolean> get() = speechRecognitionManager.isListening
    val recognizedText: StateFlow<String> get() = speechRecognitionManager.recognizedText
    val foregroundTTSIsSpeaking get() = foregroundTTSManager.isSpeaking
    val foregroundTTSCurrentSpeakingText get() = foregroundTTSManager.currentSpeakingText
    val backgroundTTSIsPlaying get() = backgroundTTSManager.isPlaying
    val backgroundTTSIsReady get() = backgroundTTSManager.isReady
    val backgroundTTSCurrentText get() = backgroundTTSManager.currentText
    val backgroundTTSProgress get() = backgroundTTSManager.progress
    val backgroundTTSSettings get() = backgroundTTSManager.settings

    // 포그라운드 TTS와 음성인식(녹음)만 멈추고, 백그라운드 TTS는 멈추지 않음
    fun stopForegroundAndRecognition() {
        foregroundTTSManager.stopSpeaking()
        speechRecognitionManager.stopListening()
    }

    fun startListeningWithPolicy() {
        foregroundTTSManager.stopSpeaking()
        backgroundTTSManager.stop()
        speechRecognitionManager.clearRecognizedText()
        speechRecognitionManager.startListening()
    }

    fun stopListening() {
        speechRecognitionManager.stopListening()
    }

    fun clearRecognizedText() {
        speechRecognitionManager.clearRecognizedText()
    }

    fun stopBackgroundTTS() = backgroundTTSManager.stop()

    fun stopForegroundTTSSpeaking() = foregroundTTSManager.stopSpeaking()

    // 정책 적용: 포그라운드 TTS 재생 (백그라운드 TTS 중지 후 재생)
    fun playForegroundTTS(original: String, tts: String) {
        stopBackgroundTTS()
        foregroundTTSManager.speakWithOriginalText(original, tts)
    }

    // 정책 적용: 백그라운드 문장 학습 (포그라운드 TTS 중지 후 재생)
    fun playBackgroundSentenceLearning(sentences: List<SentenceItem>) {
        stopForegroundTTSSpeaking()
        backgroundTTSManager.startSentenceLearning(sentences)
    }

    // 정책 적용: 백그라운드 단락 학습 (포그라운드 TTS 중지 후 재생)
    fun playBackgroundParagraphLearning(paragraphs: List<ParagraphItem>, sentencesMap: Map<Int, List<SentenceItem>>) {
        stopForegroundTTSSpeaking()
        backgroundTTSManager.startParagraphLearning(paragraphs, sentencesMap)
    }

    fun updateBackgroundTTSSettings(settings: BackgroundTTSSettings) = backgroundTTSManager.updateSettings(settings)
} 
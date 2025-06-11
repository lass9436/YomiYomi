package com.lass.yomiyomi.media

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaManager @Inject constructor(
    val foregroundTTSManager: ForegroundTTSManager,
    val backgroundTTSManager: BackgroundTTSManager,
    val speechRecognitionManager: SpeechRecognitionManager
) {
    fun stopAll() {
        foregroundTTSManager.stopSpeaking()
        speechRecognitionManager.stopListening()
    }
} 
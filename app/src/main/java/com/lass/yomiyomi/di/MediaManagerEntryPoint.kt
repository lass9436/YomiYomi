package com.lass.yomiyomi.di

import com.lass.yomiyomi.media.ForegroundTTSManager
import com.lass.yomiyomi.media.BackgroundTTSManager
import com.lass.yomiyomi.media.SpeechRecognitionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MediaManagerEntryPoint {
    fun foregroundTTSManager(): ForegroundTTSManager
    fun backgroundTTSManager(): BackgroundTTSManager
    fun speechRecognitionManager(): SpeechRecognitionManager
} 

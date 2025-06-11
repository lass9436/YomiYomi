package com.lass.yomiyomi.di

import com.lass.yomiyomi.tts.ForegroundTTSManager
import com.lass.yomiyomi.tts.BackgroundTTSManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpeechManagerEntryPoint {
    fun speechManager(): ForegroundTTSManager
    fun backgroundTTSManager(): BackgroundTTSManager
} 

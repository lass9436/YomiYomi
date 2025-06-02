package com.lass.yomiyomi.di

import com.lass.yomiyomi.speech.SpeechManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpeechManagerEntryPoint {
    fun speechManager(): SpeechManager
} 
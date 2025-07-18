package com.lass.yomiyomi.di

import android.content.Context
import com.lass.yomiyomi.media.ForegroundTTSManager
import com.lass.yomiyomi.media.BackgroundTTSManager
import com.lass.yomiyomi.media.SpeechRecognitionManager
import com.lass.yomiyomi.media.MediaManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideForegroundTTSManager(
        @ApplicationContext context: Context
    ): ForegroundTTSManager {
        return ForegroundTTSManager(context)
    }

    @Provides
    @Singleton
    fun provideBackgroundTTSManager(
        @ApplicationContext context: Context
    ): BackgroundTTSManager {
        return BackgroundTTSManager(context)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognitionManager(
        @ApplicationContext context: Context
    ): SpeechRecognitionManager {
        return SpeechRecognitionManager(context)
    }

    @Provides
    @Singleton
    fun provideMediaManager(
        foregroundTTSManager: ForegroundTTSManager,
        backgroundTTSManager: BackgroundTTSManager,
        speechRecognitionManager: SpeechRecognitionManager
    ): MediaManager {
        return MediaManager(foregroundTTSManager, backgroundTTSManager, speechRecognitionManager)
    }
} 

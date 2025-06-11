package com.lass.yomiyomi.di

import android.content.Context
import com.lass.yomiyomi.tts.SpeechManager
import com.lass.yomiyomi.tts.BackgroundTTSManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideSpeechManager(
        @ApplicationContext context: Context,
        backgroundTTSManagerProvider: Provider<BackgroundTTSManager>
    ): SpeechManager {
        return SpeechManager(context, backgroundTTSManagerProvider)
    }

    @Provides
    @Singleton
    fun provideBackgroundTTSManager(
        @ApplicationContext context: Context,
        speechManager: SpeechManager
    ): BackgroundTTSManager {
        return BackgroundTTSManager(context, speechManager)
    }
} 

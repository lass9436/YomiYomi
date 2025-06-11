package com.lass.yomiyomi.di

import android.content.Context
import com.lass.yomiyomi.tts.ForegroundTTSManager
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
    ): ForegroundTTSManager {
        return ForegroundTTSManager(context, backgroundTTSManagerProvider)
    }

    @Provides
    @Singleton
    fun provideBackgroundTTSManager(
        @ApplicationContext context: Context,
        foregroundTTSManager: ForegroundTTSManager
    ): BackgroundTTSManager {
        return BackgroundTTSManager(context, foregroundTTSManager)
    }
} 

package com.lass.yomiyomi.di

import android.content.Context
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.speech.BackgroundTTSManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideSpeechManager(
        @ApplicationContext context: Context
    ): SpeechManager {
        return SpeechManager(context)
    }

    @Provides
    @Singleton
    fun provideBackgroundTTSManager(
        @ApplicationContext context: Context
    ): BackgroundTTSManager {
        return BackgroundTTSManager(context)
    }
} 
package com.lass.yomiyomi.di

import com.lass.yomiyomi.media.MediaManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MediaManagerEntryPoint {
    fun mediaManager(): MediaManager
} 

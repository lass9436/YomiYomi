package com.lass.yomiyomi.di

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.repository.StudyRepository
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.data.repository.MyWordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideStudyRepository(@ApplicationContext context: Context): StudyRepository {
        return StudyRepository(context)
    }

    @Provides
    @Singleton
    fun provideKanjiRepository(@ApplicationContext context: Context): KanjiRepository {
        return KanjiRepository(context)
    }

    @Provides
    @Singleton
    fun provideWordRepository(@ApplicationContext context: Context): WordRepository {
        return WordRepository(context)
    }

    @Provides
    @Singleton
    fun provideMyWordRepository(@ApplicationContext context: Context): MyWordRepository {
        return MyWordRepository(context)
    }
} 

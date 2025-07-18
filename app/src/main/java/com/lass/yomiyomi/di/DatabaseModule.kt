package com.lass.yomiyomi.di

import android.content.Context
import com.lass.yomiyomi.data.database.AppDatabase
import com.lass.yomiyomi.data.repository.StudyRepository
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.ParagraphListMappingRepository
import com.lass.yomiyomi.data.repository.ParagraphListRepository
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

    @Provides
    @Singleton
    fun provideMyKanjiRepository(@ApplicationContext context: Context): MyKanjiRepository {
        return MyKanjiRepository(context)
    }

    @Provides
    @Singleton
    fun provideSentenceRepository(@ApplicationContext context: Context): MySentenceRepository {
        return MySentenceRepository(context)
    }

    @Provides
    @Singleton
    fun provideParagraphRepository(@ApplicationContext context: Context): MyParagraphRepository {
        return MyParagraphRepository(context)
    }

    @Provides
    @Singleton
    fun provideParagraphListRepository(@ApplicationContext context: Context): ParagraphListRepository {
        return ParagraphListRepository(context)
    }

    @Provides
    @Singleton
    fun provideParagraphListMappingRepository(@ApplicationContext context: Context): ParagraphListMappingRepository {
        return ParagraphListMappingRepository(context)
    }

} 

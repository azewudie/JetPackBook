package com.example.googlebooksapi.di.module

import com.example.googlebooksapi.model.remote.BookApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object NetworkModule {
    @Provides
    fun provideBookApi() = BookApi()

}
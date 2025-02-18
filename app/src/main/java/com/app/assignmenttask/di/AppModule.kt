package com.app.assignmenttask.di

import com.app.assignmenttask.network.ApiService
import com.app.assignmenttask.presentation.BookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInterface(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/sebskuse/a57b26640883bd70ee5ac092a5cdbfce/raw/2e6964b4eeaa5203117043d046754b36d8da503d/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): BookRepository {
        return BookRepository(apiService)
    }
}
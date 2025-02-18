package com.app.assignmenttask.di

import android.content.Context
import androidx.room.Room
import com.app.assignmenttask.data.local.FavouriteBookDao
import com.app.assignmenttask.data.local.FavouriteBookDatabase
import com.app.assignmenttask.data.remote.ApiService
import com.app.assignmenttask.presentation.BookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideRepository(apiService: ApiService, favouriteBookDao: FavouriteBookDao): BookRepository {
        return BookRepository(apiService, favouriteBookDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FavouriteBookDatabase {
        return Room.databaseBuilder(
            context,
            FavouriteBookDatabase::class.java,
            FavouriteBookDatabase.DB_NAME
        ).build()
    }

    @Provides
    fun provideFavouriteBookDao(database: FavouriteBookDatabase): FavouriteBookDao {
        return database.favouriteBookDao
    }
}
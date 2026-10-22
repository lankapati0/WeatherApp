package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.data.Room.SavedCityDao
import com.example.weatherapp.data.Room.SelectedLocationDao
import com.example.weatherapp.data.Room.WeatherDatabase
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
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase{
        return WeatherDatabase.getDatabase(context)
    }

    @Provides
    fun provideSavedCityDao(
        database: WeatherDatabase
    ): SavedCityDao{
        return database.savedCityDao()
    }

    @Provides
    fun provideSelectedLocationDao(
        database: WeatherDatabase
    ): SelectedLocationDao{
        return database.selectedLocationDao()
    }

}
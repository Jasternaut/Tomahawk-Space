package com.example.tomahawkspace.di

import com.example.tomahawkspace.data.api.AuthInterceptor
import com.example.tomahawkspace.data.api.NasaApi
import com.example.tomahawkspace.data.local.SettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.nasa.gov/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(settingsManager: SettingsManager): AuthInterceptor {
        return AuthInterceptor(settingsManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNasaApi(retrofit: Retrofit): NasaApi {
        return retrofit.create(NasaApi::class.java)
    }
}

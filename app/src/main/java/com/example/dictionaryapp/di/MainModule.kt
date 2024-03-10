package com.example.dictionaryapp.di

import com.example.dictionaryapp.data.api.DictionaryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    // Create an HTTP logging interceptor for logging network requests and responses
   // HttpLoggingInterceptor is used to log HTTP request and response information.
    // The logging level is set to BODY, which logs headers, body, and metadata.
    private val interceptor: HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    // Create an OkHttpClient with the logging interceptor
   // An instance of OkHttpClient is created using the OkHttpClient.Builder.
    //The logging interceptor is added to the OkHttpClient.
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    // Dagger Hilt annotation indicating that this method provides a singleton instance
    @Provides
    @Singleton
    fun providesDictionaryApi(): DictionaryApi {
        // Use Retrofit to build and create an instance of DictionaryApi
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(DictionaryApi.BASE_URL)
            .client(client)
            .build()
            .create(DictionaryApi::class.java)
    }

    //This function creates and returns an instance of DictionaryApi using Retrofit.
    //It sets up a Retrofit instance with a Gson converter factory for JSON parsing.
    //The base URL for the API is set using DictionaryApi.BASE_URL.
    //The OkHttpClient with the logging interceptor is set as the client.
    //Finally, the create method is used to create an instance of the DictionaryApi interface.
}

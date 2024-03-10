package com.example.dictionaryapp.data.repository

import android.app.Application
import com.example.dictionaryapp.data.api.DictionaryApi
import com.example.dictionaryapp.data.dto.WordItemDto
import com.example.dictionaryapp.data.mapper.toWordItem
import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import com.example.dictionaryapp.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException


import javax.inject.Inject

class DictionaryRepositoryImpl@Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val application: Application
): DictionaryRepository {

    override suspend fun getWordResult(
        word: String
    ): Flow<Result<WordItem>> {
        return flow {
            emit(Result.Loading(true))

            //we get the result here after loading

            val remoteWordResulDto = try{
                dictionaryApi.getWordResult(word)
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error("Cannot get the Word Result"))
                emit(Result.Loading(false))
                return@flow
            }catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error("Cannot get the Word Result"))
                emit(Result.Loading(false))
                return@flow
            }catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error("Cannot get the Word Result"))
                emit(Result.Loading(false))
                return@flow
            }


            remoteWordResulDto?.let{wordResultDto->
            wordResultDto[0]?.let{
                wordItemDto ->
                emit(Result.Success(wordItemDto.toWordItem()))

                emit(Result.Loading(false))
                return@flow
            }



            }

            emit(Result.Error("Cannot get the Word Result"))
            emit(Result.Loading(false))


        }

    }

}
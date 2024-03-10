package com.example.dictionaryapp.domain.repository

import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.util.Result
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun getWordResult(
        word : String,

    ): Flow<Result<WordItem>>
}
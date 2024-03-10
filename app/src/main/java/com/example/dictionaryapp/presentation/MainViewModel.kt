package com.example.dictionaryapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import com.example.dictionaryapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) : ViewModel(){

    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    private var searchJob : Job? = null

    init {
        _mainState.update {
            it.copy(searchWord =  "english")
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            loadWordResult()
        }

    }

    fun onEvent(mainUiEvent: MainUiEvent){
        when(mainUiEvent){
            MainUiEvent.OnSearchClick -> {
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    loadWordResult()
                }
            }
            is MainUiEvent.OnSearchWordChange -> {
                _mainState.update{
                    it.copy(
                        searchWord = mainUiEvent.newWord.lowercase()
                    )

                }
            }
        }
    }

    private fun loadWordResult(){
        viewModelScope.launch{
            dictionaryRepository.getWordResult(
                mainState.value.searchWord
            ).collect{result ->
                when(result){
                    is Result.Error -> Unit
                    is Result.Loading -> {
                        _mainState.update{
                            it.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                    is Result.Success -> {
                        result.data?.let {wordItem->
                            _mainState.update {
                                it.copy(
                                    wordItem = wordItem
                                )
                            }
                        }
                    }
                }

            }

        }
    }

}
package com.example.dictionaryapp.presentation

sealed class MainUiEvent {

    data class OnSearchWordChange(val newWord : String): MainUiEvent()
    object OnSearchClick : MainUiEvent()
}
package com.example.dictionaryapp.domain.model

data class Definition(
    val antonyms: List<Any>? = null,
    val definition: String? = null,
    val example: String? = null,
    val synonyms: List<String>? = null
)
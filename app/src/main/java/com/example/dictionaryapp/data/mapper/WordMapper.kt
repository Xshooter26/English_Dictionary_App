package com.example.dictionaryapp.data.mapper

import com.example.dictionaryapp.data.dto.DefinitionDto
import com.example.dictionaryapp.data.dto.MeaningDto
import com.example.dictionaryapp.data.dto.WordItemDto
import com.example.dictionaryapp.domain.model.Definition
import com.example.dictionaryapp.domain.model.Meaning
import com.example.dictionaryapp.domain.model.WordItem

fun WordItemDto.toWordItem() = WordItem(
    word = word ?: "",
    meanings = meanings?.map{

        it.toMeaning()
    }?: emptyList(),
    phonetic = phonetic ?: ""
)

fun MeaningDto.toMeaning() = Meaning(
//    definitions = definitionDtoToDefinition(definitions?.get(0)),
    definitions = definitionDtoToDefinition(definitions!![0]),
    partOfSpeech = partOfSpeech?: ""
)

fun definitionDtoToDefinition(
    definitionDto: DefinitionDto
) = Definition(
    antonyms = definitionDto.synonyms?: listOf(),
    definition = definitionDto.definition ?: "",
    example = definitionDto.example?: "",
    synonyms = definitionDto.synonyms?: listOf()
)


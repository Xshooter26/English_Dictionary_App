package com.example.dictionaryapp

import androidx.compose.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.domain.model.Meaning
import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.presentation.MainState
import com.example.dictionaryapp.presentation.MainUiEvent
import com.example.dictionaryapp.presentation.MainViewModel
import com.example.dictionaryapp.ui.theme.DictionaryAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DictionaryAppTheme {
                // A surface container using the 'background' color from the theme
                BarColor()
                val mainViewModel = hiltViewModel<MainViewModel>()
                val mainState by mainViewModel.mainState.collectAsState()
                val keyboardController = LocalSoftwareKeyboardController.current
                val keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search  // Set the keyboard action to Search
                )
                val outlineColors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green, // Change the outline color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary, // Change the outline color when not focused
                    focusedTrailingIconColor = Color.Green
                )


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 17.dp)
                               ,
                            colors = outlineColors,
                            value = mainState.searchWord,
                            onValueChange = {
                                mainViewModel.onEvent(
                                    MainUiEvent.OnSearchWordChange(it)
                                )
                            }, trailingIcon = {
                                Icon(imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clickable {
                                            mainViewModel.onEvent(
                                                MainUiEvent.OnSearchClick
                                            )
                                        })
                            },  keyboardOptions = keyboardOptions,
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    // Handle the search action here
                                    mainViewModel.onEvent(
                                        MainUiEvent.OnSearchClick
                                    )
                                    keyboardController?.hide() // Optionally hide the keyboard
                                }
                            ),
                            label = {
                                Text(
                                    text = "Search a Word", fontSize = 16.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 19.6.sp
                            )
                        )
                    }

                ) { paddingValues ->
                    val padding = paddingValues

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
                    ) {
                        MainScreen(mainState)
                    }
                }


            }
        }
    }


    @Composable
    private fun MainScreen(
        mainState: MainState
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 30.dp)
            ) {
                mainState.wordItem?.let { wordItem ->
                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = wordItem.word!!,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wordItem.phonetic!!,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                }
            }

                Box(
                    modifier = Modifier
                        .padding(top = 110.dp)
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 50.dp,
                                topEnd = 50.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.8f))
                ) {
                    if (mainState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        mainState.wordItem?.let { wordItem ->
                            WordResult(wordItem)
                        }
                    }

                }
            }
        }


    }

    @Composable
    fun WordResult(wordItem: WordItem) {
        LazyColumn(contentPadding = PaddingValues(vertical = 32.dp)) {
            items(wordItem.meanings!!.size) { index ->
                Meaning(
                    meaning = wordItem.meanings[index],
                    index = index
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }


    @Composable
    fun Meaning(
        meaning: Meaning,
        index: Int
    ) {

        Column(modifier = Modifier.fillMaxWidth()
            .padding (17.dp)
        ) {
            Text(
                text = "${index + 1}.${meaning.partOfSpeech}",
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(0.4f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(
                        top = 2.dp, bottom = 4.dp,
                        start = 12.dp, end = 12.dp
                    )
            )
            if(meaning.definitions?.definition!!.isNotEmpty()){

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp)


                ){

                    Text(
                        text = "Definition:   ",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp,

                            ))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = meaning.definitions.definition,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )


                }
            }

            if(meaning.definitions.example!!.isNotEmpty()){

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp)


                ){

                    Text(
                        text = "Example:  ",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp,

                            ))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = meaning.definitions.example,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )


                }
            }
        }

    }

    @Composable
    private fun BarColor() {
        val systemUiController = rememberSystemUiController()
        val color = MaterialTheme.colorScheme.background
        LaunchedEffect(color) {
            systemUiController.setSystemBarsColor(
                color
            )
        }
    }


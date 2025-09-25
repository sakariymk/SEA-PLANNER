package no.uio.ifi.in2000.gruppe40.prosjekt.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import no.uio.ifi.in2000.gruppe40.prosjekt.R
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather.WeatherViewModel
import java.util.Locale
import android.content.res.Configuration
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController


@Composable
fun HomeScreen(weatherviewModel: WeatherViewModel, homeScreenViewModel: HomeScreenViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val options = listOf("skjul", "nedbør", "vind", "strøm", "bølgehøyde")
    val selectedOption = weatherviewModel.selectedOption
    var searchText by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val searchMode by homeScreenViewModel.searchMode.collectAsState()
    val savableStateHolder = rememberSaveableStateHolder()
    val isLoading by homeScreenViewModel.isLoading.collectAsState()
    val higherGribValue = homeScreenViewModel.upperValue

    //Controlling keyboard and searchbar
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    //Internett-Error Dialog
    var showDialog by remember { mutableStateOf(false) }
    var temporaryDisable by remember { mutableStateOf(false) }
    val buttonOpacity = if (isNetworkAvailable(context)) 1f else 0.3f

    LaunchedEffect(Unit) {
        if (!isNetworkAvailable(context)) {
            showDialog = true
            temporaryDisable = true
        }
    }

    LaunchedEffect(searchMode) {
        if (searchMode) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    //shows the network
    if (showDialog) {
        NoInternetDialog(
            onClose = { showDialog = false },
            onDismiss = { showDialog = false }
        )
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLandscape) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "SEA PLANNER",
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.fishing), // the app icon
                        contentDescription = "Boat icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                savableStateHolder.SaveableStateProvider(key = "MapView") {
                    MapView(weatherviewModel, homeScreenViewModel)
                }

                Row(
                    Modifier
                        .fillMaxWidth(),
                ) {

                    // if the search button is on, display search bar
                    if (searchMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                label = {
                                    Text("Søk sted")
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),

                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    weatherviewModel.settZoomPosition(searchText, context)
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    homeScreenViewModel.toggleSearchMode()
                                }),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = Color.Gray,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                            )
                        }

                    }

                    // if the search button is off, removes the search bar from display
                    if (!searchMode) {
                        OutlinedButton(
                            onClick = { expanded = !temporaryDisable },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .alpha(buttonOpacity),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black,
                                containerColor = Color.White
                            )
                        ) {
                            Text(text = selectedOption.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            })
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown arrow"
                            )

                        GribRangeSlider(higherGribValue, homeScreenViewModel.maxSliderValue)

                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(option.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()
                                        ) else it.toString()
                                    })
                                },
                                onClick = {
                                    weatherviewModel.updateSelectedOption(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Displays the map view component
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                MapView(weatherviewModel, homeScreenViewModel)
            }
        }
    }
}

@Composable
fun GribRangeSlider(
    sliderValue: MutableState<Float>,
    maxAllowedValue: MutableState<Float>
) {
    Column {
        Slider(
            value = sliderValue.value,
            onValueChange = { newValue ->
                sliderValue.value = newValue
            },
            valueRange = 0f..maxAllowedValue.value,
            steps = 10
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Text(text = "${roundFloat(sliderValue.value)}")
        }

    }
}

// returns a double, limits amount of decimals to 1
@SuppressLint("DefaultLocale")
fun roundFloat(value: Float, decimals: Int = 1): String {
    return "%.${decimals}f".format(value)
}
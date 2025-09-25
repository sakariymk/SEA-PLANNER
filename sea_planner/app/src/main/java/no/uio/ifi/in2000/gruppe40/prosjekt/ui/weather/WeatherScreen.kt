package no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.gruppe40.prosjekt.R
import no.uio.ifi.in2000.gruppe40.prosjekt.model.weather.WeatherForecast
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val forecast by viewModel.weatherList.collectAsState()
    val days by viewModel.daysList.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val selectedDay by viewModel.selectedDate.collectAsState()

    val context = LocalContext.current
    val errorMessage = viewModel.errorMessage.value

    var expanded by remember { mutableStateOf(false) }
    var choosenItem by remember { mutableStateOf("Din posisjon") }

    val locationAccessDenied = viewModel.locationAccessDenied.value

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // markers:

    val markers by viewModel.theMarkersOfThemap.collectAsState()

    val alternatives = buildList {
        if (!locationAccessDenied) add("Din posisjon")
        add("Oslo")
        add("Bergen")
        add("Stavanger")
        add("Trondheim")
        add("Kristiansand")
        addAll(markers.map { it.name })
    }


    LaunchedEffect(choosenItem) {
        when (choosenItem) {
            "Oslo" -> viewModel.fetchWeatherByLocation(59.9133, 10.7389, "Oslo")
            "Bergen" -> viewModel.fetchWeatherByLocation(60.39299, 5.32415, "Bergen")
            "Stavanger" -> viewModel.fetchWeatherByLocation(58.9700, 5.7314, "Stavanger")
            "Trondheim" -> viewModel.fetchWeatherByLocation(63.4297, 10.3933, "Trondheim")
            "Kristiansand" -> viewModel.fetchWeatherByLocation(58.1472, 7.9972, "Kristiansand")
            "Din posisjon" -> viewModel.fetchWeatherForUser(context)
            else -> {
                val item = markers.find { it.name == choosenItem }
                item?.let {
                    viewModel.fetchWeatherByLocation(
                        it.position.latitude,
                        it.position.longitude
                    )
                }
            }
        }
    }

    Column (modifier = Modifier
    ) {
        if (!isLandscape) {
            Text(
                "Værvarsel",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )
            HorizontalDivider()
        }
        Row(
            modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(10.dp),
        ) {
                Text(
                    choosenItem,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                )


            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .size(30.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
                ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown arrow"
                )
            }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))

        ) {
            alternatives.forEach { alternative ->
                val blocked = alternative == "Din posisjon" && locationAccessDenied
                DropdownMenuItem(
                    text = { Text(alternative) },
                    onClick = {
                        if (!blocked) {
                            choosenItem = alternative
                            expanded = false
                        }
                    },
                    enabled = !blocked
                )

            }
        }
        }




        HorizontalDivider()

        //Errorscreen
        if (errorMessage != null) {
            Text(
                "$errorMessage",
                Modifier.padding(16.dp)
            )
        }

        LazyColumn {
            items(days) { item ->
                val date = item.date.substring(0,10)
                item.temperature.toInt()
                WeatherCard(
                    forecast = item,
                    symbolCode = item.symbolCode,
                    onClick = {

                    viewModel.setSelectedDate(date)
                        viewModel.openDialog()

                    },
                )

            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.closeDialog()
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = { viewModel.closeDialog() }) {
                            Text("Lukk")
                        }
                    }
                },
                title = {
                    if (!isLandscape) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Time-for-time værvarsel", fontWeight = FontWeight.Bold)
                            HorizontalDivider()
                            Text("$selectedDay", fontWeight = FontWeight.Bold)
                        }
                    }

                },
                text = {
                    LazyColumn {
                        val now = Instant.now()
                        val filteredForecast = forecast.filter { item ->
                            try {
                                val forecastTime = Instant.parse(item.date)
                                val isSameDay = item.date.substring(0, 10) == viewModel.selectedDate.value
                                isSameDay && forecastTime.isAfter(now)
                            } catch (e: Exception) {
                                false
                            }
                        }
                        items(filteredForecast) { item ->
                            HourlyForecastCard(
                                forecast = item,
                                date = item.date,
                                symbolCode = item.symbolCode,
                                windSpeed = item.windSpeed,
                                windDirection = item.windDirection
                            )

                        }
                    }
                }
            )
        }


    }

    LaunchedEffect(Unit) {
        viewModel.fetchWeatherForUser(context)
    }

}

@Composable
fun WeatherCard(
    forecast: WeatherForecast,
    symbolCode: String,
    onClick: (String) -> Unit,
) {
    val date = forecast.date.substring(0,10)
    val temp = forecast.temperature
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(date) }
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier
                .padding(10.dp)) {
                Text(text = date,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                    .align(Alignment.CenterHorizontally))
                HorizontalDivider()
                Text(text = "${temp}°C",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = fetchImage(symbolCode),
                        contentDescription = symbolCode,
                        modifier = Modifier.size(100.dp)
                    )
                    Text(text = changeLanguage(symbolCode),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally))
                }
            }
        }

    }
}

@Composable
fun HourlyForecastCard(
    forecast: WeatherForecast,
    date: String,
    symbolCode: String,
    windSpeed: Double,
    windDirection: Double
) {

    themedIcons(symbolCode)
    val timeFormatted = try {
        val parsed = Instant.parse(date)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(parsed)
    } catch (e: Exception) {
        date
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(timeFormatted, fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp))
            HorizontalDivider()
            Row {
                Image(
                    painter = painterResource(themedIcons("thermometer")),
                    contentDescription = "icon indicating wind",
                    modifier = Modifier.size(40.dp)
                )
                Text("${forecast.temperature}°C",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp))
            }

            Row {
                Image(
                    painter = painterResource(themedIcons("windy")),
                    contentDescription = "icon indicating wind",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(10.dp))
                Text("$windSpeed m/s")
            }
            RotatableWindArrow(windDirection)
        }

            Box {
                Column(
                    modifier = Modifier
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = fetchImage(symbolCode),
                        contentDescription = symbolCode,
                        modifier = Modifier.size(100.dp)
                    )
                    Text(text = changeLanguage(symbolCode),
                        modifier = Modifier.padding(20.dp))
                }
            }
    }
}

@Composable
fun RotatableWindArrow(direction: Double) {
    Image(
        painter = painterResource(themedIcons("black_arrow")),
        contentDescription = "icon indicating wind direction",
        modifier = Modifier.size(20.dp)
            .graphicsLayer (
                rotationZ = direction.toFloat()
            )
    )
}

fun changeLanguage(symbolCode: String): String {
    val word = when (symbolCode) {
        //clear-sky
        "clearsky_day" -> "klarvær dag"
        "clearsky_night" -> "klarvær natt"

        //fair
        "fair_day" -> "pent vær dag"
        "fair_night" -> "pent vær natt"

        //partlycloudy
        "partlycloudy_day" -> "delvis skyet dag"
        "partlycloudy_night" -> "delvis skyet natt"

        //fog
        "fog" -> "tåke"

        //cloudy
        "cloudy" -> "overskyet"

        //rain
        "lightrain" -> "lett regn"
        "rain" -> "regn"
        "heavyrain" -> "kraftig regn"

        //sleet
        "lightsleet" -> "lett sludd"
        "sleet" -> "sludd"
        "heavysleet" -> "kraftig sludd"

        //snow
        "lightsnow" -> "lett snø"
        "snow" -> "snø"
        "heavysnow" -> "kraftig snø"

        //rain-shower
        "lightrainshowers_day" -> "lette regnbyger dag"
        "lightrainshowers_night" -> "lette regnbyger natt"
        "rainshowers_day" -> "regnbyger dag"
        "rainshowers_night" -> "regnbyger natt"
        "heavyrainshowers_day" -> "kraftige regnbyger dag"
        "heavyrainshowers_night" -> "kraftige regnbyger natt"

        //sleet-shower
        "lightsleetshowers_day" -> "lette sluddbyger dag"
        "lightsleetshowers_night" -> "lette sluddbyger natt"
        "sleetshowers_day" -> "sluddbyger dag"
        "sleetshowers_night" -> "sluddbyger natt"
        "heavysleetshowers_day" -> "kraftige sluddbyger dag"
        "heavysleetshowers_night" -> "kraftige sluddbyger natt"

        //snow-shower
        "lightsnowshowers_day" -> "lette snøbyger dag"
        "lightsnowshowers_night" -> "lette snøbyger natt"
        "snowshowers_day" -> "snøbyger dag"
        "snowshowers_night" -> "snøbyger natt"
        "heavysnowshowers_day" -> "kraftige snøbyger dag"
        "heavysnowshowers_night" -> "kraftige snøbyger natt"

        //rain shower + thunder
        "lightrainshowersandthunder_day" -> "lette regnbyger med torden dag"
        "lightrainshowersandthunder_night" -> "lette regnbyger med torden natt"
        "rainshowersandthunder_day" -> "regnbyger med torden dag"
        "rainshowersandthunder_night" -> "regnbyger med torden natt"
        "heavyrainshowersandthunder_day" -> "kraftige regnbyger med torden dag"
        "heavyrainshowersandthunder_night" -> "kraftige regnbyger med torden natt"

        //sleet + shower + thunder
        "lightssleetshowersandthunder_day" -> "lette sluddbyger med torden dag"
        "lightssleetshowersandthunder_night" -> "lette sluddbyger med torden natt"
        "sleetshowersandthunder_day" -> "sluddbyger med torden dag"
        "sleetshowersandthunder_night" -> "sluddbyger med torden natt"
        "heavysleetshowersandthunder_day" -> "kraftige sluddbyger med torden dag"
        "heavysleetshowersandthunder_night" -> "kraftige sluddbyger med torden natt"

        // snow + shower + thunder
        "lightsnowshowersandthunder_day" -> "lette snøbyger med torden dag"
        "lightsnowshowersandthunder_night" -> "lette snøbyger med torden natt"
        "snowshowersandthunder_day" -> "snøbyger med torden dag"
        "snowshowersandthunder_night" -> "snøbyger med torden natt"
        "heavysnowshowersandthunder_day" -> "kraftige snøbyger med torden dag"
        "heavysnowshowersandthunder_night" -> "kraftige snøbyger med torden natt"

        //rain + thunder
        "lightrainandthunder" -> "lett regn med torden"
        "rainandthunder" -> "regn med torden"
        "heavyrainandthunder" -> "kraftig regn med torden"

        //sleet + thunder
        "lightsleetandthunder" -> "lett sludd med torden"
        "sleetandthunder" -> "sludd med torden"
        "heavysleetandthunder" -> "kraftig sludd med torden"

        //snow + thunder
        "lightsnowandthunder" -> "lett snø med torden"
        "snowandthunder" -> "snø med torden"
        "heavysnowandthunder" -> "kraftig snø med torden"

        else -> "ukjent vær"
    }
    return word
}


@Composable
fun fetchImage(symbolCode: String): Painter {
    val iconRes = when (symbolCode) {
        //based on weather icons from Yr
        //https://hjelp.yr.no/hc/en-us/articles/203786121-Weather-symbols-on-Yr

        //clear-sky
        "clearsky_day" -> R.drawable._01d
        "clearsky_night" -> R.drawable._01n

        //fair
        "fair_day" -> R.drawable._02d
        "fair_night" -> R.drawable._02n

        //partlycloudy
        "partlycloudy_day" -> R.drawable._03d
        "partlycloudy_night" -> R.drawable._03n

        //fog
        "fog" -> R.drawable._15

        //cloudy
        "cloudy" -> R.drawable._04

        //rain
        "lightrain" -> R.drawable._46
        "rain" -> R.drawable._09
        "heavyrain" -> R.drawable._10

        //sleet
        "lightsleet" -> R.drawable._47
        "sleet" -> R.drawable._12
        "heavtsleet" -> R.drawable._48

        //snow
        "lightsnow" -> R.drawable._49
        "snow" -> R.drawable._13
        "heavysnow" -> R.drawable._50

        //rain-shower
        "lightrainshowers_day" -> R.drawable._40d
        "lightrainshowers_night" -> R.drawable._40n
        "rainshowers_day" -> R.drawable._05d
        "rainshowers_night" -> R.drawable._05n
        "heavyrainshowers_day" -> R.drawable._41d
        "heavyrainshowers_night" -> R.drawable._41n

        //sleet-shower
        "lightsleetshowers_day" -> R.drawable._42d
        "lightsleetshowers_night" -> R.drawable._42n
        "sleetshowers_day" -> R.drawable._07d
        "sleetshowers_night" -> R.drawable._07n
        "heavysleetshowers_day" -> R.drawable._43d
        "heavysleetshowers_night" -> R.drawable._43n

        //snow-shower
        "lightsnowshowers_day" -> R.drawable._44d
        "lightsnowshowers_night" -> R.drawable._44n
        "snowshowers_day" -> R.drawable._08d
        "snowshowers_night" -> R.drawable._08n
        "heavysnowshowers_day" -> R.drawable._45d
        "heavysnowshowers_night" -> R.drawable._45n

        //rain shower + thunder
        "lightrainshowersandthunder_day" -> R.drawable._24d
        "lightrainshowersandthunder_night" -> R.drawable._24n
        "rainshowersandthunder_day" -> R.drawable._06d
        "rainshowersandthunder_night" -> R.drawable._06n
        "heavyrainshowersandthunder_day" -> R.drawable._25d
        "heavyrainshowersandthunder_night" -> R.drawable._25n

        //sleet + shower + thunder
        "lightssleetshowersandthunder_day" -> R.drawable._26d
        "lightssleetshowersandthunder_night" -> R.drawable._26n
        "sleetshowersandthunder_day" -> R.drawable._20d
        "sleetshowersandthunder_night" -> R.drawable._20n
        "heavysleetshowersandthunder_day" -> R.drawable._27d
        "heavysleetshowersandthunder_night" -> R.drawable._27n

        // snow + shower + thunder
        "lightsnowshowersandthunder_day" -> R.drawable._28d
        "lightsnowshowersandthunder_night" -> R.drawable._28n
        "snowshowersandthunder_day" -> R.drawable._21d
        "snowshowersandthunder_night" -> R.drawable._21n
        "heavysnowshowersandthunder_day" -> R.drawable._29d
        "heavysnowshowersandthunder_night" -> R.drawable._29n

        //rain + thunder
        "lightrainandthunder" -> R.drawable._30
        "rainandthunder" -> R.drawable._22
        "heavyrainandthunder" -> R.drawable._11

        //sleet + thunder
        "lightsleetandthunder" -> R.drawable._31
        "sleetandthunder" -> R.drawable._23
        "heavysleetandthunder" -> R.drawable._32

        //snow + thunder
        "lightsnowandthunder" -> R.drawable._33
        "snowandthunder" -> R.drawable._14
        "heavysnowandthunder" -> R.drawable._34

        else -> R.drawable.warning
    }
    return painterResource(id = iconRes)
}

//changes icons depending on enabling of DarkMode
@Composable
fun themedIcons(icons: String): Int {
    val isDark = isSystemInDarkTheme()

    return when (icons) {
        "thermometer" -> if (isDark) R.drawable.white_thermometer else R.drawable.thermometer
        "windy" -> if (isDark) R.drawable.white_windy else R.drawable.windy
        "black_arrow" -> if (isDark) R.drawable.white_arrow else R.drawable.black_arrow
        else -> R.drawable.warning
    }
}

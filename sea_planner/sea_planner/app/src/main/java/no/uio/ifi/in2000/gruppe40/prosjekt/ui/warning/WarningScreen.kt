package no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Feature
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.MetalertRetrofit
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun WarningScreen() {

    //Local UI states for loading, error and selected warning
    var notifications by remember { mutableStateOf<List<Feature>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedNotification by remember { mutableStateOf<Feature?>(null) }

    //fetch data when composable is launched
    LaunchedEffect(Unit) {
        try {
            val result = MetalertRetrofit.api.getMetalerts()
            notifications = result.features
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Feil ved henting av data: ${e.message}"
        }
    }

    //shows loading, error or alerts based on state
    if (isLoading) {
        Text("Henter data...")
    } else if (errorMessage != null) {
        Text("Feil: $errorMessage")
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { feature ->
                    WarningCard(
                        feature = feature,
                        eventAwarenessName = feature.properties.eventAwarenessName,
                        area = feature.properties.area,
                        time = feature.properties.eventEndingTime,
                        onClick = { selectedNotification = feature }
                    )
                }
            }
        }
    }

    //when an alert is selected, it shows the detail dialog
    //when dismissed the detail dialog is closed
    selectedNotification?.let { feature ->
        WarningDetailDialog(
            feature = feature,
            onDismiss = { selectedNotification = null }
        )
    }
}

//Defines look of an alert on the screen
@Composable
fun WarningCard(
    feature: Feature,
    eventAwarenessName: String?,
    area: String?,
    time: String?,
    onClick: () -> Unit
) {

    val backgroundColor = parseRiskMatrixColor(feature.properties.riskMatrixColor)

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable{onClick()},
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append(eventAwarenessName)
                    append(", ")
                    append(area)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Sluttidspunkt: ")
                    }
                    append(formatDateTime(time))
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}


//Dialog for selected alert with detailed description of alert
@Composable
fun WarningDetailDialog(feature: Feature, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = feature.properties.eventAwarenessName) },
        text = {
            Column (
                //scrollable for long descriptions
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 400.dp).verticalScroll(rememberScrollState())
            ){
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Område: ")
                        }
                        append(feature.properties.area)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Sluttidspunkt: ")
                        }
                        append(formatDateTime(feature.properties.eventEndingTime))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Alvorlighetsgrad: ")
                        }
                        append(feature.properties.severity)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Sikkerhet: ")
                        }
                        append(feature.properties.certainty)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Beskrivelse:", fontWeight = FontWeight.Bold)
                Text(text = feature.properties.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Konsekvenser:", fontWeight = FontWeight.Bold)
                Text(text = feature.properties.consequences)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Instruksjoner:", fontWeight = FontWeight.Bold)
                Text(text = feature.properties.instruction)
            }
        },
        //button to close dialog
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Lukk")
            }
        }
    )
}

//Formats dateTimeString to more readable form
fun formatDateTime(dateTimeString: String?): String {
    return try {
        val parsedDate = ZonedDateTime.parse(dateTimeString)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        parsedDate.format(formatter)
    } catch (e: Exception) {
        "Ukjent tid" // in the case of a failing parsing
    }
}

//Parse color description to Color values
fun parseRiskMatrixColor(colorString: String?): Color {
    return when (colorString?.lowercase()) {
        "red" -> Color.Red
        "orange" -> Color(0xFFFFA500) // Orange color
        "yellow" -> Color.Yellow
        "green" -> Color.Green
        "blue" -> Color.Blue
        else -> Color.LightGray // Standard color if the value is unknown
    }
}
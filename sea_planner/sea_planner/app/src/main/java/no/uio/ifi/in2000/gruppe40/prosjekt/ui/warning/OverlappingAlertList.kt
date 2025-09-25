package no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Feature

@Composable
fun OverlappingAlertList(
    alerts: List<Feature>,          //List of overlapping alerts to display
    onDismiss: () -> Unit,          //Used when closing the list
    onSelect: (Feature) -> Unit     //used when selecting alert in list
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Overlappende varsler") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                alerts.forEach { alert ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onSelect(alert) },     //Select the alert on click
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Varsel: ${alert.properties.eventAwarenessName}, ${alert.properties.area}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        //button to close the list
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Lukk")
            }
        }
    )
}
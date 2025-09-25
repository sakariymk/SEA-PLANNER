package no.uio.ifi.in2000.gruppe40.prosjekt.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// checks to see if internet is turned off

fun isNetworkAvailable(context: Context): Boolean {
    val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectManager.activeNetwork ?: return false
    val capabilities = connectManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// in the event the app does not have internet connection, an alert is displayed
@Composable
fun NoInternetDialog(onClose: ()-> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ingen nettverk") },
        text = { Text("Ingen internettforbindelse oppdaget. Vennligst sjekk tilkoblingen") },
        confirmButton = {
            Button(onClick = onClose) {
                Text("Lukk")
            }
        }
    )
}
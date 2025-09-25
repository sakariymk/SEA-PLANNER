package no.uio.ifi.in2000.gruppe40.prosjekt.ui.theme


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import no.uio.ifi.in2000.gruppe40.prosjekt.R


@Composable
fun loadMapStyle(): MapStyleOptions? {
    // Reads the JSON filen
    val context = LocalContext.current
    return try {
        val json = context.resources.openRawResource(R.raw.stil2).bufferedReader().use {it.readText().trimIndent()}
        //Load the JSON style from Snazzy Maps
        MapStyleOptions(json)
    } catch (e: Exception) {
        Log.e("Mapstyle", "The style does not work...", e)
        null
    }
}
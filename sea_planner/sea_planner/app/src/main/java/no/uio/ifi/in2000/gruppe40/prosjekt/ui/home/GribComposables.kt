package no.uio.ifi.in2000.gruppe40.prosjekt.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import no.uio.ifi.in2000.gruppe40.prosjekt.R
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Current
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Waves
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Wind
import java.util.Calendar

// these composable functions adds markers given by the gribfiles, and modify them visually
@SuppressLint("DefaultLocale")
@Composable
fun WaterArrow(context: Context ,current: Current, todayDateAndTime: Calendar,latitude: Double, longitude: Double ) {
    val speed = current.getCurrentSpeed(todayDateAndTime,"DBSL:3", latitude, longitude)
    val rotation = current.getCurrentDirection(todayDateAndTime, "DBSL:3", latitude, longitude)

    Marker(
        state    = MarkerState(position = LatLng(latitude, longitude)),
        icon     = bitmapDescriptorFromVector(context, R.drawable.arrow),
        rotation = rotation.toFloat(),
        title = "Speed: " + String.format("%.2f", speed) + "m/s",
        flat = true
    )

}

@SuppressLint("DefaultLocale")
@Composable
fun WindArrow(context: Context ,wind: Wind, todayDateAndTime: Calendar,latitude: Double, longitude: Double ) {
    val speed = wind.getWindSpeed(todayDateAndTime,"TGL", latitude, longitude)
    val rotation = wind.getWindDirection(todayDateAndTime,"TGL", latitude, longitude)

    Marker(
        state    = MarkerState(position = LatLng(latitude, longitude)),
        icon     = bitmapDescriptorFromVector(context, R.drawable.arrow),
        rotation = rotation.toFloat(),
        title = "Speed: " + String.format("%.2f", speed) + "m/s",
        flat = true
    )

}

@Composable
fun WavesHeatMap(waves: Waves, todayDateAndTime: Calendar,latitude: Double, longitude: Double ) {
    val waveheight = waves.getWaveHeight(todayDateAndTime,"TGL", latitude, longitude)

    val size = 0.30
    val halfSize = size / 2

    val polygonPoints = listOf(
        LatLng(latitude - halfSize, longitude - halfSize), // Bottom-left
        LatLng(latitude - halfSize, longitude + halfSize), // Bottom-right
        LatLng(latitude + halfSize, longitude + halfSize), // Top-right
        LatLng(latitude + halfSize, longitude - halfSize), // Top-left
        LatLng(latitude - halfSize, longitude - halfSize)  // Closing
    )


    Polygon(
        points = polygonPoints,
        fillColor = getColorForValue(waveheight, 2, 0),
        strokeColor = Color.Transparent
    )

}

@Composable
fun DangerZone(latitude: Double, longitude: Double) {
    val size = 0.30
    val halfSize = size / 2

    val polygonPoints = listOf(
        LatLng(latitude - halfSize, longitude - halfSize), // Bottom-left
        LatLng(latitude - halfSize, longitude + halfSize), // Bottom-right
        LatLng(latitude + halfSize, longitude + halfSize), // Top-right
        LatLng(latitude + halfSize, longitude - halfSize), // Top-left
        LatLng(latitude - halfSize, longitude - halfSize)  // Closing
    )

    Polygon(
        points = polygonPoints,
        fillColor = Color(0x80FFA500),
        strokeColor = Color.Transparent
    )
}

@Composable
fun PrecipationHeatMap(wind: Wind, todayDateAndTime: Calendar,latitude: Double, longitude: Double ) {
    val totalprecipation = wind.getTotalPrecipitation(todayDateAndTime,"TGL", latitude, longitude)

    val size = 0.30
    val halfSize = size / 2

    val polygonPoints = listOf(
        LatLng(latitude - halfSize, longitude - halfSize), // Bottom-left
        LatLng(latitude - halfSize, longitude + halfSize), // Bottom-right
        LatLng(latitude + halfSize, longitude + halfSize), // Top-right
        LatLng(latitude + halfSize, longitude - halfSize), // Top-left
        LatLng(latitude - halfSize, longitude - halfSize)  // Closing
    )

    Polygon(
        points = polygonPoints,
        fillColor = getColorForValue(totalprecipation, 2, 0),
        strokeColor = Color.Transparent
    )
}

fun getColorForValue(value: Double, maxValue: Int, minValue: Int): Color {
    // Normalize to 0–1 range
    val normalized = ((value - minValue) / (maxValue - minValue)).coerceIn(0.0, 1.0)

    // Light blue: RGB(173, 216, 230)
    // Dark blue:  RGB(0, 0, 139)
    val red = (173 * (1 - normalized)).toInt()
    val green = (216 * (1 - normalized)).toInt()
    val blue = (230 * (1 - normalized) + 139 * normalized).toInt()

    return Color(160, red, green, blue)
}


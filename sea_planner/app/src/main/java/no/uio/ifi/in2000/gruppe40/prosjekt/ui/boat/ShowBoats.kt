package no.uio.ifi.in2000.gruppe40.prosjekt.ui.boat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.MarkerState
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import no.uio.ifi.in2000.gruppe40.prosjekt.R
import no.uio.ifi.in2000.gruppe40.prosjekt.data.boat.Boat
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.home.bitmapDescriptorFromVector

// a composable function that returns boats on a map as markers, and adds a function where upon click
// the user will get a window containing information about the boats such as velocity, coarse, last position, etc.
@Composable
fun ShowBoats(
    context: Context,
    boats: List<Boat>,
    onToggle: Boolean
) {
    boats.forEach { boat ->
        if (boat.latitude != 0.0 && boat.longitude != 0.0) {
            val iconRes = when (boat.shipType?.toInt()) {
                30      -> R.drawable.fishboat
                31      -> R.drawable.purpleboat
                36      -> R.drawable.orangeboat
                35, 55  -> R.drawable.greenboat
                in 60..66, in 70..99 -> R.drawable.whiteboat
                58      -> R.drawable.redboat
                3, 34   -> R.drawable.blackboat
                else    -> R.drawable.whiteboat
            }

            MarkerInfoWindowContent(
                state    = MarkerState(position = LatLng(boat.latitude, boat.longitude)),
                icon     = bitmapDescriptorFromVector(context, iconRes),
                rotation = boat.courseOverGround?.toFloat() ?: 0f,
                flat = true,
                anchor   = Offset(0.5f, 0.5f),
                onClick = {onToggle},
            ) {
                Column(modifier = Modifier
                    .padding(8.dp)) {
                    Text(text = boat.name ?: "Ukjent", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Fart: ${boat.speedOverGround ?: "?"} kn",  color = Color.Black)
                    Text("Kurs: ${boat.courseOverGround ?: "?"}°",  color = Color.Black)
                    Text("MMSI: ${boat.mmsi ?: "?"}",  color = Color.Black)
                    Text("Sist posisjon: ${boat.latitude.format(5)} / ${boat.longitude.format(5)}",  color = Color.Black)
                }
            }
        }
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

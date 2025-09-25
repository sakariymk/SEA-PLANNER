package no.uio.ifi.in2000.gruppe40.prosjekt.data.marker

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale



// returns a string, context, map-limit within a latitude and longitude (LatLng) object
// this LatLng object is used for the WeatherScreen

object GeocodingDataSource {
    suspend fun fetchLatLngFromPlace(place: String, context: Context, bounds: LatLngBounds): LatLng? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val adresser = geocoder.getFromLocationName(place, 1)
                if (!adresser.isNullOrEmpty()) {
                    val address = adresser[0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    // as long as the marker is within the bounds of the map, it will be marked
                    if (bounds.contains(latLng)) {
                        latLng
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("Geocoding", "Exception at geocoding: ${e.message}")
                null
            }
        }
    }
}
package no.uio.ifi.in2000.gruppe40.prosjekt.data.marker

import com.google.android.gms.maps.model.LatLng

// specifies the types of the marker. A marker should have an Id, Position, and a name
data class InteractableMarkers (
    val id: Int,
    val position: LatLng,
    var name: String
    )
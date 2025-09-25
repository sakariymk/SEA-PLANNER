package no.uio.ifi.in2000.gruppe40.prosjekt.data.marker

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// creates a repository which stores the markers created on the map, or removes them by the user's desire
//resets everytime the app restarts
 object MarkerRepository{

    private val _markers = MutableStateFlow<List<InteractableMarkers>>(emptyList())
    val markers: StateFlow<List<InteractableMarkers>> = _markers
    private var counter = 1

    fun addMarker(mark: InteractableMarkers) {
        _markers.value += mark
        counter++
    }

    fun removeMarker(mark: InteractableMarkers) {
        _markers.value -= mark
    }

    fun removeAllMarkers() {
        _markers.value = emptyList()
        counter = 1
    }
}
package no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.gruppe40.prosjekt.data.marker.GeocodingDataSource.fetchLatLngFromPlace
import no.uio.ifi.in2000.gruppe40.prosjekt.data.marker.InteractableMarkers
import no.uio.ifi.in2000.gruppe40.prosjekt.data.marker.MarkerRepository
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.AlertPolygon
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Feature
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.MetalertRepository
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.MetalertRetrofit
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.WeatherRetrofit
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.WeatherRepository
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.WeatherResponse
import no.uio.ifi.in2000.gruppe40.prosjekt.model.weather.WeatherForecast
import java.time.ZoneOffset
import java.time.ZonedDateTime

open class WeatherViewModel : ViewModel() {
    private val metalertRepository = MetalertRepository(MetalertRetrofit.api)
    private val weatherRepository = WeatherRepository(WeatherRetrofit.api)

    //Fetches weather
    private val _weatherList = MutableStateFlow<List<WeatherForecast>>(emptyList())
    val weatherList: StateFlow<List<WeatherForecast>> = _weatherList

    //days of the forecast
    private val _daysList = MutableStateFlow<List<WeatherForecast>>(emptyList())
    val daysList: StateFlow<List<WeatherForecast>> = _daysList.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    //positions that will be shown weather; Dropdownmenu
    private val _locationName = mutableStateOf("Din posisjon")

    //Error-message
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    //Permission to show the user's position
    private val _locationAccessDenied = mutableStateOf(false)
    val locationAccessDenied: State<Boolean> = _locationAccessDenied

    //For the search bar
    private val _initialZoomPosition = MutableStateFlow<LatLng?>(null)
    val initialZoomPosition: StateFlow<LatLng?> = _initialZoomPosition

    //limits the map to show only Norway
    val justNorway = LatLngBounds(
        LatLng(57.0, 4.0),  //Limit the South-West
        LatLng(71.5, 31.0) //Limit the North-East
    )

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog


    fun openDialog() {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun settZoomPosition(place: String, context: Context) {
        viewModelScope.launch {
            val result = fetchLatLngFromPlace(place, context, justNorway)
            _initialZoomPosition.value = result
        }

    }

    //Markers:
    //Fetches markers from MarkerRepository
    val theMarkersOfThemap: StateFlow<List<InteractableMarkers>> = MarkerRepository.markers


    fun addMarker(mark: InteractableMarkers) {
        MarkerRepository.addMarker(mark)
    }

    fun removeMarker(mark: InteractableMarkers) {
        MarkerRepository.removeMarker(mark)
    }

    fun removeAllMarkers() {
        MarkerRepository.removeAllMarkers()
    }

    fun fetchWeatherForUser(context: Context) {
        fetchUserLocation(context) { latitude, longitude ->
            fetchWeatherFromUser(latitude, longitude)
        }
    }

    //fetches the
    private fun createSummary(list: List<WeatherForecast>): List<WeatherForecast> {
        return list.groupBy { it.date.substring(0, 10) }
            .mapNotNull { (_, forecasts) ->
                val selected = forecasts.firstOrNull {
                    it.date.contains("T12:00")
                }
                    ?: forecasts.firstOrNull()
                selected
            }
    }


    //Fetches weather from the user's position
    private fun fetchWeatherFromUser(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _locationName.value = "User location"
            _errorMessage.value = null

            val result = weatherRepository.fetchWeather(latitude, longitude)

            if (result.isSuccess) {
                val response = result.getOrThrow()
                val filteredForecast = filterForecast(response)
                _weatherList.value = filteredForecast
                _daysList.value = createSummary(filteredForecast)
            } else {
                _errorMessage.value =
                    "Feil ved å hente vær: ${result.exceptionOrNull()?.message}"
                Log.e(
                    "WeatherVM",
                    "Feil ved å hente data: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    //fetches weather from a specific place; also used if user disabled tracking their location
    fun fetchWeatherByLocation(latitude: Double, longitude: Double, placeName: String = "Unknown") {
        viewModelScope.launch {
            _locationName.value = placeName
            _errorMessage.value = null

            val result = weatherRepository.fetchWeather(latitude, longitude)

            if (result.isSuccess) {
                val response = result.getOrThrow()
                val filteredForecast = filterForecast(response)
                _weatherList.value = filteredForecast
                _daysList.value = createSummary(filteredForecast)
            } else {
                _errorMessage.value =
                    "Feil ved å hente vær: ${result.exceptionOrNull()?.message}"
                Log.e(
                    "WeatherVM",
                    "Feil ved å hente data: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    private fun fetchUserLocation(context: Context, onLocationReady: (Double, Double) -> Unit) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        onLocationReady(location.latitude, location.longitude)
                    } else {
                        Log.e("WeatherVM", "No locations are available...")
                    }
                }
                .addOnFailureListener {
                    _errorMessage.value =
                        "Fikk ikke tilgang til posisjon... Vennligst tillatt tilgang til bruker location for å vise vær for din posisjon"
                    Log.e("WeatherVM", "Exception at fetching location ${it.message}")
                    _locationAccessDenied.value = true
                }
        } catch (e: SecurityException) {
            Log.e("WeatherVM", "Missing permission: ${e.message}")
        }
    }

    private fun filterForecast(response: WeatherResponse): List<WeatherForecast> {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val withinThreeDays = now.plusDays(3)

        return response.properties.timeseries
            .filter {
                val forecastTime = ZonedDateTime.parse(it.time)
                forecastTime.isAfter(now.plusHours(0)) && forecastTime.isBefore(withinThreeDays)
            }
            .map {
                WeatherForecast(
                    date = it.time,
                    temperature = it.data.instant.details.air_temperature,
                    symbolCode = it.data.next_1_hours?.summary?.symbol_code ?: "clearsky_day",
                    windSpeed = it.data.instant.details.wind_speed,
                    windDirection = it.data.instant.details.wind_from_direction
                )
            }
    }

    var selectedOption by mutableStateOf("Værforhold")
        private set

    fun updateSelectedOption(option: String) {
        selectedOption = option
    }

    var alertPolygons by mutableStateOf<List<AlertPolygon>>(emptyList())

    var selectedAlerts by mutableStateOf<List<Feature>>(emptyList())
        private set

    //fetches alerts data from repository and handles polygons
    open fun fetchMetalerts() {
        viewModelScope.launch {
            try {
                val result = metalertRepository.fetchWarning("marine")
                _errorMessage.value = null
                if (result.isSuccess) {
                    val response = result.getOrThrow()

                    // Handles parsing of polygons from features
                    alertPolygons = response.features.mapNotNull { feature ->
                        val coords = mutableListOf<LatLng>()

                        when (feature.geometry.type) {
                            "Polygon" -> {
                                // handles polygon as a list with coordinates [ [ [lon, lat], ... ] ]
                                val coordinates =
                                    feature.geometry.coordinates as? List<List<List<Double>>>
                                coordinates?.firstOrNull()?.forEach { coord ->
                                    if (coord.size >= 2) {
                                        val lon = coord[0]
                                        val lat = coord[1]
                                        coords.add(LatLng(lat, lon))
                                    }
                                }
                            }

                            "MultiPolygon" -> {
                                // Handles MultiPolygon as a list  with multiple polygons [ [ [ [lon, lat], ... ] ] ]
                                val coordinates =
                                    feature.geometry.coordinates as? List<List<List<List<Double>>>>
                                coordinates?.firstOrNull()?.firstOrNull()?.forEach { coord ->
                                    if (coord.size >= 2) {
                                        val lon = coord[0]
                                        val lat = coord[1]
                                        coords.add(LatLng(lat, lon))
                                    }
                                }
                            }

                            else -> return@mapNotNull null // Unknown type
                        }

                        // creates an AlertPolygon if we have coordinates
                        if (coords.isNotEmpty()) {
                            AlertPolygon(feature, coords)
                        } else null
                    }
                } else {
                    _errorMessage.value = "Feil ved henting av farevarsler: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("WeatherVM", "Feil ved henting av farevarsler: ${e.message}")
            }
        }

    }

    //updates selectedAlerts after user interaction
    fun updateSelectedAlerts(alerts: List<Feature>) {
        selectedAlerts = alerts
    }
}
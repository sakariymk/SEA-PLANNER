package no.uio.ifi.in2000.gruppe40.prosjekt.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.gruppe40.prosjekt.R
import no.uio.ifi.in2000.gruppe40.prosjekt.data.marker.InteractableMarkers
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Feature
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Current
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Waves
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Wind
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.boat.BoatViewModel
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.boat.ShowBoats
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.OverlappingAlertList
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.WarningDetailDialog
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.findOverlappingAlerts
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.isOverlap
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.parseRiskMatrixColor
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.theme.loadMapStyle
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather.WeatherViewModel
import java.util.Calendar
import kotlin.math.roundToInt


//modifies icons on map such as boats and wind/current vectors
fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    val width = 50
    val height = 50

    vectorDrawable.setBounds(0, 0, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


@Composable
fun MapView(viewModel: WeatherViewModel = viewModel(), homeScreenViewModel: HomeScreenViewModel) {

    val alertPolygons = viewModel.alertPolygons
    val selectedAlerts = viewModel.selectedAlerts
    var selectedAlert by remember { mutableStateOf<Feature?>(null) }
    var showOverlappingAlertsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val boatViewModel: BoatViewModel = viewModel()
    val gribRepository by homeScreenViewModel.getGribRepository().collectAsState()
    val osloGribObjectList = gribRepository.osloList
    val westNorGribObjectList = gribRepository.westNorList
    val searchMode by homeScreenViewModel.searchMode.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var boatMenuExpanded by  remember { mutableStateOf(false) }

    // Boats:
    val allBoats        by boatViewModel.allBoats.collectAsState()
    val fishingBoats    by boatViewModel.fishingBoats.collectAsState()
    val towingBoats     by boatViewModel.towingBoats.collectAsState()
    val sailingBoats    by boatViewModel.sailingBoats.collectAsState()
    val enforcementBoats by boatViewModel.enforcementBoats.collectAsState()
    val commercialBoats by boatViewModel.commercialBoats.collectAsState()
    val medicalBoats    by boatViewModel.medicalBoats.collectAsState()
    val divingBoats     by boatViewModel.divingBoats.collectAsState()


    val initialZoomPosition by viewModel.initialZoomPosition.collectAsState()

    //load the MapStyle
    val mapStyle = loadMapStyle()

    //User on the map:
    val userLoc = remember {LocationServices.getFusedLocationProviderClient(context)}

    // Toggle-Button for the markers
    var markerMode by remember { mutableStateOf(false) }
    val markers by viewModel.theMarkersOfThemap.collectAsState()
    var selectedMarker by remember { mutableStateOf<InteractableMarkers?>(null) }

    val keyController = LocalSoftwareKeyboardController.current

    var mapProperties by remember { mutableStateOf(
        MapProperties(
            isMyLocationEnabled = false,
            // limits the map to show only Norway
            latLngBoundsForCameraTarget = viewModel.justNorway,
            minZoomPreference = 6f,
            maxZoomPreference = 15f,
            mapStyleOptions = mapStyle
        )) }

    // starts the app on Bergen, despite user location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(60.405892, 5.273913), 12f)
    }


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        }
    }

    // as soon as the app is running, the app checks to see if user location permission is on
    // if not it will ask the user to activate the permission to use user location
    LaunchedEffect(Unit, alertPolygons) {
        viewModel.fetchMetalerts()

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val overlappingAlerts = findOverlappingAlerts(alertPolygons)
        if (overlappingAlerts.size > 1) {
            showOverlappingAlertsDialog = true
        }
    }

    //fetches the boats
    LaunchedEffect(Unit) {
        boatViewModel.fetchBoats()
    }

    //Searchbar result will move the map to the desired area
    LaunchedEffect(initialZoomPosition) {
        initialZoomPosition?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 12f),
                durationMs = 1000
            )
        }
    }

    // options for the boat dropdownmenu
    var showAll          by rememberSaveable { mutableStateOf(false) }
    var showFishing      by rememberSaveable { mutableStateOf(false) }
    var showTowing       by rememberSaveable { mutableStateOf(false) }
    var showSailing      by rememberSaveable { mutableStateOf(false) }
    var showEnforcement  by rememberSaveable { mutableStateOf(false) }
    var showCommercial   by rememberSaveable { mutableStateOf(false) }
    var showMedical      by rememberSaveable { mutableStateOf(false) }
    var showDiving       by rememberSaveable { mutableStateOf(false) }

    // adds a multiple choice dropdownmenu
    val boatsToShow = when {
        showAll -> allBoats
        else -> buildList {
            if(showFishing) addAll(fishingBoats)
            if(showTowing) addAll(towingBoats)
            if(showSailing) addAll(sailingBoats)
            if(showEnforcement) addAll(enforcementBoats)
            if(showCommercial) addAll(commercialBoats)
            if(showMedical) addAll(medicalBoats)
            if(showDiving) addAll(divingBoats)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            GoogleMap(
                properties = mapProperties,
                cameraPositionState = cameraPositionState,
                //removes the default Google Maps buttons 1: recenter, 2: zoom buttons
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false
                ),
                onMapClick = { latLng ->
                    keyController?.hide()
                    selectedMarker = null
                    if (markerMode) {

                        val newMarker = InteractableMarkers(
                            id = 0,
                            position = latLng,
                            name = "Marker_${markers.size + 1}"
                        )
                        viewModel.addMarker(newMarker)

                    }
                }
            ) {
                markers.forEach { marker ->
                    Marker(
                        title = marker.name,
                        state = MarkerState(position = marker.position),
                        onClick = {
                            if (!markerMode) {
                                selectedMarker = marker
                                viewModel.fetchWeatherByLocation(
                                    marker.position.latitude,
                                    marker.position.longitude
                                )
                            }
                            true
                        }
                    )
                }

                val todayDateAndTime: Calendar = Calendar.getInstance()
                var coordinateStep = 0.30

                val higherGribValue = homeScreenViewModel.upperValue.floatValue

                val osloWind = osloGribObjectList.getOrNull(1) as? Wind
                val westNorWind = westNorGribObjectList.getOrNull(1) as? Wind


                // Draw heatmaps for rain and wave height
                // these four following if-statements shows the wind, precipitation, currents, wave-height on the map
                if (viewModel.selectedOption == "nedbør" && osloWind != null && westNorWind != null) {
                    homeScreenViewModel.maxSliderValue.floatValue = 2f

                    var lat = 58.9

                    while (lat <= 60.0) {
                        var lon = 9.8
                        while (lon <= 11.2) {
                            val precipation =
                                osloWind.getTotalPrecipitation(todayDateAndTime, "TGL", lat, lon)
                            if (precipation > 0 && precipation < 100) {
                                PrecipationHeatMap(osloWind, todayDateAndTime, lat, lon)
                                if (precipation > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }

                    lat = 57.9
                    while (lat <= 63.0) {
                        var lon = 4.45
                        while (lon <= 7.0) {
                            val precipation =
                                westNorWind.getTotalPrecipitation(todayDateAndTime, "TGL", lat, lon)
                            if (precipation > 0 && precipation < 100) {
                                PrecipationHeatMap(westNorWind, todayDateAndTime, lat, lon)
                                if (precipation > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }
                }

                if (viewModel.selectedOption == "bølgehøyde") {
                    val osloWaves = osloGribObjectList[2] as Waves
                    val westNorWaves = westNorGribObjectList[2] as Waves

                    homeScreenViewModel.maxSliderValue.floatValue = 2f

                    var lat = 58.9

                    while (lat <= 60.0) {
                        var lon = 9.8
                        while (lon <= 11.2) {
                            val waves = osloWaves.getWaveHeight(todayDateAndTime, "TGL", lat, lon)
                            if (waves > 0 && waves < 100) {
                                WavesHeatMap(osloWaves, todayDateAndTime, lat, lon)
                                if (waves > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }

                    lat = 57.9
                    while (lat <= 63.0) {
                        var lon = 4.45
                        while (lon <= 7.0) {
                            val waves =
                                westNorWaves.getWaveHeight(todayDateAndTime, "TGL", lat, lon)
                            if (waves > 0 && waves < 100) {
                                WavesHeatMap(westNorWaves, todayDateAndTime, lat, lon)
                                if (waves > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }

                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }
                }


                // Draw arrows for wind and current
                if (viewModel.selectedOption == "vind") {
                    val osloWindObject = osloGribObjectList[1] as Wind
                    val westNorWindObject = westNorGribObjectList[1] as Wind
                    coordinateStep = 0.30
                    homeScreenViewModel.maxSliderValue.floatValue = 60f

                    var lat = 58.9

                    while (lat <= 60.0) {
                        var lon = 9.8
                        while (lon <= 11.2) {
                            val wind = osloWindObject.getWindSpeed(todayDateAndTime, "TGL", lat, lon)
                            if (wind > 0 && wind < 30) {
                                WindArrow(context, osloWindObject, todayDateAndTime, lat, lon)
                                if (wind > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }


                    lat = 57.9
                    while (lat <= 63.0) {
                        var lon = 4.45
                        while (lon <= 7.0) {
                            val wind = westNorWindObject.getWindSpeed(todayDateAndTime, "TGL", lat, lon)
                            if (wind > 0 && wind < 30) {
                                WindArrow(context, westNorWindObject, todayDateAndTime, lat, lon)
                                if (wind > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }
                }

                if (viewModel.selectedOption == "strøm") {
                    val osloCurrent = osloGribObjectList[0] as Current
                    val westNorCurrent = westNorGribObjectList[0] as Current

                    homeScreenViewModel.maxSliderValue.floatValue = 2f

                    var lat = 58.9
                    coordinateStep = 0.30


                    while (lat <= 60.0) {
                        var lon = 9.8
                        while (lon <= 11.2) {
                            val speed =
                                osloCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", lat, lon)
                            if (speed > 0 && speed < 100) {
                                WaterArrow(context, osloCurrent, todayDateAndTime, lat, lon)
                                if (speed > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }

                    lat = 57.9
                    while (lat <= 63.0) {
                        var lon = 4.45
                        while (lon <= 7.0) {
                            val speed =
                                westNorCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", lat, lon)
                            if (speed > -2 && speed < 15) {
                                WaterArrow(context, westNorCurrent, todayDateAndTime, lat, lon)
                                if (speed > higherGribValue) {
                                    DangerZone(lat, lon)
                                }
                            }
                            lon += coordinateStep
                        }
                        lat += coordinateStep
                    }
                }

                //Iterate over each alert polygon and draw on the map
                alertPolygons.forEach { alertPolygon ->
                    val fillColor =
                        parseRiskMatrixColor(alertPolygon.feature.properties.riskMatrixColor)
                    //Draws interactive polygon with correct riskColor
                    Polygon(
                        points = alertPolygon.coordinates,
                        fillColor = fillColor.copy(alpha = 0.4f),   // almost transparent color
                        strokeColor = fillColor,                    // Border color
                        strokeWidth = 3f,
                        clickable = !markerMode,
                        onClick = {
                            //find potential overlapping polygons
                            val overlapping = alertPolygons.filter { other ->
                                other.feature != alertPolygon.feature &&
                                        isOverlap(alertPolygon.coordinates, other.coordinates)
                            }.map { it.feature }

                            //If overlapping polygons exist, set overlapping to true and define selected alert to null
                            if (overlapping.isNotEmpty()) {
                                viewModel.updateSelectedAlerts(overlapping + alertPolygon.feature)
                                selectedAlert = null
                                showOverlappingAlertsDialog = true
                            } else {
                                //If no overlapping polygons, overlapping turns false and selected alert becomes the one clicked
                                viewModel.updateSelectedAlerts(listOf(alertPolygon.feature))
                                selectedAlert = alertPolygon.feature
                                showOverlappingAlertsDialog = false
                            }
                        }
                    )
                }
                //shows overlapping list if there are overlapping ones
                if (showOverlappingAlertsDialog && selectedAlerts.size > 1) {
                    OverlappingAlertList(
                        alerts = selectedAlerts,
                        onDismiss = {
                            viewModel.updateSelectedAlerts(emptyList())
                            showOverlappingAlertsDialog = false  // closes the dialog
                        },
                        onSelect = { alert ->
                            selectedAlert = alert
                        }
                    )
                }

                //show detaildialog for selected alert
                if (selectedAlert != null) {
                    WarningDetailDialog(
                        feature = selectedAlert!!,
                        onDismiss = { selectedAlert = null }  // if the user closes the dialog
                    )
                }

                ShowBoats(context = context, boats = boatsToShow, onToggle = markerMode)
            }

            //buttons below:

            if (!isLandscape) {
                // The buttons for searching + markers + removing markers
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.padding(top = 130.dp),
                    contentAlignment = Alignment.TopStart
                ) {

                    Column {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                        Button(onClick = {
                            homeScreenViewModel.toggleSearchMode()
                        }) {
                            if (searchMode) {
                                Image(
                                    painter = painterResource(R.drawable.searchicon2),
                                    contentDescription = "Search icon",
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.searchicon),
                                    contentDescription = "Search icon",
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        //Toggle markers
                        ToggleButtonForMarker(
                            modifier = Modifier.size(40.dp),
                            onToggle = {
                                markerMode = !markerMode
                                selectedMarker = null
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        if (markers.isEmpty()) {
                            Button(
                                onClick = {

                                },
                                modifier = Modifier
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.bin),
                                    contentDescription = "Kan ikke fjerne noen markers",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.removeAllMarkers()
                                },
                                modifier = Modifier
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.bin2),
                                    contentDescription = "Fjerner alle markers",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }

                // --- info + Recenter + Shows boats + Dropdown ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.padding(top = 130.dp),
                    contentAlignment = Alignment.TopEnd
                ) {

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Box(
                            modifier = Modifier
                                //.fillMaxWidth()
                                .height(120.dp)
                        )
                        ShowHelp(modifier = Modifier.size(50.dp))
                        //Button that recenters the map to the user
                        FindUser(modifier = Modifier.size(40.dp),
                            onClick = {
                                userLoc.lastLocation.addOnSuccessListener { position ->
                                    if (position != null) {
                                        val userLatLng =
                                            LatLng(position.latitude, position.longitude)
                                        cameraPositionState.move(
                                            CameraUpdateFactory.newLatLng(userLatLng)
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = { boatMenuExpanded = !boatMenuExpanded },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        ) {

                            Image(
                                painter = painterResource(R.drawable.sailing_boat),
                                contentDescription = "Show boat",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    // Custom made zoom buttons
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        CustomMadeZoomButtons(cameraPositionState)
                    }
                }
            } else {
                // The buttons for searching + markers + removing markers
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {

                    Column {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                        Button(onClick = {
                            homeScreenViewModel.toggleSearchMode()
                        }) {
                            if (searchMode) {
                                Image(
                                    painter = painterResource(R.drawable.searchicon2),
                                    contentDescription = "Search icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.searchicon),
                                    contentDescription = "Search icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        //Toggle markers


                        ToggleButtonForMarker(
                            modifier = Modifier.size(30.dp),
                            onToggle = {
                                markerMode = !markerMode
                                selectedMarker = null
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        if (markers.isEmpty()) {
                            Button(
                                onClick = {

                                },
                                modifier = Modifier
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.bin),
                                    contentDescription = "Kan ikke fjerne noen markers",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.removeAllMarkers()
                                },
                                modifier = Modifier
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.bin2),
                                    contentDescription = "Fjerner alle markers",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }

                // --- info + Recenter + Shows boats + Dropdown ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Box(
                            modifier = Modifier
                                .height(100.dp)
                        )
                        ShowHelp(modifier = Modifier.size(40.dp))

                        //Button that recenters the map to the user
                        FindUser(modifier = Modifier.size(30.dp),
                            onClick = {
                                userLoc.lastLocation.addOnSuccessListener { position ->
                                    if (position != null) {
                                        val userLatLng =
                                            LatLng(position.latitude, position.longitude)
                                        cameraPositionState.move(
                                            CameraUpdateFactory.newLatLng(userLatLng)
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = { boatMenuExpanded = !boatMenuExpanded },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        ) {

                            Image(
                                painter = painterResource(R.drawable.sailing_boat),
                                contentDescription = "Show boat",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }

            DropdownMenu(
                expanded = boatMenuExpanded,
                onDismissRequest = { boatMenuExpanded = false },
                modifier = Modifier
                    .width(300.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {

                FilterRow("Vis alle båter", imageResId = R.drawable.towingboaticon, showAll) {
                    showAll = it
                    if (it) {
                        showFishing = false; showTowing = false
                        showSailing = false; showEnforcement = false
                        showCommercial = false; showMedical = false
                        showDiving = false
                    }
                }

                HorizontalDivider()
                FilterRow("Fiskebåter", imageResId = R.drawable.fishboat, showFishing) {
                    showFishing = it; if (it) showAll = false
                }

                FilterRow("Slepebåter", imageResId = R.drawable.purpleboat, showTowing) {
                    showTowing = it; if (it) showAll = false
                }
                FilterRow("Seilbåter", imageResId = R.drawable.orangeboat, showSailing) {
                    showSailing = it; if (it) showAll = false
                }
                FilterRow("Patrulje/militære", imageResId = R.drawable.greenboat, showEnforcement) {
                    showEnforcement = it; if (it) showAll = false
                }
                FilterRow("Kommersielle", imageResId = R.drawable.whiteboat, showCommercial) {
                    showCommercial = it; if (it) showAll = false
                }
                FilterRow("Medisinske", imageResId = R.drawable.redboat, showMedical) {
                    showMedical = it; if (it) showAll = false
                }
                FilterRow("Dykkebåter", imageResId = R.drawable.blackboat, showDiving) {
                    showDiving = it; if (it) showAll = false
                }
            }



            //selecting an existing marker; grants user option to inspect a marker,
            // renaming it, or deleting it

            /*

            This block of code works on an emulator, but not on a physical device
            Could be improved in the future
            selectedMarker?.let { thisMarker ->
                if (!markerMode) {
                    var newName by remember { mutableStateOf(thisMarker.name) }
                    var offsetX by remember { mutableFloatStateOf(0f) }
                    var offsetY by remember { mutableFloatStateOf(0f) }
                    var showTextField by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .offset { (IntOffset(offsetX.roundToInt(), offsetY.roundToInt())) }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (showTextField) {
                                    OutlinedTextField(
                                        value = newName,
                                        onValueChange = { newName = it },
                                        label = { Text("Navn") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                thisMarker.name = newName
                                                keyController?.hide()
                                                showTextField = false
                                            }
                                        )
                                    )
                                } else {
                                    Text(text = thisMarker.name,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            showTextField = true
                                        })
                                }


                                HorizontalDivider()
                                Text("Lat: ${roundLatLng(thisMarker.position.latitude)}")
                                Text("Lng: ${roundLatLng(thisMarker.position.longitude)}")

                                Spacer(modifier = Modifier.height(8.dp))

                                Row {
                                    CopyValuesFromButton("${thisMarker.position.latitude}, ${thisMarker.position.longitude}")
                                    Button(onClick = {
                                        viewModel.removeMarker(thisMarker)
                                        selectedMarker = null
                                    }) {
                                        Image(
                                            painter = painterResource(R.drawable.bin2),
                                            contentDescription = "remove the marker",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Button(onClick = { selectedMarker = null }) {
                                        Image(
                                            painter = painterResource(R.drawable.close),
                                            contentDescription = "close the card",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }*/
            //}
        }

        }
    }

// toggle on/off function for the markers
@Composable
fun ToggleButtonForMarker(
    modifier: Modifier,
    onToggle: (Boolean) -> Unit,
) {
    var isAddMarkerMode by remember { mutableStateOf(false) }

    Button(
        onClick = {
            isAddMarkerMode = !isAddMarkerMode
            onToggle(isAddMarkerMode)
                  },
    ) {

        if (isAddMarkerMode) {
            Image(
                painter = painterResource(id = R.drawable.mapmarker),
                contentDescription = "Can add a marker",
                modifier = modifier
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.mapmarkerred),
                contentDescription = "Can NOT add a marker",
                modifier = modifier
            )
        }
    }
}

@Composable
fun FindUser(
    onClick: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
    ) {
        Image(
            painter = painterResource(R.drawable.location),
            contentDescription = "Finner bruker på kartet",
            modifier = modifier
        )
    }

}

@Composable
fun CopyValuesFromButton(text: String) {
    val clipboardManager = LocalClipboardManager.current

    Button(onClick = {
        clipboardManager.setText(AnnotatedString(text))
    }) {
        Image(
            painter = painterResource(R.drawable.copy),
            contentDescription = "copy the values",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CustomMadeZoomButtons(cameraPositionState: CameraPositionState) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(16.dp)

    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    val currentZoom = cameraPositionState.position.zoom
                    cameraPositionState.animate(CameraUpdateFactory.zoomTo(currentZoom + 1))
                }

            }
        ) {
            Text("+")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    val currentZoom = cameraPositionState.position.zoom
                    cameraPositionState.animate(CameraUpdateFactory.zoomTo(currentZoom -1))
                }
            }
        ) {
            Text("-")
        }
    }
}

@Composable
fun ShowHelp(
    modifier: Modifier
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    val scroller = rememberScrollState()
    Button (
        onClick = {
            showHelpDialog = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Unspecified
        )
    ) {
            Image(
                painter = painterResource(R.drawable.info),
                contentDescription = "Help",
                modifier = modifier
                    .size(60.dp)
                    .clickable {
                        showHelpDialog = true
                    }
            )


        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = {showHelpDialog = false},
                confirmButton = {
                    Button(
                        onClick = {showHelpDialog = false}
                    ) {
                        Text("Lukk")
                    }
                },
                text = {
                    Column(
                        Modifier.verticalScroll(scroller)
                    ) {
                        Row {
                            AccessableImage(R.drawable.searchicon, "Searchbar")
                            Text("Aktiverer søkefeltet")
                        }
                        HorizontalDivider()
                        Row {
                            AccessableImage(R.drawable.mapmarkerred, "Markermode")
                            Text("Aktiverer markør modus")
                        }
                        HorizontalDivider()
                        Row {
                            AccessableImage(R.drawable.bin, "bin")
                            Text("Fjerner alle markører")
                        }
                        HorizontalDivider()
                        Row {
                            AccessableImage(R.drawable.location, "location")
                            Text("Flytter kartet mot bruker")
                        }
                        HorizontalDivider()
                        Row {
                            AccessableImage(R.drawable.sailing_boat, "boat")
                            Text("Viser liste av ulike båter")
                        }
                        HorizontalDivider()
                    }

                }
            )
        }
    }
}

@Composable
fun FilterRow(
    label: String,
    imageResId: Int,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,

) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = null)
                Spacer(Modifier.width(8.dp))
                Text(label, color = Color.Black)
                Image(
                    painter = painterResource(id =imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        onClick = { onToggle(!checked) }
    )
}

@SuppressLint("DefaultLocale")
fun roundLatLng(latLng: Double): Double {
    return String.format("%.4f", latLng).toDouble()
}

@Composable
fun AccessableImage(imageResId: Int, contentDesc: String?) {
    val fontScale = LocalDensity.current.fontScale
    val imageSize = 20.dp * fontScale

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDesc,
        modifier = Modifier.size(imageSize)
    )
}
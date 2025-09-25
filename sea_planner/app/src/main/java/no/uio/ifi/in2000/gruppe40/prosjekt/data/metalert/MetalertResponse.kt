package no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert

import com.google.android.gms.maps.model.LatLng

// Response from the Metalert API containing a list of features
data class MetalertResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: AlertProperties, //Metadata and details about the alert
    val geometry: Geometry           //Geospatial data
)

//Pairs a Feature with its coordinates
data class AlertPolygon(
    val feature: Feature,
    val coordinates: List<LatLng>
)


data class Geometry(
    val coordinates: Any,
    val type: String
)

//Descriptions and categories for an alert
data class AlertProperties(
    val title: String,
    val description: String,
    val event: String,
    val eventEndingTime: String,
    val severity: String,
    val riskMatrixColor: String,
    val consequences: String,
    val certainty: String,
    val eventAwarenessName: String,
    val area: String,
    val instruction: String
)


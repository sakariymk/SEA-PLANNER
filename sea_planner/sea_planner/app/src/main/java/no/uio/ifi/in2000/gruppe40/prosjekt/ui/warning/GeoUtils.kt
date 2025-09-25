package no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning

import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.AlertPolygon
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Feature
import no.uio.ifi.in2000.gruppe40.prosjekt.model.warning.BoundingBox


// helps with displaying warnings on map
fun List<LatLng>.boundingBox(): BoundingBox {
    val lats = this.map { it.latitude }
    val lngs = this.map { it.longitude }
    return BoundingBox(
        left = lngs.minOrNull() ?: 0.0,
        right = lngs.maxOrNull() ?: 0.0,
        top = lats.maxOrNull() ?: 0.0,
        bottom = lats.minOrNull() ?: 0.0
    )
}

//checks if two warnings overlap
fun isOverlap(coordsA: List<LatLng>, coordsB: List<LatLng>): Boolean {
    val boxA = coordsA.boundingBox()
    val boxB = coordsB.boundingBox()

    return boxA.left < boxB.right &&
            boxA.right > boxB.left &&
            boxA.top > boxB.bottom &&
            boxA.bottom < boxB.top
}

//checks all existing warnings for overlap
fun findOverlappingAlerts(alertPolygons: List<AlertPolygon>): List<Feature> {
    val overlapping = mutableListOf<Feature>()
    for (i in alertPolygons.indices) {
        for (j in i + 1 until alertPolygons.size) {
            val alertA = alertPolygons[i]
            val alertB = alertPolygons[j]
            if (isOverlap(alertA.coordinates, alertB.coordinates)) {
                if (alertA.feature !in overlapping) overlapping.add(alertA.feature)
                if (alertB.feature !in overlapping) overlapping.add(alertB.feature)
            }
        }
    }
    return overlapping
}
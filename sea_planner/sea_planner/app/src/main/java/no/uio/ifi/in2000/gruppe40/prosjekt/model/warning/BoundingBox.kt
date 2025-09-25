package no.uio.ifi.in2000.gruppe40.prosjekt.model.warning

//Represents geographical bounding box using four coordinates
//Used to show warnings on the map
data class BoundingBox(val left: Double, val right: Double, val top: Double, val bottom: Double)

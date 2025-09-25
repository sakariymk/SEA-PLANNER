package no.uio.ifi.in2000.gruppe40.prosjekt.data.boat

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Boat(
    val courseOverGround: Double?,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val rateOfTurn: Float?,
    val shipType: Float?,
    val speedOverGround: Double?,
    val trueHeading: Float?,
    val navigationalStatus: Float?,
    val mmsi: Long?,
    val msgtime: String?
) {
    override fun toString(): String {
        return buildString {
            append("Boat:\n")
            append("  courseOverGround: ${courseOverGround ?: "N/A"}\n")
            append("  latitude: $latitude\n")
            append("  longitude: $longitude\n")
            append("  name: ${name ?: "N/A"}\n")
            append("  rateOfTurn: ${rateOfTurn ?: "N/A"}\n")
            append("  shipType: ${shipType ?: "N/A"}\n")
            append("  speedOverGround: ${speedOverGround ?: "N/A"}\n")
            append("  trueHeading: ${trueHeading ?: "N/A"}\n")
            append("  navigationalStatus: ${navigationalStatus ?: "N/A"}\n")
            append("  mmsi: ${mmsi ?: "N/A"}\n")
            append("  msgtime: ${msgtime ?: "N/A"}\n")
        }
    }
}

@Serializable
data class BearerKey(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val scope: String
)

// fetches data from AIS barentswatch API
suspend fun getBoatAPI(): List<Boat> {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    //sends a request to create an access token which returns the access token
    val keyResponse: HttpResponse = client.submitForm(
        url = "https://id.barentswatch.no/connect/token",
        formParameters = parameters {
            append("client_id", "sakka1609@gmail.com:API")
            append("client_secret", "Gruppe40IN2000")
            append("scope", "ais")
            append("grant_type", "client_credentials")
        }
    )
    // saves the access token
    val bearerKey: BearerKey = keyResponse.body()

    //uses the access token to saves a list of boats
    val boatResponse = client.get("https://live.ais.barentswatch.no/v1/latest/combined") {
        headers.append(HttpHeaders.Authorization, "Bearer ${bearerKey.access_token}")
    }

    //puts the list here
    val boats: List<Boat> = boatResponse.body()

    //filters the boats to show only the boats located on the western shores of Norway
    val westCoastBoats = boats.filter { boat ->
        boat.latitude in 59.3..61.6 && boat.longitude in 3.5..6.5
    }

    return westCoastBoats
}


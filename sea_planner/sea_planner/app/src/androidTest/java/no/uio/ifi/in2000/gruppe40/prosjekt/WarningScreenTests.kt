package no.uio.ifi.in2000.gruppe40.prosjekt

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng

import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.AlertPolygon
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Feature
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.home.MapView
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.WarningCard
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather.WeatherViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.mockk
import io.mockk.every
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import no.uio.ifi.in2000.gruppe40.prosjekt.data.marker.InteractableMarkers
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.AlertProperties
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.Geometry
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.WarningDetailDialog
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.WarningScreen

@RunWith(AndroidJUnit4::class)
class WarningScreenTests {

    //provides Compose testing utilities
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    //checks if the loading text is shown when waiting for the Metalerts data
    @Test
    fun warningScreen_showsLoadingText() {
        composeTestRule.setContent {
            WarningScreen()
        }

        composeTestRule.onNodeWithText("Henter data...").assertExists()
    }

    //checks if the detail dialog is shown when clicking on an alert
    @Test
    fun warningCard_opensDetailDialog_onClick() = runTest {
        val testFeature = Feature(
            properties = AlertProperties(
                title = "Testvarsel",
                description = "En beskrivelse av testvarselet",
                event = "Storm",
                eventEndingTime = "2025-05-06T15:00:00Z",
                severity = "Moderate",
                riskMatrixColor = "Yellow",
                consequences = "Skader på eiendom, kraftig regn",
                certainty = "High",
                eventAwarenessName = "Stormvarsel",
                area = "Hordaland",
                instruction = "Hold deg innendørs og vær forsiktig med vindskeier."
            ),
            geometry = Geometry(
                coordinates = listOf(LatLng(60.0, 5.0), LatLng(61.0, 6.0)),
                type = "Polygon"
            )
        )

        composeTestRule.setContent {
            var selectedNotification by remember { mutableStateOf<Feature?>(null) }

            WarningCard (
                feature = testFeature,
                eventAwarenessName = "Flomfare",
                area = "Oslo",
                time = "2025-05-06T12:00:00Z",
                onClick = { selectedNotification = testFeature }
            )

            selectedNotification?.let {
                WarningDetailDialog(it) { selectedNotification = null }
            }
        }

        composeTestRule.onNodeWithText("Flomfare, Oslo").performClick()
        composeTestRule.onNodeWithText("Instruksjoner:").assertExists()
    }
}


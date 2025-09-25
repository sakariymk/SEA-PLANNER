package no.uio.ifi.in2000.gruppe40.prosjekt

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.home.Navigation
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.theme.Gruppe_40_prosjektTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Gruppe_40_prosjektTheme {
                Navigation()
            }
        }
    }
}




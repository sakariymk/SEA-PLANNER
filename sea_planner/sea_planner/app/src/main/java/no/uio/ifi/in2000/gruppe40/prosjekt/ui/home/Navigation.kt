package no.uio.ifi.in2000.gruppe40.prosjekt.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.gruppe40.prosjekt.R
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.warning.WarningScreen
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather.WeatherScreen
import no.uio.ifi.in2000.gruppe40.prosjekt.ui.weather.WeatherViewModel


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val weatherviewModel: WeatherViewModel = viewModel()
    val saveableStateHolder = rememberSaveableStateHolder()
    val homeScreenViewModel: HomeScreenViewModel = viewModel()
    val isLoading by homeScreenViewModel.isLoading.collectAsState()

    if (isLoading) {
        LoadingScreen()
    } else {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                saveableStateHolder.SaveableStateProvider("home") {
                    HomeScreen(weatherviewModel, homeScreenViewModel)
                }
            }

            composable("notifications") { WarningScreen() }
            composable("weather") {
                saveableStateHolder.SaveableStateProvider("weather") {
                    WeatherScreen(weatherviewModel)
                }

            }
        }
    }

    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    //val homeScreenViewModel: HomeScreenViewModel = viewModel()
    //val isLoading by homeScreenViewModel.isLoading.collectAsState()
    if (!isLandscape) {
        NavigationBar {
            NavigationBarItem(
                label = { Text("Vær") },
                selected = false,
                onClick = { navController.navigate("weather") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                } },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.day_partial_cloud),
                        contentDescription = "Weather",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            NavigationBarItem(
                label = { Text("Hjem") },
                selected = false,
                onClick = {
                    //homeScreenViewModel.navigateLoading(navController, "home")
                    navController.navigate("home")
                    {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                } },
                icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") }
            )

            NavigationBarItem(
                label = { Text("Varsler") },
                selected = false,
                onClick = { navController.navigate("notifications") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
                          },
                icon = { Icon(
                    painter = painterResource(R.drawable.warning_icon),
                    contentDescription = "warning icon",
                    modifier = Modifier.size(32.dp)
                )
                    //(Icons.Outlined.Notifications, contentDescription = "Varsler")
                }
            )
        }
    }

}
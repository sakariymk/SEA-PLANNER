# Architecture

We have adhered closely to established [Android "best practices"](https://developer.android.com/topic/architecture/recommendations). The application follows a layered MVVM architecture, where the data layer and the UI layer are cleanly separated and each component has a well-defined, single responsibility. 
This structure delivers high cohesion within each layer and low coupling between layers, these are the key principles for writing robust and maintainable code.

> Examples:
>> Cohesion: For example, the sole purpose of `BoatDataSource` is to get coordinates, name, shiptype etc. Thereafter fetch the data from the BarentsWatch API and deserialize it.

>> Coupling: `WeatherScreen` for example only depends on data from `WeatherViewModel`.

We have tried to deliver high cohesion and low coupling between layers throughout the whole structure and not just in the examples above.
But we unfortunately have low cohesion and high coupling in the part that HomeScreen is using both HomeScreenViewModel and WeatherViewModel. Mapview is also using HomeScreenViewModel, WeatherViewModel and BoatViewModel. 

For our architeture we have used "Unidirectional Data Flow". Which means that state flows in only one direction, every composable reads state but never mutates it directly. When the user performs an action like choosing a location from a dropdown meny, the composable then calls an intent function/method in its ViewModel.
The ViewModel then updates this chosen variable (location) by using `StateFlow`. Each time the StateFlow emits a new value, the Compose runtime automatically recomposes the UI, ensuring the screen always mirrors the current data. 

>Example:
>> Whenever the user selects a location in `WeatherScreen`, the composable calls a function in `WeatherViewModel`that updates the `chosenItem` StateFlow. Because the UI collects this flow with collectAsState(), the dropdown menu instantly reflects the new selection.


## Architecture drawing
![MaritimeApp-ApplikasjonArkitekturV2 drawio](https://github.uio.no/IN2000-V25/team-40/assets/11232/d29ed29d-c026-4217-a17f-271e41ae9563)



## Further Development

### API level

#### MinSDK

**minSdk = 26**

We raised the project’s minimum SDK from the Android-Studio default of 24 to 26 so that we could, among other things, use `formatDateTime()` for timestamping warning reports. Several other date- and time-handling utilities we depend on also require API 26, so the upgrade unlocks them all in one step.

We could have worked around this, yet migrating to API 26 affects only [about 2 % of devices](https://apilevels.com/), an impact we regard as well within acceptable limits.


#### Target SDK
**targetSdk = 35**

Target SDK 35 is the default for android studio projects and is the recommended and latest SDK during the project period


### Maintenance and further development.

The application is made in Kotlin using Jetpack Compose. It is built around [MET APIes](https://api.met.no/) such as "GribFiles", "Kuling- og farevarsler" and "Weather". We have used an additional API from [BarentsWatch](https://nais.kystverket.no/?lang=en) to show boats on the map. The data is fetched using `Retrofit`, and then processed in repositories such as `GribRepository`, `BoatRepository`, `WeatherRepository` etc. The data from GRIB files is extracted by using a GRIB decoder [JGribX](https://github.com/spidru/JGribX), more specifically we have used it as a library and it is located under the `libs` folder as `jgribx-jdk8.jar`. GRIB and any other data is stored in the UI layer in the viewmodels for for most of the screens.`HomeScreen` and `MapView` are the most important screens/files in the code, and their viewmodels: `HomeScreenViewModel` and `WeatherViewModel`. 

### Technological debt/ Further improving code

-  HomeScreen should just use one viewmodel HomeScreenViewModel
-  MapView should just use one viewmodel WeatherViewmodel
-  WarningScreen uses WeatherViewModel, should have its own viewmodel
-  Write more tests


## Libraries and other resources we have used

[JGribX](https://github.com/spidru/JGribX) GRIB decoder/parser








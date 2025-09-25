# SEA PLANNER

SEA PLANNER is an Android app for planning sea trips along the Norwegian coastline. It provides real-time weather, marine traffic, and hazard info using MET and BarentsWatch data.  

### Main Features
1. **Main Map** – Interactive map with weather from GRIB files. Shows precipitation, wave height (color-coded), wind and currents (arrows), MET alerts, and nearby vessels. Users can add custom markers and adjust thresholds for areas of concern.  
2. **Weather Forecast** – 4-day hourly forecast for temperature, precipitation, and wind. View weather for your location, cities, or custom markers to plan trips or choose alternative dates/ports.  
3. **Alerts** – List of marine warnings color-coded by severity. Tap for details, risks, and instructions.  

## 🖼 Screenshots
![Main Map](https://github.com/user-attachments/assets/28a3e61e-93cf-4741-b6cc-68751f3d5508)  
*(Main map with weather, vessels, and markers)*  

## 📦 Quick Start
1. Install [Android Studio](https://developer.android.com/studio/install)  
2. Clone/download this project  
3. Open in Android Studio: [Guide](https://developer.android.com/studio/projects/create-project#ImportAProject)  
4. Enable Google Maps API and get your key: [Guide](https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#1)  
5. Add API key in `local.properties`:
```properties
MAP_API_NOKKEL = YOUR_KEY_HERE
Run the app on an emulator or Android device: Guide

If errors occur: run Gradle sync → Build > Clean Project → restart Android Studio if needed.

📙 Libraries
Kotlin & AndroidX Core, Compose, Google Maps Compose

Coil, Ktor, Retrofit & Gson, OkHttp, Logger

JGribX, Local JAR file

Unit testing: JUnit, Mockito, MockK

Contributing
Pull requests welcome. For major changes, open an issue first to discuss.

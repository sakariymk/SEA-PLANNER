# SEA PLANNER

SEA PLANNER is an android app for planning trips on the sea. It uses data from MET and Barentswatch to display various information about the situation on the Norwegian coastline

<img width="859" height="419" alt="image" src="https://github.com/user-attachments/assets/28a3e61e-93cf-4741-b6cc-68751f3d5508" />


## 📦 How to run the app
+ Download and install Android Studio, [See guide here](https://developer.android.com/studio/install)
+ Download this projectfold from GitHub or download it from the deliveryfolder in Devilry
+ Open the project in Andorid Studio [See guide here](https://developer.android.com/studio/projects/create-project#ImportAProject)
+ Navigate to Google Maps API and follow the instructions there.
+ Create an emulator or connect to an Andorid device and run the app. [See guid here](https://developer.android.com/studio/run/emulator#get-started). 
> If an error was to randomly happen, run gradle sync. Afterwards click on `build > clean project`
> If you move locations, and use a different internet, restart Android Studio



## Google Maps API

IMPORTANT! In order for the app to function, along with its map, it is vital that you - the user - acquire your own google maps API key. 
Without this API key, the entire map will be empty. To avoid this, please follow these steps on this link:
https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#1

ABOUT THE GOOGLE BILLING:
Essentially, you will need to make your own Google Cloud Platform account.
Note that it will mention billing, however, you can choose a free course which includes
a 90 day trial, in which after its due, you can cancel without ever commiting your funds.
Basically, a free 90 day access to an API key. 

After getting your account, make sure to create a new project (if you do not already have one).
Then search for "Maps SDK for Android", and have it enabled. 
After enabling it, you should get a prompt showing your API key. 
In the event you do not see this key on a prompt, go over to "Key & Credentials", 
on the furthest right side where it says "Maps Platform API Key", should be the option to show your key.

(Video tutorial link below:)
https://youtu.be/gnvbxMfuZc0?t=91 to 4:14

AFTER GETTING THE KEY:

After acquiring your own Google Maps API key, go over to your 
local.properties file. Then you must write the following:

MAP_API_NOKKEL = YOUR_KEY_HERE

(Tutorial link below:)
https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#3

ABOUT HOW THE KEY IS USED:
MAP_API_NOKKEL is a variable stored within the app. DO NOT CHANGE ANYTHING ABOUT THIS VARIABLE!
The only thing you must do with this is, after the '=' symbol, write your API key. Do not use "" or ''
or any other symbols on YOUR_KEY_HERE.


ABOUT LOCAL.PROPERTIES:
This file is only for you to modify. This means that it is ONLY available to you; not your friends/colleagues. 

AFTER THE STEPS
After writing MAP_API_NOKKEL = YOUR_KEY_HERE on local.properties, you must close Android Studio and re-open it.
Hopefully it should show the map from your API key. Keep in mind that it could take several minutes for the API to fully register 
within Android Studio. 

In the event that the map still does not show up, make sure to:
—make sure your google 
—write your API key correctly

## 📙 Libraries   
    +Kotlin and AndroidX core
    +Compose
    +Debug Compose
    +Compose UI-testing
    +Google Maps Compose and related
    +Coil
    +Ktor
    +Retrofit and Gson
    +OkHttp
    +Logger
    +Local JAR-fil
    +Unit testing: junit, core-testing, mockito, mockk, 
    +JGribX

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

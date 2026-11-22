# WeatherPeek
This is the repository of the app WeatherPeek. It is an app about weather data prediction created
by me, Marco Túlio Todeschini Coelho. You may ask, why make another weather data app prediction,
since there are so many already available? Well, the answer is quite simple: I did it to show up my
skills with modern software engineering techniques, tools and procedures. This app was made in
Kotlin for Android, using coroutines, Retrofit2 as a library for online data recovery, screen
transition with shared elements, the MVVM architecture, Room database, Work Manager, view binding,
Glide for online image retrieval, and local image caching, plus custom android views created by
myself.

This app consumes the OpenWeather One Call API 4.0 endpoints
(https://api.openweathermap.org/data/4.0/onecall/current, .../timeline/1h and .../timeline/1day).

If you downloaded this project, and it doesn't work by itself out of the box, it is because
you need to add an openweathermap.org app id key to the project. Add it to the `local.properties`
file in the project root (this file is ignored by git, so the key never gets committed):

```properties
oneCallAppId=YOUR_APP_ID
```

The project still builds without it, but it prints a warning while building and the app shows a
message asking for the app id instead of retrieving any data. Be aware that One Call is sold as a
separate "One Call API" subscription: a brand new account key will not be accepted by these
endpoints until you subscribe to that plan (it includes a free daily call allowance, but the
subscription itself still has to be activated). Each weather refresh costs 5 calls.

It is currently licensed under MIT license, so feel free to use it as you please.

Also, I would like to thanks and attribute some icons and images used in this app to the following
artist: http://www.freepik.com/ at https://www.flaticon.com/

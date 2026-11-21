# WeatherPeek
This is the repository of the app WeatherPeek. It is an app about weather data prediction created
by me, Marco Túlio Todeschini Coelho. You may ask, why make another weather data app prediction,
since there are so many already available? Well, the answer is quite simple: I did it to show up my
skills with modern software engineering techniques, tools and procedures. This app was made in
Kotlin for Android, using coroutines, Retrofit2 as a library for online data recovery, screen
transition with shared elements, the MVVM architecture, Room database, Work Manager, view binding,
Glide for online image retrieval, and local image caching, plus custom android views created by
myself.

This app consumes the OpenWeather One Call API 3.0 endpoint
(https://api.openweathermap.org/data/3.0/onecall).

If you downloaded this project, and it doesn't work by itself out of the box, it is because
you need to add an openweathermap.org app id key to the project, in the val oneCallAppId of
the file Constants.kt. Otherwise, this app won't retrieve any data. Be aware that One Call 3.0
is sold as a separate "One Call by Call" subscription: a brand new account key will not be
accepted by this endpoint until you subscribe to that plan (it includes a free daily call
allowance, but the subscription itself still has to be activated).

It is currently licensed under MIT license, so feel free to use it as you please.

Also, I would like to thanks and attribute some icons and images used in this app to the following
artist: http://www.freepik.com/ at https://www.flaticon.com/

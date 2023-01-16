# WeatherPeek
This is the repository of the app WeatherPeek. It is an app about weather data prediction created
by me, Marco Túlio Todeschini Coelho. You may ask, why make another weather data app prediction,
since there are so many already available? Well, the answer is quite simple: I did it to show up my
skills with software engineering techniques, and tools. This app was made in Kotlin, for Android,
using Retrofit2 as a library for online data recovery, using screen transition with shared elements,
using the MVVM architecture, the newest activity permission request method with
registerForActivityResult, view binding for code cleanup and Glide for online image retrieval,
local image caching and custom android views. I also plan to implement the repository pattern for
the weather data retrieved.

If you downloaded this project, and it doesn't work by itself out of the box, it is because
you need to add an openweathermap.org app id key to the project, in the val oneCallAppId of
the file OneCallAppId.kt. Otherwise, this app won't retrieve any data.

It is currently licensed under MIT license, so feel free to use it as you please.

Also, I would like to thanks and attribute some icons and images used in this app to the following
artist: http://www.freepik.com/ at https://www.flaticon.com/

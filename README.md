# CoronaVirus-2019-nCoV-Live-Tracking-App

CoronaVirus(2019-nCoV) outbreak Live Map tracker 

[![PlayStore][playstore-image]][playstore-url]


<!-- Put the following at the end of README.md -->
[playstore-image]: https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png

<!-- Setup URLs -->
[playstore-url]: https://play.google.com/store/apps/details?id=co.kyald.coronavirustracking&hl=en

##  Introduction
With latest CoronaVirus(2019-nCoV) outbreak, this app helps you to monitor current situation of the outbreak.

##  Tech Spesification
- AndroidX
- Kotlin
- Coroutines
- Retrofit for network request
- Room
- Glide
- Koin for DI
- MapBox

In this project I'm using MVVM and Repository Pattern.

##  How to use it?
- Clone this project using git clone [url]
- build the project by using ./gradlew clean :app:assembleDebug

_Make sure to make some changes with signingConfig in signing.gradle_

## Data Source
- The Center for Systems Science and Engineering (CSSE) at JHU
- https://infographics.channelnewsasia.com/

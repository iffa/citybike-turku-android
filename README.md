# citybike-turku-android (Fölläri)

<img src="app/src/main/web_hi_res_512.png" align="left"
width="200"
    hspace="10" vspace="10">

An open source, ad free Android application for Turku's city bike service. Written in Kotlin and using the latest Android Architecture Components.

Available on the Google Play Store soon
<a href="https://play.google.com/store/apps/details?id=xyz.santeri.citybike">
    <img alt="Get it on Google Play"
        height="80"
        src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>

**Data source:** Turku region public transport's transit and timetable data. The administrator of the data is Turku region public transport. Dataset is downloaded from http://data.foli.fi/ using the license Creative Commons Attribution 4.0 International (CC BY 4.0).

**Icon made by [Freepik](http://freepik.com) from [Flaticon](http://flaticon.com).**

## Contributing
Contributions are always welcome. If you want to contribute, fork the project and submit a pull request.

### Configuration
In order to build and test the app, you need to add your Google Maps API key to your [user home](gradle) `gradle.properties` file (as `CITYBIKE_MAPS_API_KEY`), and generate a release key as `signing/release.jks`.

## License

    Copyright 2018 Santeri Elo <me@santeri.xyz>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[contributors]: https://github.com/iffa/citybike-turku-android/graphs/contributors
[gradle]: https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
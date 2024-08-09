<h1 align="center">Story App 🤳🏻</h1>

<p align="center">
  Android app to update whatever it is you're doing! <br>
  <a href="https://android-arsenal.com/api?level=23"><img alt="API" src="https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://maps.gooogle.com"><img alt="Maps" src="https://img.shields.io/badge/Google%20Maps-brightgreen.svg?style=flat&logo=google-maps"/></a>
</p>

<p align="center">  
  This app implements MVVM Architecture, Unit Testing, and Paging3. Story app also implements user authentication, and the ability for users to see other user's story and upload a story of their own.
</p>
</br>

## Source API 🛜

Story App uses the proprietary story API provided by Dicoding, documented [here](https://story-api.dicoding.dev/v1/#/) for its data. The API provides the basic functionality of the application. **Retrofit** is used for handling data communication with the API.

## Continuous Integration 📲

![image](https://github.com/ranggarahman/Play-Pal/assets/79177708/3c2b0fc6-725c-43ae-a0bc-2ae4fec558fa)

Story App uses GitHub Actions to implement continous integration. The tests being run [here](https://github.com/ranggarahman/Story-App/actions) includes running Unit Tests and Generating the APK to check for a successful build.

## Architecture 👷🏻
**PlayPal** is based on the Clean Architecture, which is based on [Google's official architecture guidelines](https://developer.android.com/topic/architecture).

Here is the illustration of how Clean Architecture is implemented in Story App:

<div align="center">
  <img src="https://github.com/ranggarahman/Play-Pal/assets/79177708/77536986-75a4-4922-98b8-7cb8be9e846d" alt="Clean Architecture in PlayPal" width="400">
</div>

The names listed in the diagram represents the classes that are used in building Story App.

## Paging3 💉

Story App utilizes Paging 3 to implement lazy loading, ensuring the efficient display even if there are hundreds of stories to be displayed.

## Google Maps Integration 📌

Story App also integrates location feature using Google Maps to view stories based on their location.

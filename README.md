# Emojify

<p align="center"> <img src="https://cdn.rawgit.com/bruno78/emojify/b9345908/app/src/main/res/drawable/closed_smile.png" width="200" alt="icon image" > </p>

This application uses [Google Mobile Vision](https://developers.google.com/vision/) for 
face recognition and _emojify_ according with the face expression. 

This recognition is based on combination of two groups: eyes and lips.



## Pre-requisites

* Android SDK v25
* Android min SDK v15

## Tools Used 

* [Google Mobile Vision ](https://developers.google.com/vision/)
* [ButterKnife](http://jakewharton.github.io/butterknife/)
* [Timber](https://github.com/JakeWharton/timber)

## Instructions 

Download or clone this repo on your machine, open the project using Android Studio. 
Once Gradle builds the project, click "run" and choose an emulator.

## Issues

* The apps only recognize faces if photo is taken on landscape mode.
* App crashes when you choose not to save a picture previously taken, use the camare
again to emojify a new picture. 




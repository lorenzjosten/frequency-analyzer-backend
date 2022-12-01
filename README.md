# Backend

This reactive backend web application uses Spring Boot with a file based h2 database.

It features a basic json API for uploading a mpeg-3 layer audio file and streaming its data. Power-spectra are
calculated from the frames of the mp3 file.

## Build

Build the app from the parent project "app" for a production build with frontend. For a standalone bootJar run:

```shell
gradlew build
```

## Execution

The backend app can be run in development mode:

```shell
gradlew bootRun
```

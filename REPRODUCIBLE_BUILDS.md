# Reproducible Builds

Note: reproducible builds work starting version 1.0.44

## Install Docker

Download and install [Docker](https://www.docker.com/).

## Check your Thorium app version and build timestamp

1. Open the Thorium app
2. Go to Settings
3. Check the app version listed under About 'Version' (e.g., 1.0.44), and record its value to be used later
4. Check the build timestamp under About 'Build Time' (e.g., 1593942384524), and record its value to be used later

## Download the App open-source code

1. Make sure you have `git` installed
2. Clone the Github repository
3. Checkout the Tag that corresponds to the version of your Thorium app (e.g., 1.0.44)

```shell
git clone https://github.com/sschueller/peertube-android ~/peertube-android
cd ~/peertube-android
git checkout v1.0.44
```

## Build the project using Docker

1. Build a Docker Image with the required Android Tools
2. Build the App in the Docker Container while specifying the build timestamp that was recorded earlier (e.g., 1593942384524)
3. Copy the freshly-built APK

```shell
cd ~/peertube-android
docker build -t thorium-builder .
docker run --rm -v ~/peertube-android:/home/peertube-android -w /home/peertube-android thorium-builder gradle assembleProdRelease -PkeystorePassword=securePassword -PkeyAliasPassword=securePassword -PkeystoreFile=build.keystore -PbuildTimestamp=1593942384524
cp app/build/outputs/apk/prod/release/app-prod-release.apk thorium-built.apk
```

## Extract the Play Store APK from your phone

1. Make sure you have `adb` installed
2. Connect your phone to your computer
3. Extract the APK from the phone

```shell
cd ~/peertube-android
adb pull `adb shell pm path net.schueller.peertube | cut -d':' -f2` thorium-store.apk
```

## Compare the two files

1. Make sure you have `python` installed
2. Use the `apkdiff` script to compare the APKs

```shell
cd ~/peertube-android
python apkdiff.py thorium-built.apk thorium-store.apk
```


## Required Gitlab CI Variables
github_token # git hub token to push release to github
GITLAB_TOKEN # token from gitlab user so the version bump can be commited
google_play_service_account_api_key_json # google play store json
signing_jks_file_hex # We store this binary file in a variable as hex with this command, `xxd -p android-signing-keystore.jks > jks.txt` (remove all \n)
signing_key_alias # Alias name of signing key
signing_key_password # Key password
signing_keystore_password # keystore password

## Testing CI locally

```
cd ${repo}
# build docker image
sudo docker build -t thorium ../thorium # where the second thorium is the name of the ${repo} folder
# build debug
cd ${repo}
sudo docker run --rm -v "$(pwd):/build/project" -w "/build/project" -it thorium bundle exec fastlane buildDebug
# run tests
sudo docker run --rm -v "$(pwd):/build/project" -w "/build/project" -it thorium bundle exec fastlane test
# build release,
sudo docker run --rm -v "$(pwd):/build/project" -w "/build/project" -it thorium bundle exec fastlane buildRelease
```

# warning running this on your local repo may create files owned by root because of docker for example the build dir.
These have to be removed with sudo

# Update fastlane
```
sudo docker run --rm -v "$(pwd):/build/project" -w "/build/project" -it thorium bundle update
sudo chown -R myuser *
```
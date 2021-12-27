## Internal deployment notes

  1. merge pull-requests on github into develop
  2. Locally switch to develop
  3. Pull github develop
  4. Pull weblate develop
  5. Push to develop gitlab and github
  6. Merge develop into master and merge
  7. Wait for Release Build and release to play store
  8. Wait for gitlab -> github sync
  9. Run publishGithub


## fastlane update
```
bundle update
```
## Internal deployment notes

  1. merge pull-requests on github into develop
  2. Locally switch to develop
  3. Pull github develop
  4. Pull weblate develop
  5. Push to develop gitlab
  6. Merge develop into master and merge
  7. Wait for Release Build and release to play store
  8. Wait for gitlab -> github sync
  9. Run publishGithub
 10. Merge master into develop, push to github


## fastlane update (install ruby2.7 and "gem-2.7 install bundler")
```
bundle-2.7 update
```
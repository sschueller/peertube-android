## Internal deployment notes

  1. merge pull-requests on github into develop
  2. Locally switch to develop
  3. Pull github develop
  4. Pull weblate develop
  5. Add change logs (fastlane/metadata/android/en-US/changelogs/XXX.txt)
  6. Push to gitlab and github
  7. Merge request into master and merge
  8. Add Release Tag on master branch
  9. Release to play store
  10. Wait for gitlab -> github sync
  11. Run publishGithub



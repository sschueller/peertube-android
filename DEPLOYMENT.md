## Internal deployment notes

  1. merge pull-requests on github into develop
  2. Locally switch to develop
  3. Pull github develop
  4. Pull weblate develop
  5. Add change logs (fastlane/metadata/android/en-US/changelogs/XXX.txt)
  6. Run ci-script/update-changelog.sh
  7. Push to gitlab
  8. Merge request into master and merge
  9. Add Release Tag on master branch
  10. Release to play store
  11. Wait for gitlab -> github sync
  12. Run publishGithub



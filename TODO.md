Existing Features:
x auto hide top bar
x sort / filter of videos
- search video service
x address book
x add
x remove
x edit
x server list
x filter
--- pick
? server authentication
x insecure server option
? switch url of retrofit api
- Internal video playlist
- playback speed and options in player
- pip mode
- background audio playback
x like / dislike
- account view
x discover view
- channel view
- Permissions for download
- subscribe / unsubscribe
x CI pipeline (gradle?)

Issues:
- Server change doesn't work until restart (retrofit)
- Login/logout needs to update UI
- Memory issue on explore list
- Word "Subscribe" too long in app bar
- VideoList meta bar can't be dynamic height, causes scroll back issue.
- Refreshing video list causes odd loading order of video items
- playback rotate on click doesn't re-hide buttons
- Explore list is memory intensive, leak??
- Access Token refresh circular injection problem
- app crashes when clicking items in background list while player is visible (minimode)

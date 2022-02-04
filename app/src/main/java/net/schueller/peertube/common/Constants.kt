package net.schueller.peertube.common

object Constants {

    const val PREF_LANG_APP_KEY = "pref_language_app"
    const val PREF_THEME_KEY = "pref_theme"
    const val PREF_DARK_MODE_KEY = "pref_dark_mode"
    const val PREF_SHOW_NSFW_KEY = "pref_show_nsfw"
    const val PREF_VIDEO_LANG_KEY = "pref_show_nsfw"
    const val PREF_VIDEO_SPEED_KEY = "pref_video_speed"
    const val PREF_BACK_PAUSE_KEY = "pref_back_pause"
    const val PREF_BACKGROUND_BEHAVIOR_KEY = "pref_background_behavior"
    const val PREF_BACKGROUND_AUDIO_KEY = "backgroundAudio"
    const val PREF_BACKGROUND_STOP_KEY = "backgroundStop"
    const val PREF_BACKGROUND_FLOAT_KEY = "backgroundFloat"
    const val PREF_TORRENT_PLAYER_KEY = "pref_torrent_player"
    const val PREF_ACCEPT_INSECURE_KEY = "pref_accept_insecure"
    const val PREF_CLEAR_HISTORY_KEY = "pref_clear_history"

    const val PREF_TOKEN_ACCESS = "pref_token_access"
    const val PREF_TOKEN_REFRESH = "pref_token_refresh"
    const val PREF_TOKEN_EXPIRATION = "pref_token_expiration"
    const val PREF_TOKEN_TYPE = "pref_token_type"
    const val PREF_AUTH_USERNAME = "pref_auth_username"
    const val PREF_AUTH_PASSWORD = "pref_auth_password"
    const val PREF_CLIENT_ID = "pref_client_id"
    const val PREF_CLIENT_SECRET = "pref_client_secret"
    const val PREF_API_BASE_KEY = "pref_api_base_key"
    const val PREF_QUALITY_KEY = "pref_quality_key"

    const val FALLBACK_BASE_URL = "https://troll.tv" // Thorium test peertube server
    const val SERVER_IDX_BASE_URL = "https://instances.joinpeertube.org/api/v1/"

    const val INVALID_URL_PLACEHOLDER = "http://invalid"
    const val VIDEO_SHARE_URI_PATH = "/videos/watch/"
    const val PEERTUBE_API_PATH = "/api/v1/"

    const val VIDEOS_API_PAGE_SIZE = 25
    const val SERVERS_API_PAGE_SIZE = 25
    const val VIDEOS_API_START_INDEX = 0
    const val SERVERS_API_START_INDEX = 0
    const val PARAM_VIDEO_UUID = "uuid"

    const val APP_BACKGROUND_AUDIO_INTENT = "BACKGROUND_AUDIO"

}
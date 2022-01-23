package net.schueller.peertube.feature_server_address.domain.use_case

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.Constants.PREF_API_BASE_KEY
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_video.data.remote.auth.LoginService
import net.schueller.peertube.feature_video.data.remote.auth.Session


class SelectServerAddress (
    @ApplicationContext private val context: Context,
    private val session: Session,
    private val loginService: LoginService
) {
    private val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    operator fun invoke(serverAddress: ServerAddress) {

        // save new server to pref
        val editor = sharedPreferences.edit()
        editor.putString(PREF_API_BASE_KEY, serverAddress.serverHost)
        editor.apply()

        // invalidate session
        if (session.isLoggedIn()) {
            session.invalidate()
        }

        // attempt auth if we have username
        if (serverAddress.username.isNullOrBlank().not()) {
            loginService.authenticate(serverAddress.username, serverAddress.password)
        }

        // TODO: notify views that this has changed

    }
}
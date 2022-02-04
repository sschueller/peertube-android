package net.schueller.peertube.feature_server_address.domain.use_case

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.Constants.PREF_API_BASE_KEY
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_video.data.remote.auth.LoginService
import net.schueller.peertube.feature_video.data.remote.auth.Session
import net.schueller.peertube.feature_video.data.repository.RetrofitInstance


class SelectServerAddressUseCase (
    @ApplicationContext private val context: Context,
    private val session: Session,
    private val loginService: LoginService,
    private val retrofitInstance: RetrofitInstance
) {
    private val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    operator fun invoke(serverAddress: ServerAddress) {

        Log.v("SSA", "Server: " + serverAddress.serverHost)

        // save new server to pref
        val editor = sharedPreferences.edit()
        editor.putString(PREF_API_BASE_KEY, serverAddress.serverHost)
        editor.apply()

        // reload Retrofit
        retrofitInstance.updateRetrofitInstance()

        // invalidate session
        if (session.isLoggedIn()) {
            Log.v("SSA", "session invalidate")
            session.invalidate()
        }

        // attempt auth if we have username
        if (serverAddress.username.isNullOrBlank().not()) {
            Log.v("SSA", "Attempt auth")
            loginService.authenticate(serverAddress.username, serverAddress.password)
        }

        // TODO: notify views that this has changed

    }
}
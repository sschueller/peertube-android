package net.schueller.peertube.feature_video.data.remote.auth

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AccessTokenAuthenticator @Inject constructor(
    private val session: Session,
): Authenticator {

    private val tag = "ATAuthenticator"

    override fun authenticate(route: Route?, response: Response): Request? {

        // check if we are using tokens
        val accessToken = session.getToken()
        if (!isRequestWithAccessToken(response) || accessToken == null) {
            return null
        }
        synchronized(this) {
            val newAccessToken = session.getToken()
            // Access token is refreshed in another thread.
            if (accessToken != newAccessToken) {
                Log.v(tag, "Access token is refreshed in another thread")
                return newRequestWithAccessToken(response.request, newAccessToken!!)
            }

            // do we have a refresh token?
            if (session.getRefreshToken() == null) {
                Log.v(tag, "No refresh token available")
                return null
            }
            Log.v(tag, "refresh token: " + session.getRefreshToken())

            // Need to refresh an access token
//            Log.v(tag, "Need to refresh an access token")
//            loginService.refreshToken()
//            val updatedAccessToken = session.getToken()
//            if (updatedAccessToken != null) {
//                return newRequestWithAccessToken(response.request, updatedAccessToken)
//            }
            Log.v(tag, "Refresh failed")

//            Log.v(tag, "Invalidating saved credentials")
//            session.invalidate()
//
//            Log.v(tag, "new request without credentials")
//            return newRequestWithOutAccessToken(response.request)

            // TODO, remove credentials if invalid

            return null
        }
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return header != null && header.startsWith("Bearer")
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", accessToken)
            .build()
    }
    private fun newRequestWithOutAccessToken(request: Request): Request {
        return request.newBuilder()
            .build()
    }
}
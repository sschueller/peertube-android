package net.schueller.peertube.feature_video.data.remote.auth

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Request
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val session: Session
) :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val tag = "AInterc"

        val mainResponse: Response
        val mainRequest: Request = chain.request()
        val token = session.getToken()

        if (session.isLoggedIn() && token !== null) {

            // add authentication header to each request if we are logged in
            val builder: Request.Builder =
                mainRequest.newBuilder().header("Authorization", token)
                    .method(mainRequest.method, mainRequest.body)
            Log.v(tag, "Intercept: " + session.getToken());

            // build request
            val req: Request = builder.build()
            mainResponse = chain.proceed(req)

            // logout on auth error
            if (401 or 403 == mainResponse.code) {
                session.invalidate()
                Log.v(tag, "Intercept: Logout forced")
            }
        } else {
            Log.v(tag, "Mot logged in or no token")
            mainResponse = chain.proceed(chain.request())
        }

        return mainResponse

    }

}
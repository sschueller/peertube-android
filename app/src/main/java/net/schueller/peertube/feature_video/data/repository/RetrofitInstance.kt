package net.schueller.peertube.feature_video.data.repository

import android.annotation.SuppressLint
import android.util.Log
import net.schueller.peertube.common.UrlHelper
import net.schueller.peertube.feature_video.data.remote.PeerTubeApi
import net.schueller.peertube.feature_video.data.remote.auth.AccessTokenAuthenticator
import net.schueller.peertube.feature_video.data.remote.auth.AuthorizationInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.*


class RetrofitInstance @Inject constructor(
    private val authorizationInterceptor: AuthorizationInterceptor,
    private val accessTokenAuthenticator: AccessTokenAuthenticator,
    private val urlHelper: UrlHelper
) {
    private val tag = "RFI"

    companion object {
        private var retrofitInstance : PeerTubeApi? = null
    }

    // TODO: this doesn't work, existing UseCases keep using previous instance
    fun updateRetrofitInstance(
        insecure: Boolean = false
    ) {
        retrofitInstance = build(insecure)
    }

    @Synchronized
    fun getRetrofitInstance(): PeerTubeApi {
        if (retrofitInstance == null) {
            retrofitInstance = build( false)
        }
        return retrofitInstance as PeerTubeApi
    }

    fun build(insecure: Boolean = false): PeerTubeApi {

        val apiUrl = urlHelper.getUrlWithVersion()

        Log.v(tag, "current Url: $apiUrl")

        val okhttpClientBuilder: OkHttpClient.Builder = if (!insecure) {
            Log.v(tag, "Secure Instance")
            OkHttpClient.Builder()
        } else {
            Log.v(tag, "Unsafe Instance")
            getUnsafeOkHttpClient()
        }

        // TODO: Remove debug
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        okhttpClientBuilder.addInterceptor(logging)

        // Add auth
        okhttpClientBuilder.addInterceptor(authorizationInterceptor)
        okhttpClientBuilder.authenticator(accessTokenAuthenticator)

        return Retrofit.Builder()
            .client(okhttpClientBuilder.build())
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PeerTubeApi::class.java)
    }


}



fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
    // Create a trust manager that does not validate certificate chains
    // Install the all-trusting trust manager
    // Create an ssl socket factory with our all-trusting manager
    try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

            }
        )

        // Install the all-trusting trust manager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        val builder = OkHttpClient.Builder()

        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier(HostnameVerifier { _: String?, _: SSLSession? -> true })

        return builder

    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

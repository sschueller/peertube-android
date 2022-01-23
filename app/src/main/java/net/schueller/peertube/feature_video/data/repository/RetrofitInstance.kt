package net.schueller.peertube.feature_video.data.repository

import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient
import retrofit2.Retrofit;

import android.annotation.SuppressLint
import android.util.Log
import net.schueller.peertube.common.Constants.BASE_URL
import net.schueller.peertube.feature_video.data.remote.auth.AccessTokenAuthenticator
import net.schueller.peertube.feature_video.data.remote.auth.AuthorizationInterceptor
import net.schueller.peertube.feature_video.data.remote.PeerTubeApi
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.*

@Singleton
class RetrofitInstance @Inject constructor(
    private val authorizationInterceptor: AuthorizationInterceptor,
    private val accessTokenAuthenticator: AccessTokenAuthenticator
) {
    private val tag = "RetrofitInstance"

    private var retrofitInstance = build(BASE_URL, false)

    fun updateBaseUrl(
        baseUrl: String = BASE_URL,
        insecure: Boolean = false
    ) {
        retrofitInstance = build(baseUrl, insecure)
    }

    fun getRetrofitInstance(): PeerTubeApi {
        return retrofitInstance;
    }

    fun build(baseUrl: String = BASE_URL,
              insecure: Boolean = false): PeerTubeApi {
        val okhttpClientBuilder: OkHttpClient.Builder = if (!insecure) {
            Log.v(tag, "Secure Instance")
            OkHttpClient.Builder()
        } else {
            Log.v(tag, "Unsafe Instance")
            getUnsafeOkHttpClient()
        }
        okhttpClientBuilder.addInterceptor(authorizationInterceptor)
        okhttpClientBuilder.authenticator(accessTokenAuthenticator)
        return Retrofit.Builder()
            .client(okhttpClientBuilder.build())
            .baseUrl(baseUrl)
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

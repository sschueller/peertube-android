package net.schueller.peertube.feature_video.data.remote.auth

import android.content.Context

import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.schueller.peertube.common.Constants.PREF_AUTH_USERNAME
import net.schueller.peertube.common.Constants.PREF_CLIENT_ID
import net.schueller.peertube.common.Constants.PREF_CLIENT_SECRET
import net.schueller.peertube.common.Constants.PREF_TOKEN_ACCESS
import net.schueller.peertube.common.Constants.PREF_TOKEN_EXPIRATION
import net.schueller.peertube.common.Constants.PREF_TOKEN_REFRESH
import net.schueller.peertube.common.Constants.PREF_TOKEN_TYPE
import net.schueller.peertube.feature_video.data.remote.PeerTubeApi
import net.schueller.peertube.feature_video.data.remote.dto.OauthClientDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class LoginService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: PeerTubeApi
){
    private val tag = "LoginService"
    private val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    fun authenticate(username: String?, password: String?) {

        try {
            CoroutineScope(Dispatchers.IO).launch {

                val oauthClientDto: OauthClientDto = api.getOauthClientLocal()

                // store new client id and secret
                val editor = sharedPreferences.edit()
                editor.putString(PREF_CLIENT_ID, oauthClientDto.clientId)
                editor.putString(PREF_CLIENT_SECRET, oauthClientDto.clientSecret)
                editor.apply()

                Log.wtf(tag, "sharedPreferences save")


                Log.wtf(tag, "getAuthenticationToken 1")


                Log.wtf(tag, "getAuthenticationToken 2")

                val response = api.getAuthenticationToken(
                    oauthClientDto.clientId,
                    oauthClientDto.clientSecret,
                    "code",
                    "password",
                    "upload",
                    username,
                    password
                )

                val tokenDto = response.body()

                if (response.isSuccessful && tokenDto != null) {

                    Log.wtf(tag, "accessToken: "  + tokenDto.accessToken)

                    editor.putString(PREF_TOKEN_ACCESS, tokenDto.accessToken)
                    editor.putString(PREF_TOKEN_REFRESH, tokenDto.refreshToken)
                    editor.putString(PREF_TOKEN_EXPIRATION, tokenDto.tokenType)

                    editor.apply()

                } else {
                    Log.wtf(tag, "Login failed")
                }

            }

        } catch (e: HttpException) {
            Log.wtf(tag, e.localizedMessage)
        } catch (e: IOException) {
            Log.wtf(tag, "Login Error")
        }

    }

    fun refreshToken() {

        try {

            val refreshToken = sharedPreferences.getString(PREF_TOKEN_REFRESH, null)
            val userName = sharedPreferences.getString(PREF_AUTH_USERNAME, null)
            val clientId = sharedPreferences.getString(PREF_CLIENT_ID, null)
            val clientSecret = sharedPreferences.getString(PREF_CLIENT_SECRET, null)

            CoroutineScope(Dispatchers.IO).launch {

                val response = api.refreshToken(
                    clientId,
                    clientSecret,
                    "refresh_token",
                    "code",
                    userName,
                    refreshToken
                )

                val tokenDto = response.body()

                if (response.isSuccessful && tokenDto != null) {
                    val editor = sharedPreferences.edit()
                    editor.putString(PREF_TOKEN_ACCESS, tokenDto.accessToken)
                    editor.putString(PREF_TOKEN_REFRESH, tokenDto.refreshToken)
                    editor.putString(PREF_TOKEN_TYPE, tokenDto.tokenType)
                    editor.apply()
                    Log.wtf(tag, "Logged in")
                } else {
                    Log.wtf(tag, "Login failed")
                }

            }

        }  catch (e: HttpException) {
            Log.wtf(tag, e.localizedMessage)
        } catch (e: IOException) {
            Log.wtf(tag, "Refresh Error")
        }

    }

}
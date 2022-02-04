package net.schueller.peertube.feature_video.data.remote.auth

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.Constants.PREF_AUTH_PASSWORD
import net.schueller.peertube.common.Constants.PREF_AUTH_USERNAME
import net.schueller.peertube.common.Constants.PREF_TOKEN_ACCESS
import net.schueller.peertube.common.Constants.PREF_TOKEN_REFRESH
import net.schueller.peertube.common.Constants.PREF_TOKEN_TYPE
import net.schueller.peertube.feature_video.domain.model.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Session @Inject constructor(
    @ApplicationContext private val context: Context
    ) {

    private val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        // check if token exist or not
        // return true if exist otherwise false
        // assuming that token exists

        //Log.v("Session", "isLoggedIn: " + (getToken() != null));
        return getToken() != null
    }



    fun getToken(): String? {
        // return the token that was saved earlier
        val token = sharedPreferences.getString(PREF_TOKEN_ACCESS, null)
        val type = sharedPreferences.getString(PREF_TOKEN_TYPE, "Bearer")

        Log.v("Session", "token: " + token)

        return if (token != null) {
            "$type $token"
        } else null
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(PREF_AUTH_PASSWORD, null
        )
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(PREF_TOKEN_REFRESH, null
        )
    }

    fun invalidate() {
        // get called when user become logged out
        // delete token and other user info
        // (i.e: email, password)
        // from the storage
        val editor = sharedPreferences!!.edit()
        editor.putString(PREF_AUTH_PASSWORD, null)
        editor.putString(PREF_AUTH_USERNAME, null)
        editor.putString(PREF_TOKEN_ACCESS, null)
        editor.putString(PREF_TOKEN_REFRESH, null)
        editor.commit()
    }

}
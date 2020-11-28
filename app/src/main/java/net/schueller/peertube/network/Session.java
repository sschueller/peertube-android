/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.schueller.peertube.R;
import net.schueller.peertube.application.AppApplication;

import static net.schueller.peertube.service.LoginService.refreshToken;

public class Session {

    private static volatile Session sSoleInstance;
    private static SharedPreferences sharedPreferences;

    //private constructor.
    private Session() {

        Context context = AppApplication.getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Session getInstance() {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            synchronized (Session.class) {
                if (sSoleInstance == null) sSoleInstance = new Session();
            }
        }

        return sSoleInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected Session readResolve() {
        return getInstance();
    }


    public boolean isLoggedIn() {
        // check if token exist or not
        // return true if exist otherwise false
        // assuming that token exists

        //Log.v("Session", "isLoggedIn: " + (getToken() != null));

        return getToken() != null;
    }

    public String getToken() {
        // return the token that was saved earlier

        String token = sharedPreferences.getString(AppApplication.getContext().getString(R.string.pref_token_access), null);
        String type = sharedPreferences.getString(AppApplication.getContext().getString(R.string.pref_token_type), "Bearer");

        if (token != null) {
            return type + " " + token;
        }

        return null;
    }

    public String getPassword() {
        return sharedPreferences.getString(AppApplication.getContext().getString(R.string.pref_auth_password), null);

    }

    public String getRefreshToken() {
        return sharedPreferences.getString(AppApplication.getContext().getString(R.string.pref_token_refresh), null);

    }

    public String refreshAccessToken() {

        refreshToken();
        // refresh token

        return this.getToken();
    }

    public void invalidate() {
        // get called when user become logged out
        // delete token and other user info
        // (i.e: email, password)
        // from the storage

        Context context = AppApplication.getContext();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_auth_password), null);
        editor.putString(context.getString(R.string.pref_auth_username), null);
        editor.putString(context.getString(R.string.pref_token_access), null);
        editor.putString(context.getString(R.string.pref_token_refresh), null);

        editor.commit();
    }
}
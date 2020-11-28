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

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    public AuthorizationInterceptor() {
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        Session session = Session.getInstance();
        Response mainResponse;
        Request mainRequest = chain.request();

        if (session.isLoggedIn()) {

            // add authentication header to each request if we are logged in
            Request.Builder builder = mainRequest.newBuilder().header("Authorization", session.getToken()).
                    method(mainRequest.method(), mainRequest.body());
           // Log.v("Authorization", "Intercept: " + session.getToken());

            // build request
            Request req = builder.build();
            mainResponse = chain.proceed(req);

            // logout on auth error
            if (mainResponse.code() == 401 || mainResponse.code() == 403) {
                session.invalidate();
                Log.v("Authorization", "Intercept: Logout forced");
            }

        } else {
            mainResponse = chain.proceed(chain.request());
        }

        return mainResponse;
    }

}
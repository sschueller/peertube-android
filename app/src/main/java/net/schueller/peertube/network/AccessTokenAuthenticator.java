package net.schueller.peertube.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class AccessTokenAuthenticator implements Authenticator {

    private static final String TAG = "ATAuthenticator";

    public AccessTokenAuthenticator() {
    }

    @Nullable
    @Override
    public Request authenticate(Route route, @NonNull Response response) {
        Session session = Session.getInstance();

        // check if we are using tokens
        final String accessToken = session.getToken();
        if (!isRequestWithAccessToken(response) || accessToken == null) {
            return null;
        }
        synchronized (this) {
            final String newAccessToken = session.getToken();
            // Access token is refreshed in another thread.
            if (!accessToken.equals(newAccessToken)) {
                Log.v(TAG, "Access token is refreshed in another thread");
                return newRequestWithAccessToken(response.request(), newAccessToken);
            }

            // do we have a refresh token?
            if (session.getRefreshToken() == null) {
                Log.v(TAG, "No refresh token available");
                return null;
            }

            Log.v(TAG, "refresh token: " + session.getRefreshToken());

            // Need to refresh an access token
            Log.v(TAG, "Need to refresh an access token");
            final String updatedAccessToken = session.refreshAccessToken();
            if (updatedAccessToken != null) {
                return newRequestWithAccessToken(response.request(), updatedAccessToken);
            }
            Log.v(TAG, "Refresh failed");
            return null;
        }
    }

    private boolean isRequestWithAccessToken(@NonNull Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", accessToken)
                .build();
    }
}
package net.schueller.peertube.network;

import net.schueller.peertube.model.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class TokenRenewInterceptor implements Interceptor {

    public TokenRenewInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // if 'x-auth-token' is available into the response header
        // save the new token into session.The header key can be
        // different upon implementation of backend.
        String newToken = response.header("x-auth-token");
        if (newToken != null) {

        }

        return response;
    }
}
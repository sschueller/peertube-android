package net.schueller.peertube.network;

import net.schueller.peertube.model.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    public AuthorizationInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mainResponse = chain.proceed(chain.request());
        if (mainResponse.code() == 401 || mainResponse.code() == 403) {

        }
        return mainResponse;
    }
}
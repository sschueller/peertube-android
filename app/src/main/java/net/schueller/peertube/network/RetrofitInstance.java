package net.schueller.peertube.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static String baseUrl;

    public static Retrofit getRetrofitInstance(String newBaseUrl) {
        if (retrofit == null || !newBaseUrl.equals(baseUrl)) {
            baseUrl = newBaseUrl;

            OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

            okhttpClientBuilder.addInterceptor(new TokenRenewInterceptor());
            okhttpClientBuilder.addInterceptor(new AuthorizationInterceptor());

            retrofit = new retrofit2.Retrofit.Builder()
                    .client(okhttpClientBuilder.build())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
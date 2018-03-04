package net.schueller.peertube.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static String baseUrl;

    public static Retrofit getRetrofitInstance(String newBaseUrl) {
        if (retrofit == null || !newBaseUrl.equals(baseUrl)) {
            baseUrl = newBaseUrl;
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
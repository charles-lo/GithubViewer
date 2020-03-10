package com.charles.githubviewer.api;

import android.util.Log;

import com.charles.autocachingconveter.GsonCacheableConverter;
import com.charles.githubviewer.data.BoxManager;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

class ServiceFactory {

    static APIService getInstance() {

        String baseUrl = "https://api.github.com/";


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonCacheableConverter.create((type, responseBody) -> {
                    if (responseBody instanceof Collection)
                        BoxManager.getStore().boxFor(type).put((ArrayList) responseBody);
                    else BoxManager.getStore().boxFor(type).put(responseBody);
                }))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideHttpClient())
                .build();

        return retrofit.create(APIService.class);
    }

    private static OkHttpClient provideHttpClient() {
        okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        String code = null;
        try {
            code = AESCrypt.decrypt("charles", "cuwen4YC/QDsxXJrET1bcc7opTBSFlU4CezvBoH31eI4j/jkmkgaYNTcRVfJAgH6");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        Log.d("charles111", code);

        String finalCode = code;
        builder.addInterceptor(chain -> {
            okhttp3.Request request = chain.request();
            Headers headers = request.headers().newBuilder().add("Authorization", " token " + finalCode).build();
            request = request.newBuilder().headers(headers).build();
            return chain.proceed(request);
        });

        return builder.build();
    }
}
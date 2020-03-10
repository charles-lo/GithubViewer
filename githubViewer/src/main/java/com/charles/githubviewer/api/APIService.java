package com.charles.githubviewer.api;

import com.charles.autocachingconveter.Cacheable;
import com.charles.githubviewer.api.model.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface APIService {

    @GET("users")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Single<List<User>> fetchAllUser(@QueryMap Map<String, Object> params);

    @Cacheable
    @GET("users/{name}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Single<User> fetchUser(@Path(value="name", encoded=true) String name);
}
package com.example.android_retrofit_signup_login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApInterface {
    @GET("api/{email}/{password}")
    Call<LoginModel> authenticate(@Path("email") String email, @Path("password") String password);
    @POST("api/{email}/{password}")
    Call<LoginModel> registration(@Path("email") String email, @Path("password") String password);

}

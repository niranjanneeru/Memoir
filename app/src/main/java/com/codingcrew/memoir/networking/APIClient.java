package com.codingcrew.memoir.networking;

import com.codingcrew.memoir.models.Journal;
import com.codingcrew.memoir.models.LoginUser;
import com.codingcrew.memoir.models.Task;
import com.codingcrew.memoir.models.User;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIClient {

    @Headers({"Connection:close", "Accept-Encoding:identity"})
    @POST("auth/token/login")
    Call<ResponseBody> login(@Body LoginUser user);

    @Headers("Connection:close")
    @POST("auth/users/")
    Call<ResponseBody> createUser(@Body User user);


    @GET("journal/category/j")
    Call<ArrayList<Journal>> getJournals(@Header("Authorization") String key);

    @POST("journal/")
    Call<ResponseBody> postJournalOrDiary(@Header("Authorization") String key, @Body Journal journal);

    @PUT("journal/{id}/")
    Call<Journal> changeJournalDiary(@Path("id") String id, @Header("Authorization") String key, @Body Journal journal);

    @DELETE("journal/{id}/")
    Call<ResponseBody> deleteJournal(@Path("id") String id, @Header("Authorization") String key);

    @GET("journal/category/d")
    Call<ArrayList<Journal>> getNotes(@Header("Authorization") String key);

    @GET("notes/")
    Call<ArrayList<Task>> getTask(@Header("Authorization") String key);

    @POST("notes/")
    Call<ResponseBody> pushTask(@Header("Authorization") String key, @Body Task task);

    @PUT("notes/{id}/")
    Call<Task> changeTask(@Path("id") String id, @Header("Authorization") String key, @Body Task task);

    @DELETE("notes/{id}/")
    Call<ResponseBody> deleteTask(@Path("id") String id, @Header("Authorization") String key);

}

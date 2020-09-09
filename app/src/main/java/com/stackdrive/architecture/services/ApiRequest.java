package com.stackdrive.architecture.services;

import com.stackdrive.architecture.models.Note;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiRequest {

    @GET("posts")
    Call<List<Note>> getPosts();
}
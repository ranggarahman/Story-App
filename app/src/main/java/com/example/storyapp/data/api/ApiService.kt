package com.example.storyapp.data.api

import com.example.storyapp.data.api.request.LoginRequest
import com.example.storyapp.data.api.request.RegisterRequest
import com.example.storyapp.data.api.response.LoginResponse
import com.example.storyapp.data.api.response.RegisterResponse
import com.example.storyapp.data.api.response.StoryDetailResponse
import com.example.storyapp.data.api.response.StoryListResponse
import com.example.storyapp.data.api.response.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/v1/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/v1/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("/v1/stories")
    suspend fun getStoryList(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String
    ): Response<StoryListResponse>

    @GET("/v1/stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") id : String? = null,
        @Header("Authorization") token : String
    ) : Response<StoryDetailResponse>

    @Multipart
    @POST("/v1/stories")
    suspend fun postStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
        @Header("Authorization") token: String
    ): Response<StoryUploadResponse>
}
package com.example.cardupdate



import retrofit2.http.Body
import retrofit2.http.POST


interface RetrofitAPI {
    @POST("app_api/api/messages/add_message")
    fun createPost(@Body dataModal: DataModel): retrofit2.Call<ResponseData?>

    @POST("app_api/api/info/add_new")
    fun addUser(@Body data: DataModelUserData): retrofit2.Call<ResponseData?>
}

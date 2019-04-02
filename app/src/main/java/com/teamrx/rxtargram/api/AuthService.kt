package com.teamrx.rxtargram.api

import com.google.firebase.auth.AuthResult
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


object AuthProvider {

    val serverIP = "http://172.26.117.229:8000"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10L, TimeUnit.SECONDS)
        .readTimeout(10L, TimeUnit.SECONDS)
        .writeTimeout(10L, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool())
        //.addNetworkInterceptor(BrunchOkHttpInterceptor(if (BuildConfig.DEBUG) BrunchOkHttpInterceptor.LOGLEVEL_FULL else BrunchOkHttpInterceptor.LOGLEVEL_NONE))
        .build()


    val retrofit = Retrofit.Builder()
//        .baseUrl("https://rxteam-sns.firebaseapp.com")
        .client(okHttpClient)
        .baseUrl(serverIP)
        .addConverterFactory(GsonConverterFactory.create())

        .build()



    init {

    }

    val service = retrofit.create(AuthService::class.java)

    fun verifyToken(token: String): Call<Auth> {
        return service.postAuth(token)
    }

}


interface AuthService {

    @POST("/verifyToken")
    fun postAuth(@Query("token") kakaoAccessToken: String): Call<Auth>
}

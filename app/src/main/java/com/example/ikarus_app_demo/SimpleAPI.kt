package com.example.ikarus_app_demo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//interface of client
interface SimpleAPI {
    @GET("chain_data")
    suspend fun getChain(): Response<Blockchain>

    @GET("chain")
    suspend fun getChain176(): Response<Blockchain176>

    @POST("geo_transaction")
    suspend fun geotransaction(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/api/v1/create")
    suspend fun createEmployee(@Body requestBody: RequestBody): Response<ResponseBody>

}
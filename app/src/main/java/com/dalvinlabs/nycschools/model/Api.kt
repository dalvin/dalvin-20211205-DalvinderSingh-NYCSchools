package com.dalvinlabs.nycschools.model

import com.google.gson.annotations.SerializedName
import dagger.Module
import dagger.Provides
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://data.cityofnewyork.us/resource/"

@Module
class ApiModule {

    @Provides
    fun providesApi(retrofitBuilder: Retrofit.Builder): Api =
        retrofitBuilder.baseUrl(BASE_URL).build().create(Api::class.java)

}

data class School(
    val dbn: String,
    @SerializedName("school_name")
    val name: String
)

data class Details(
    val dbn: String,
    @SerializedName("sat_critical_reading_avg_score")
    val reading: String,
    @SerializedName("sat_writing_avg_score")
    val writing: String,
    @SerializedName("sat_math_avg_score")
    val math: String,
)

data class SchoolWithDetails(
    val name: String,
    val reading: String,
    val writing: String,
    val math: String
)

interface Api {

    @GET("s3k6-pzi2.json")
    suspend fun getSchools(): Response<List<School>>

    @GET("f9bf-2cp4.json")
    suspend fun getDetails(): Response<List<Details>>
}
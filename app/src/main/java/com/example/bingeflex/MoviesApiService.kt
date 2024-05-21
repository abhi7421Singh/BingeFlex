package com.example.bingeflex

import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = TMDB_API_KEY,
        @Query("language") language: String = "en"
    ): MoviesListSchema

    companion object {
        const val TMDB_API_KEY = "50f227f3951cbd97bd72c86105f55b76"
    }
}

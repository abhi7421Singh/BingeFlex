package com.example.bingeflex

import com.example.bingeflex.model.MoviesSchema
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviesListSchema(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<MoviesSchema>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

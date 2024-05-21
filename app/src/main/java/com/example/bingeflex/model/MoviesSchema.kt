package com.example.bingeflex.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviesSchema(
    @Json(name = "adult")
    val adult: Boolean,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "genre_ids")
    val genreIds: List<Int>?,
    @Json(name = "genres")
    val genres: List<Genre>?,
    @Json(name = "media_type")
    val mediaType: String?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "imdb_id")
    val imdbId: String?,
    @Json(name = "original_language")
    val originalLanguage: String,
    @Json(name = "overview")
    val overview: String,
    @Json(name = "popularity")
    val popularity: Double,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "runtime")
    val runtime: Int?,
    @Json(name = "title")
    val title: String,
    @Json(name = "video")
    val video: Boolean,
    @Json(name = "vote_average")
    val voteAverage: Double,
    @Json(name = "vote_count")
    val voteCount: Int
)
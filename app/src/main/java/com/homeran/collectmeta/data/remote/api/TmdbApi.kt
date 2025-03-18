package com.homeran.collectmeta.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    suspend fun searchMovies(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "zh-CN",
        @Query("page") page: Int = 1
    ): TmdbSearchResponse

    @GET("search/tv")
    suspend fun searchTvShows(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "zh-CN",
        @Query("page") page: Int = 1
    ): TmdbSearchResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Header("Authorization") apiKey: String,
        @Query("movie_id") movieId: String,
        @Query("language") language: String = "zh-CN",
        @Query("append_to_response") appendToResponse: String = "credits"
    ): TmdbMovieDetails

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(
        @Header("Authorization") apiKey: String,
        @Query("tv_id") tvId: String,
        @Query("language") language: String = "zh-CN",
        @Query("append_to_response") appendToResponse: String = "credits"
    ): TmdbTvShowDetails
}

data class TmdbSearchResponse(
    val page: Int,
    val results: List<TmdbSearchResult>,
    val total_pages: Int,
    val total_results: Int
)

data class TmdbSearchResult(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String,
    val poster_path: String?,
    val release_date: String?,
    val first_air_date: String?
)

data class TmdbMovieDetails(
    val id: Int,
    val title: String,
    val original_title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String,
    val runtime: Int,
    val credits: TmdbCredits,
    val genres: List<TmdbGenre>
)

data class TmdbTvShowDetails(
    val id: Int,
    val name: String,
    val original_name: String,
    val overview: String,
    val poster_path: String?,
    val first_air_date: String,
    val last_air_date: String?,
    val number_of_seasons: Int,
    val number_of_episodes: Int,
    val credits: TmdbCredits,
    val genres: List<TmdbGenre>
)

data class TmdbCredits(
    val cast: List<TmdbCast>,
    val crew: List<TmdbCrew>
)

data class TmdbCast(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String?
)

data class TmdbCrew(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    val profile_path: String?
)

data class TmdbGenre(
    val id: Int,
    val name: String
) 
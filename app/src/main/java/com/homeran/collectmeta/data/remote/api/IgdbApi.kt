package com.homeran.collectmeta.data.remote.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IgdbApi {
    @POST("games")
    suspend fun searchGames(
        @Header("Authorization") apiKey: String,
        @Header("Client-ID") clientId: String,
        @Body query: String
    ): List<IgdbGame>

    @POST("games")
    suspend fun getGameDetails(
        @Header("Authorization") apiKey: String,
        @Header("Client-ID") clientId: String,
        @Body query: String
    ): List<IgdbGameDetails>
}

data class IgdbGame(
    val id: Int,
    val name: String,
    val cover: IgdbCover?,
    val first_release_date: Long?,
    val rating: Float?,
    val rating_count: Int?,
    val summary: String?
)

data class IgdbGameDetails(
    val id: Int,
    val name: String,
    val cover: IgdbCover?,
    val first_release_date: Long?,
    val rating: Float?,
    val rating_count: Int?,
    val summary: String?,
    val genres: List<IgdbGenre>?,
    val platforms: List<IgdbPlatform>?,
    val developers: List<IgdbCompany>?,
    val publishers: List<IgdbCompany>?,
    val similar_games: List<IgdbGame>?
)

data class IgdbCover(
    val id: Int,
    val url: String
)

data class IgdbGenre(
    val id: Int,
    val name: String
)

data class IgdbPlatform(
    val id: Int,
    val name: String
)

data class IgdbCompany(
    val id: Int,
    val name: String
) 
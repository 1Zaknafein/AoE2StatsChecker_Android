package com.example.aoestats

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Aoe2netAPI {

    @GET("api/leaderboard")
    fun getLeaderboardData(
        @Query("game") game : String,
        @Query("search") search : String,
        @Query("leaderboard_id") leaderboard_id : Int,
        @Query("count") count: Int)
    : Call<PlayerLookup>


    @GET("api/player/ratinghistory")
    fun getPlayerRatingHistory(
        @Query("game") game: String,
        @Query("leaderboard_id") leaderboard_id: Int,
        @Query("steam_id") steam_id: String,
        @Query("count") count : Int)
    :Call<PlayerRatingHistoryData>


    @GET("api/leaderboard")
    fun getPlayerDataSteamID(
        @Query("game") game : String,
        @Query("steam_id") search : String,
        @Query("leaderboard_id") leaderboard_id : Int)
    :Call<PlayerLookup>

}
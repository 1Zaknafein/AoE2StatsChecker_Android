package com.example.aoestats

import java.io.Serializable

data class Player(
    val clan: Any,
    val country: String,
    val drops: Int,
    val games: Int,
    val highest_rating: Int,
    val highest_streak: Int,
    val icon: Any,
    val last_match: Int,
    val last_match_time: Int,
    val losses: Int,
    val lowest_streak: Int,
    val name: String,
    val previous_rating: Int,
    val profile_id: Int,
    val rank: Int,
    val rating: Int,
    val steam_id: String,
    val streak: Int,
    val wins: Int
):Serializable
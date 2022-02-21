package com.example.aoestats

data class PlayerLookup(
    val count: Int,
    val leaderboard: List<Player>,
    val leaderboard_id: Int,
    val start: Int,
    val total: Int
)
package com.example.aoestats

data class PlayerRatingHistoryDataItem(
    val drops: Int,
    val num_losses: Int,
    val num_wins: Int,
    val rating: Int,
    val streak: Int,
    val timestamp: Int
)
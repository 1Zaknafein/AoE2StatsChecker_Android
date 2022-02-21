package com.example.aoestats

data class DataRM(
    var games: Int,
    var winrate: String,
    var wins: Int,
    var losses: Int,
    var rating: Int,
    var ratingHistory: List<Int>
)

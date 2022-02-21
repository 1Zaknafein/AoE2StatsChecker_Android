package com.example.aoestats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


// R    r
class ActivityResults : AppCompatActivity(), RecyclerViewLeaderboardsAdapter.OnItemClickListener {


    private val BASE_URL = "https://aoe2.net/"
    private val leaderboard_id = 4;
    lateinit var playerNameList : MutableList<PlayerNames>  // for recyclerview items
    lateinit var recycler : RecyclerView
    lateinit var playerList : MutableList<Player>      // list of players (data)

    private val count = 25

    private val TAG = "activityresults"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val name = intent.getStringExtra("name")

        getPlayersByName(name!!)
    }

    private fun getPlayersByName(name : String){
        playerNameList = mutableListOf()
        playerList = mutableListOf()

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Aoe2netAPI::class.java)


        GlobalScope.launch (Dispatchers.IO){

                    try{
                        val response = api.getLeaderboardData("aoe2de",name, leaderboard_id, count).awaitResponse()
                        if (response.isSuccessful){
                            val data = response.body()!!

                            data.leaderboard.sortedBy {it.rating }

                            for (player in data.leaderboard){
                                playerNameList.add(PlayerNames(player.name, ""+player.rating))
                                playerList.add(player)
                            }

                            withContext(Dispatchers.Main){
                                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE

                                recycler = findViewById(R.id.rvNames)
                                recycler.layoutManager = LinearLayoutManager(this@ActivityResults)
                                recycler.adapter = RecyclerViewLeaderboardsAdapter(playerNameList, this@ActivityResults)

                            }
                        }

                    }catch (e: SocketTimeoutException){
                        Toast.makeText(this@ActivityResults, "Could not connect to the API", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }


    }

    override fun onItemClick(position: Int) {

        val name = playerNameList.get(position).name
        val elo = playerNameList.get(position).elo
        val intent = Intent(this, PlayerActivity::class.java)

        for (player in playerList){

            if (player.name == name && player.rating == elo.toInt()){
                Log.i(TAG, "\nname: ${player.name}  \n" +
                        "games: ${player.games}  \n" +
                        "wins: ${player.wins} \n" +
                        "losses: ${player.losses}  \n" +
                        "country: ${player.country} \n" +
                        "highest rating: ${player.highest_rating} \n" +
                        "rating: ${player.rating} \n" +
                        "rank: ${player.rank} \n" +
                        "streak: ${player.streak} \n" +
                        "drops: ${player.drops} \n" +
                        "steamid: ${player.steam_id} \n")

                intent.putExtra("data", player)

                intent.putExtra("BASE_URL", BASE_URL)
                intent.putExtra("leaderboard_id", leaderboard_id)
                break
            }
        }
        playerList.clear()
        playerNameList.clear()
        startActivity(intent)
        finish()
    }



}
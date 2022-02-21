package com.example.aoestats

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import kotlin.math.roundToInt

class PlayerActivity : AppCompatActivity() {



    private val playerDataTeamRM = DataRM(0, "", 0,0, -1, listOf())
    private val playerDataRM = DataRM(0, "", 0,0, -1, listOf())
    var leaderboardID = -1

    private lateinit var player:Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        player = intent.extras?.get("data") as Player
        leaderboardID = intent.extras?.get("leaderboard_id") as Int


        if(leaderboardID==4){
            playerDataTeamRM.games = player.games
            playerDataTeamRM.wins = player.wins
            playerDataTeamRM.losses = player.losses
            playerDataTeamRM.rating = player.rating
            displayBasicStatistics(playerDataTeamRM)
        }else{
            playerDataRM.games = player.games
            playerDataRM.wins = player.wins
            playerDataRM.losses = player.losses
            playerDataRM.rating = player.rating
            displayBasicStatistics(playerDataRM)
        }


        // display player name and rating
        findViewById<TextView>(R.id.player_name).text = player.name


        displayPlayerRatingHistory(player.steam_id)
        displayGraph()
        setTypeChangeButton()


    }


    private fun setTypeChangeButton(){
        val btn = findViewById<Button>(R.id.btnLeaderboardType)

        if (leaderboardID==4)  btn.text = "Team RM"
        else        btn.text = "1v1 RM"

        btn.setOnClickListener {

            // change displayed data, check if is available first
            // if not, call API to get it
            // store data for reuse, for less API calls


            if (btn.text == "Team RM") {
                btn.text = "1v1 RM"
                leaderboardID = 3

                // if already got data from api display stats, else call api
                if (playerDataRM.rating>=0) displayBasicStatistics(playerDataRM) else getApiData()

            }
            else {
                btn.text = "Team RM"
                leaderboardID = 4
                if (playerDataTeamRM.rating>=0) displayBasicStatistics(playerDataTeamRM) else getApiData()
            }

            //finally update graph
            displayPlayerRatingHistory(player.steam_id)
        }
    }

    private fun getApiData(){
        GlobalScope.launch (Dispatchers.IO) {

            val api = Retrofit.Builder()
                .baseUrl(intent.extras?.get("BASE_URL") as String)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Aoe2netAPI::class.java)

            try {
                val response = api.getPlayerDataSteamID("aoe2de", player.steam_id, leaderboardID).awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!

                    // should be only one as steam id was passed

                    Log.i("stats", "size $data")
                    if (data.leaderboard.isEmpty()){
                        if (leaderboardID==4){
                            playerDataTeamRM.games = 0
                            playerDataTeamRM.wins = 0
                            playerDataTeamRM.losses = 0
                            playerDataTeamRM.rating = 0
                            playerDataTeamRM.winrate = "0%"
                            withContext(Dispatchers.Main){
                                displayBasicStatistics(playerDataTeamRM)
                            }
                        }else{
                            playerDataRM.games = 0
                            playerDataRM.wins = 0
                            playerDataRM.losses = 0
                            playerDataRM.rating = 0
                            playerDataRM.winrate = "0%"
                            withContext(Dispatchers.Main){
                                displayBasicStatistics(playerDataRM)
                            }
                        }

                    }else{

                        for (p in data.leaderboard) {

                            if (leaderboardID==4){  //team rm
                                playerDataTeamRM.games = p.games
                                playerDataTeamRM.wins = p.wins
                                playerDataTeamRM.losses = p.losses
                                playerDataTeamRM.rating = p.rating


                                //update new statistics on main thread
                                withContext(Dispatchers.Main){
                                    displayBasicStatistics(playerDataTeamRM)
                                }

                            }
                            else{
                                playerDataRM.games = p.games
                                playerDataRM.wins = p.wins
                                playerDataRM.losses = p.losses
                                playerDataRM.rating = p.rating

                                withContext(Dispatchers.Main){
                                    displayBasicStatistics(playerDataRM)
                                }
                            }
                        }
                    }

                }

            } catch (e: SocketTimeoutException) {
                Toast.makeText(
                    this@PlayerActivity,
                    "Could not connect to the API",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun displayBasicStatistics(player:DataRM){
        // display player data (rating, number of games, winrate, wins and losses

        if (player.winrate == ""){
            val w = player.wins.toFloat()
            val wr:Int = (w / (w+player.losses) * 100).roundToInt()
            val winrate= "$wr%"
            player.winrate = winrate
        }

        findViewById<TextView>(R.id.tv_player_games_value).text = player.games.toString()
        findViewById<TextView>(R.id.tv_player_winrate_value).text = player.winrate
        findViewById<TextView>(R.id.tv_player_wins_value).text = player.wins.toString()
        findViewById<TextView>(R.id.tv_player_losses_value).text = player.losses.toString()
        findViewById<TextView>(R.id.btn_PlayerRating).text = player.rating.toString()

    }

    private fun displayPlayerRatingHistory(steam_id: String){

        // if already have data, no need to call api, just update views
        if (leaderboardID==4 && playerDataTeamRM.ratingHistory.size>1){
            updateGraph()
        }
        else if (leaderboardID==3 && playerDataRM.ratingHistory.size > 1){
            updateGraph()
        }
        // else no data is yet found, call api
        else{
            val api = Retrofit.Builder()
                .baseUrl(intent.extras?.get("BASE_URL") as String)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Aoe2netAPI::class.java)

            GlobalScope.launch (Dispatchers.IO){
                try{
                    val response = api.getPlayerRatingHistory("aoe2de",leaderboardID, steam_id, 100).awaitResponse()
                    if (response.isSuccessful){
                        val data = response.body()!! // returns a list of data items

                        Log.i("test1", data.toString())
                        if (leaderboardID==4){
                            playerDataTeamRM.ratingHistory = data.map {it.rating}.reversed()
                        }
                        else{
                            playerDataRM.ratingHistory = data.map { it.rating }.reversed()
                        }

                        updateGraph()
                    }

                }catch (e: SocketTimeoutException){
                    Toast.makeText(this@PlayerActivity, "Could not connect to the API", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }


    private fun updateGraph() {

        val graph = findViewById<GraphView>(R.id.idGraphView)
        val series = LineGraphSeries<DataPoint>()

        graph.removeAllSeries()

        if (leaderboardID==4){
            for (i in playerDataTeamRM.ratingHistory.indices){
                series.appendData(DataPoint(i.toDouble(), playerDataTeamRM.ratingHistory[i].toDouble()), true, playerDataTeamRM.ratingHistory.size)
            }
        }
        else if(leaderboardID==3){
            for (i in playerDataRM.ratingHistory.indices){
                series.appendData(DataPoint(i.toDouble(), playerDataRM.ratingHistory[i].toDouble()), true, playerDataRM.ratingHistory.size)
            }
        }
        series.color = Color.LTGRAY
        graph.addSeries(series)

        if (leaderboardID==4) graph.title = "Team RM rating history" else graph.title = "1v1 RM rating history"
        graph.gridLabelRenderer.reloadStyles()

    }
    private fun displayGraph(){

        val graphView = findViewById<GraphView>(R.id.idGraphView)
        graphView.title = ""
        graphView.titleColor = Color.WHITE
        graphView.titleTextSize = 65f
        graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
        graphView.gridLabelRenderer.verticalLabelsColor = Color.WHITE
        graphView.gridLabelRenderer.labelsSpace
        graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphView.gridLabelRenderer.reloadStyles()
    }

}
package com.example.aoestats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLeaderboardsAdapter(
    private val itemsList : MutableList<PlayerNames>,
    private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerViewLeaderboardsAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_name, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = itemsList[position]
        holder.playerName.text = current.name
        holder.elo.text = current.elo
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }



    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val playerName : TextView = itemView.findViewById(R.id.tvTitle)
        val elo : TextView = itemView.findViewById(R.id.tvElo)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position : Int = adapterPosition
            if (position!= RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position : Int)
    }
}
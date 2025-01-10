package com.example.targil1.Utilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.targil1.R

class ScoreAdapter(
    private val scores: List<ScoreData>,
    private val onRecordClickListener: (ScoreData) -> Unit // Lambda to handle the click event
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerPosition: TextView = view.findViewById(R.id.playerPosition)
        val playerName: TextView = view.findViewById(R.id.playerName)
        val playerScore: TextView = view.findViewById(R.id.playerScore)
        val trophyIcon: ImageView = view.findViewById(R.id.trophyIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val playerScore = scores[position]

        holder.playerPosition.text = (position + 1).toString()
        holder.playerName.text = playerScore.name
        holder.playerScore.text = "Score: ${playerScore.score}"
/*
        when (position) {
            0 -> {
                holder.trophyIcon.setImageResource(R.drawable.ic_gold_trophy)
                holder.trophyIcon.visibility = View.VISIBLE
            }
            1 -> {
                holder.trophyIcon.setImageResource(R.drawable.ic_silver_trophy)
                holder.trophyIcon.visibility = View.VISIBLE
            }
            2 -> {
                holder.trophyIcon.setImageResource(R.drawable.ic_bronze_trophy)
                holder.trophyIcon.visibility = View.VISIBLE
            }
            else -> {
                holder.trophyIcon.visibility = View.GONE
            }
        }
*/
        // Set an OnClickListener on each item to call the callback function when clicked
        holder.itemView.setOnClickListener {
            onRecordClickListener(playerScore)
        }
    }

    override fun getItemCount(): Int {
        return scores.size
    }
}


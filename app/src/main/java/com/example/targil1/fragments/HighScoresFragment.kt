package com.example.targil1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.targil1.Interfaces.Callback_HighScoreItemClicked
import com.example.targil1.R
import com.example.targil1.Utilities.ScoreAdapter

class HighScoresFragment : Fragment() {

    private lateinit var scores_LST_scores: RecyclerView
    private lateinit var scoresAdapter: ScoreAdapter
    var highScoreItemClicked: Callback_HighScoreItemClicked? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)
        findViews(view)
        initViews()
        return view
    }

    private fun findViews(view: View) {
        scores_LST_scores = view.findViewById(R.id.scores_LST_scores)
    }

    private fun initViews() {
        val topScores = SharedPreferencesManager.getInstance().getTopScores(10)

        scoresAdapter = ScoreAdapter(topScores) { score ->
            highScoreItemClicked?.highScoreItemClicked(score.latitude, score.longitude)
        }

        scores_LST_scores.layoutManager = LinearLayoutManager(context)
        scores_LST_scores.adapter = scoresAdapter
    }
}
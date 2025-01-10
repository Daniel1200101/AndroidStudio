package com.example.targil1.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.targil1.R
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.LocationManagerHelper
import com.example.targil1.Utilities.ScoreData
import com.example.targil1.Utilities.SignalManager
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ScoreActivity : AppCompatActivity() {

    private lateinit var score_LBL_score: MaterialTextView
    private lateinit var nameEditText: EditText
    private lateinit var submitButton: Button
    private var playerScore: Int = 0 // Declare the score variable globally
    private var playerName: String = ""
    private lateinit var locationManager: LocationManagerHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        // Initialize the views
        findViews()
        initViews()

        // Check if the score is in the top scores
        if (isScoreInTopScores(playerScore)) {
            score_LBL_score.text = "You are in the top scores! $playerScore"
        } else {
            score_LBL_score.text = "Nice try! Your score: $playerScore"
        }
        // Handle the submit button click
        submitButton.setOnClickListener {
            playerName = nameEditText.text.toString()
            if (playerName.isNotBlank()) {
                savePlayerScore()
                changeActivity()
            } else {
                SignalManager.getInstance().toast("Please enter your name")
            }
        }
    }

    private fun findViews() {
        score_LBL_score = findViewById(R.id.score_LBL_score)
        nameEditText = findViewById(R.id.nameEditText)
        submitButton = findViewById(R.id.submitButton)
    }

    private fun initViews() {
        // Extract the score passed from the previous activity
        initManagers()
        playerScore = intent.getIntExtra(Constants.BundleKeys.SCORE_KEY, 0)
    }

    private fun initManagers() {
        SharedPreferencesManager.init(this)
        LocationManagerHelper.init(this)
        locationManager = LocationManagerHelper.getInstance()
    }

    private fun isScoreInTopScores(score: Int): Boolean {
        val topScores: List<ScoreData> = SharedPreferencesManager.getInstance().getTopScores()

        // Ensure the list is sorted in descending order of scores
        val sortedScores = topScores.sortedByDescending { it.score }

        // Check if the new score qualifies
        return if (sortedScores.size < 10) {
            // If less than 10 scores, the new score qualifies automatically
            true
        } else {
            // Compare the new score with the lowest score in the top 10
            score > sortedScores.last().score
        }
    }

    private fun savePlayerScore() {
        try {
            val location = locationManager.getCurrentLocationForMap()
            if (location.first != 0.0 && location.second != 0.0) {
                val scoreData = ScoreData(
                    name = playerName,
                    score = playerScore,
                    latitude = location.first,
                    longitude = location.second
                )
                SharedPreferencesManager.getInstance().saveScore(scoreData)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        val newLocation =
                            LocationManagerHelper.getInstance().getCurrentLocationForMap()
                        val scoreData = ScoreData(
                            name = playerName,
                            score = playerScore,
                            latitude = newLocation.first,
                            longitude = newLocation.second
                        )
                        SharedPreferencesManager.getInstance().saveScore(scoreData)
                    } catch (e: Exception) {
                        SignalManager.getInstance().toast("Could not save location with score")
                    }
                }, 1000)
            }
        } catch (e: SecurityException) {
            SignalManager.getInstance().toast("Missing location permission")
        } catch (e: Exception) {
            SignalManager.getInstance().toast("Could not get location")
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, TopScoresActivity::class.java)
        startActivity(intent) // Start the TopScoresActivity
        finish() // Close the ScoreActivity
    }
}

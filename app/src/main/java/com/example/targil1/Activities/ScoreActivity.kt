package com.example.targil1.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.targil1.R
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.LocationManagerHelper
import com.example.targil1.Utilities.ScoreData
import com.example.targil1.Utilities.SignalManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView

class ScoreActivity : AppCompatActivity() {

    private lateinit var gameOverText: MaterialTextView
    private lateinit var scoreText: MaterialTextView // TextView to display the score
    private lateinit var positionText: MaterialTextView // TextView to display the position
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

        // Check if the score is in the top scores and show the score message
        val scorePosition = getScorePosition(playerScore)
        val gameOverMessage = "Game Over!"
        gameOverText.text = gameOverMessage

        // Update the score and position text
        scoreText.text = "Your score: $playerScore"
        positionText.text = "Your position: $scorePosition"

        animateTextAppearance(gameOverText) // Add animation

        // Handle the submit button click
        submitButton.setOnClickListener {
            playerName = nameEditText.text.toString()
            if (playerName.isNotBlank()) {
                savePlayerScore()
                showSuccessSnackbar("Score saved! Changing activity...")
                changeActivity()
            } else {
                showWarningSnackbar("Please enter your name")
            }
        }
    }

    private fun findViews() {
        gameOverText = findViewById(R.id.gameOverMessage)
        scoreText = findViewById(R.id.yourScore) // Assuming your XML has this ID
        positionText = findViewById(R.id.yourPosition) // Assuming your XML has this ID
        nameEditText = findViewById(R.id.nameEditText)
        submitButton = findViewById(R.id.submitButton)
    }

    private fun initViews() {
        // Extract the score passed from the previous activity
        initManagers()
        playerScore = intent.getIntExtra(Constants.BundleKeys.SCORE_KEY, 0)
    }

    private fun animateTextAppearance(textView: TextView) {
        textView.alpha = 0f
        textView.animate()
            .alpha(1f)
            .setDuration(1000) // 1-second fade-in animation
            .start()
    }

    private fun showWarningSnackbar(message: String) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.warningColor))
            .setTextColor(ContextCompat.getColor(this, android.R.color.white))
            .show()
    }

    private fun showSuccessSnackbar(message: String) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.successColor))
            .setTextColor(ContextCompat.getColor(this, android.R.color.white))
            .show()
    }

    private fun initManagers() {
        SharedPreferencesManager.init(this)
        LocationManagerHelper.init(this)
        locationManager = LocationManagerHelper.getInstance()
    }

    private fun getScorePosition(score: Int): Int {
        val topScores: List<ScoreData> = SharedPreferencesManager.getInstance().getTopScores()

        // Ensure the list is sorted in descending order of scores
        val sortedScores = topScores.sortedByDescending { it.score }

        // Get the position of the player's score
        return sortedScores.indexOfFirst { it.score <= score } + 1
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


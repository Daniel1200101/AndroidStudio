package com.example.targil1.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.targil1.R
import com.example.targil1.Utilities.BackgroundMusicPlayer
import com.example.targil1.Utilities.LocationManagerHelper
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    // Buttons
    private lateinit var startButton: MaterialButton
    private lateinit var settingsButton: MaterialButton
    private lateinit var topScoresButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        findViews()
        initViews()
        initBackgroundMusic()
    }

    override fun onResume() {
        super.onResume()
        BackgroundMusicPlayer.getInstance().playMusic()
    }

    override fun onPause() {
        super.onPause()
        BackgroundMusicPlayer.getInstance().stopMusic()

    }

    private fun initBackgroundMusic()
    {
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.menu_sound)
        BackgroundMusicPlayer.getInstance().playMusic()
    }

    private fun findViews() {
        // Initially set the buttons.
        startButton = findViewById(R.id.btn_start)
        settingsButton = findViewById(R.id.btn_settings)
        topScoresButton = findViewById(R.id.btn_top_scores)
    }

    private fun initViews() {

        startButton.setOnClickListener { view: View -> buttonClicked("start") }
        settingsButton.setOnClickListener { view: View -> buttonClicked("settings") }
        topScoresButton.setOnClickListener { view: View -> buttonClicked("topScores") }

        // refreshUI()
    }

    private fun buttonClicked(btn: String) {
        when (btn) {
            "start" -> {
                changeActivity(MainActivity::class.java) // Navigate to MainActivity
            }

            "settings" -> {
                changeActivity(SettingsActivity::class.java) // Navigate to SettingsActivity
            }

            "topScores" -> {
                changeActivity(TopScoresActivity::class.java) // Navigate to RecordsActivity
            }

        }

    }
    private fun changeActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish()
    }

}
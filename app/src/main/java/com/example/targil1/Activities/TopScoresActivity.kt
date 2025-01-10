package com.example.targil1.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.targil1.Interfaces.Callback_HighScoreItemClicked
import com.example.targil1.R
import com.example.targil1.enums.Difficulty
import com.example.targil1.fragments.HighScoresFragment
import com.example.targil1.fragments.MapFragment


class TopScoresActivity : AppCompatActivity(), Callback_HighScoreItemClicked {

    private lateinit var highScoresFragment: HighScoresFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_scores)
        Log.d("ScoresActivity", "onCreate started")

        findViews()
        initViews()

        try {
            initFragments()
            Log.d("ScoresActivity", "Fragments initialized successfully")
        } catch (e: Exception) {
            Log.e("ScoresActivity", "Error initializing fragments", e)
            e.printStackTrace()
        }
    }

    private fun findViews() {
        btnBack = findViewById(R.id.btnBack)
    }

    private fun initViews() {
        btnBack.setOnClickListener { navigateToMenu() }
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initFragments() {
        highScoresFragment = HighScoresFragment()
        mapFragment = MapFragment()

        highScoresFragment.highScoreItemClicked = this

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_list, highScoresFragment)
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()
    }

    override fun highScoreItemClicked(lat: Double, lon: Double) {
        mapFragment.focusOnLocation(lat, lon)
    }
}



package com.example.targil1

import android.app.Application
import com.example.targil1.Utilities.BackgroundMusicPlayer
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.SignalManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize utilities
        SignalManager.init(applicationContext)
        BackgroundMusicPlayer.init(this)
        SharedPreferencesManager.init(applicationContext)
        removeAllPlayerScores()

    }


    fun removeAllPlayerScores() {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance()
        sharedPreferencesManager.clearScores() // Removes the player scores
    }

}

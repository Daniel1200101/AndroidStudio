package com.example.targil1.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.targil1.R
import com.example.targil1.Utilities.BackgroundMusicPlayer
import com.google.android.material.button.MaterialButton

class MenuActivity: AppCompatActivity() {

    // Buttons
    private lateinit var MENU_BTN_START: MaterialButton
    private lateinit var MENU_BTN_SETTINGS: MaterialButton
    private lateinit var MENU_BTN_RECORDS: MaterialButton

    //private var buttonsMovement: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        BackgroundMusicPlayer.init(this)
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.menu_sound)
        findViews()
        initViews()
    }
    override fun onResume() {
        super.onResume()
        BackgroundMusicPlayer.getInstance().playMusic()
    }
    override fun onPause() {
        super.onPause()
        BackgroundMusicPlayer.getInstance().pauseMusic()
    }

    private fun findViews() {
        // Initially set the buttons.
        MENU_BTN_START = findViewById(R.id.btn_start)
        MENU_BTN_SETTINGS = findViewById(R.id.btn_settings)
        MENU_BTN_RECORDS = findViewById(R.id.btn_record)
    }
    private fun initViews() {

        MENU_BTN_START.setOnClickListener { view: View -> buttonClicked("start") }
        MENU_BTN_SETTINGS.setOnClickListener { view: View -> buttonClicked("settings") }
        MENU_BTN_RECORDS.setOnClickListener { view: View -> buttonClicked("records") }

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
          /*  "records" -> {
                changeActivity(RecordsActivity::class.java) // Navigate to RecordsActivity
            }
        */
        }

    }
    private fun changeActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish()
    }

}
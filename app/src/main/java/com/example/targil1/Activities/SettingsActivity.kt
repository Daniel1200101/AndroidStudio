package com.example.targil1.Activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.targil1.R
import com.example.targil1.enums.Difficulty
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {

    // UI Components
    private lateinit var switchControlMode: Switch
    private lateinit var btnBack: ImageButton
    private lateinit var btnEasy: MaterialButton
    private lateinit var btnMedium: MaterialButton
    private lateinit var btnHard: MaterialButton

    // State Variables
    private var selectedDifficultyButton: MaterialButton? = null
    private var buttonsMovement: Boolean = false
    private var selectedDifficulty: Difficulty = Difficulty.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Load saved preferences
        loadPreferences()

        // Find views
        findViews()

        // Initialize views with current settings
        initViews()
    }

    private fun loadPreferences() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Load control mode preference
        buttonsMovement = sharedPreferences.getBoolean("buttonsMovement", true)

        // Load and validate difficulty preference
        val difficultyValue = sharedPreferences.getString("selectedDifficulty", "EASY")
        selectedDifficulty = try {
            Difficulty.valueOf(difficultyValue ?: "EASY")
        } catch (e: IllegalArgumentException) {
            Difficulty.EASY
        }
    }

    private fun findViews() {
        switchControlMode = findViewById(R.id.switchControlMode)
        btnBack = findViewById(R.id.btnBack)
        btnEasy = findViewById(R.id.btnEasy)
        btnMedium = findViewById(R.id.btnMedium)
        btnHard = findViewById(R.id.btnHard)
    }

    private fun initViews() {
        // Set the switch state
        switchControlMode.isChecked = buttonsMovement

        // init the selected difficulty
        initSelectedDifficultyUI()

        // Set event listeners
        btnBack.setOnClickListener { navigateToMenu() }
        btnEasy.setOnClickListener { onDifficultySelected(btnEasy, Difficulty.EASY) }
        btnMedium.setOnClickListener { onDifficultySelected(btnMedium, Difficulty.MEDIUM) }
        btnHard.setOnClickListener { onDifficultySelected(btnHard, Difficulty.HARD) }

        switchControlMode.setOnCheckedChangeListener { _, isChecked ->
            onControlModeChanged(isChecked)
        }
    }

    private fun initSelectedDifficultyUI() {
        // Reset all buttons to the default color
        listOf(btnEasy, btnMedium, btnHard).forEach {
            it.backgroundTintList = ColorStateList.valueOf(getColor(R.color.light_gray))
        }

        // Highlight the currently selected difficulty without triggering a toast
        when (selectedDifficulty) {
            Difficulty.EASY -> {
                btnEasy.backgroundTintList = ColorStateList.valueOf(getColor(R.color.red))
                selectedDifficultyButton = btnEasy
            }

            Difficulty.MEDIUM -> {
                btnMedium.backgroundTintList = ColorStateList.valueOf(getColor(R.color.red))
                selectedDifficultyButton = btnMedium
            }

            Difficulty.HARD -> {
                btnHard.backgroundTintList = ColorStateList.valueOf(getColor(R.color.red))
                selectedDifficultyButton = btnHard
            }
        }
    }


    private fun onDifficultySelected(button: MaterialButton, difficulty: Difficulty) {
        // Update UI for selected difficulty
        selectedDifficultyButton?.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.light_gray))
        button.backgroundTintList = ColorStateList.valueOf(getColor(R.color.red))
        selectedDifficultyButton = button

        // Update selected difficulty state
        selectedDifficulty = difficulty

        val feedbackMessage = when (difficulty) {
            Difficulty.EASY -> "Easy selected. The obstacles will move slow."
            Difficulty.MEDIUM -> "Medium selected. The obstacles will move at regular speed."
            Difficulty.HARD -> "Hard selected. The obstacles will move fast."
        }
        Toast.makeText(this, feedbackMessage, Toast.LENGTH_SHORT).show()
    }

    private fun onControlModeChanged(isChecked: Boolean) {
        buttonsMovement = isChecked
        val mode = if (isChecked) "buttons" else "phone movement"
        Toast.makeText(this, "Using $mode to control character.", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMenu() {
        savePreferences()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun savePreferences() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("buttonsMovement", buttonsMovement)
        editor.putString("selectedDifficulty", selectedDifficulty.name) // Save enum name directly
        editor.apply()
    }
}

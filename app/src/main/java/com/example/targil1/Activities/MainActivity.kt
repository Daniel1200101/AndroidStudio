package com.example.targil1.Activities

import com.example.targil1.enums.Directions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.example.targil1.GameManager
import com.example.targil1.GameMechanics
import com.example.targil1.Interfaces.TiltCallback
import com.example.targil1.R
import com.example.targil1.Utilities.BackgroundMusicPlayer
import com.example.targil1.Utilities.SignalManager
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.LocationManagerHelper
import com.example.targil1.Utilities.ScoreData
import com.example.targil1.enums.Difficulty
import com.example.targil1.Utilities.TiltDetector
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {

    // Main character
    private lateinit var main_IMG_mainCharacter: Array<AppCompatImageView>

    // Buttons
    private lateinit var main_BTN_left: AppCompatImageButton
    private lateinit var main_BTN_right: AppCompatImageButton

    // Hearts array
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    // Score
    private lateinit var main_LBL_score: MaterialTextView

    // Obstacles
    private lateinit var obstacles_grid: LinearLayout

    // Coins
    private lateinit var coin_grid: LinearLayout
    // lives
    private lateinit var lives_grid: LinearLayout

    // Game Mechanics for movement
    private lateinit var gameMechanics: GameMechanics

    // Game manager for logic
    private lateinit var gameManager: GameManager

    private lateinit var tiltDetector: TiltDetector


    private var gameJob: Job? = null
    private val totalRows = 8
    private val totalColumns = 5

    private lateinit var sharedPreferences: SharedPreferences

    /// Game default settings
    private var buttonsMovement: Boolean = true // Default value
    private var gameDifficulty: Difficulty = Difficulty.EASY
    private var choosenGameDelay: Long = Constants.Timer.SLOW_DELAY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        buttonsMovement = sharedPreferences.getBoolean("buttonsMovement", false)
        gameDifficulty =
            Difficulty.valueOf(sharedPreferences.getString("selectedDifficulty", "EASY") ?: "EASY")

        BackgroundMusicPlayer.getInstance().stopMusic() // Stop the previous background music
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.tom_and_jerry_chase)
        BackgroundMusicPlayer.getInstance().playMusic()
        initTiltDetector()
        findViews() // Connecting the views of the main activity to their images.
        initViews()
        startGame() // This starts the game and obstacle generation
    }

    override fun onResume() {
        super.onResume()
        if (!buttonsMovement) {
            tiltDetector.start()
        } else {
            tiltDetector.stop() // Optionally stop the tilt detector if buttonsMovement is false
        }
        BackgroundMusicPlayer.getInstance().playMusic()
        startGame()
    }

    override fun onStop() {
        super.onStop()
        tiltDetector.stop()
        SignalManager.getInstance().cancelToast()
        BackgroundMusicPlayer.getInstance().stopMusic()
        stopGame()
    }

    override fun onPause() {
        super.onPause()
        BackgroundMusicPlayer.getInstance().stopMusic()
        stopGame()
    }

    private fun stopGame() {
        gameJob?.cancel()
        gameJob = null
    }

    private fun findViews() {
        main_IMG_mainCharacter = arrayOf(
            findViewById(R.id.main_IMG_jerry1),
            findViewById(R.id.main_IMG_jerry2),
            findViewById(R.id.main_IMG_jerry3),
            findViewById(R.id.main_IMG_jerry4),
            findViewById(R.id.main_IMG_jerry5)

        )
        // Initially set the buttons.
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )
        obstacles_grid = findViewById(R.id.obstcale_grid_layout)
        coin_grid = findViewById(R.id.coin_grid_layout)
        lives_grid = findViewById(R.id.lives_grid_layout)
    }

    private fun initViews() {
        gameManager = GameManager(this)
        gameMechanics = GameMechanics()
        initGameMechanics()
        initializeGrid(obstacles_grid)
        initializeGrid(coin_grid)
        initializeGrid(lives_grid)
        calculateDelay(gameDifficulty)
        initButtons()
        main_LBL_score.text = gameManager.score.toString()
        refreshUI()
    }

    private fun initButtons() {
        if (buttonsMovement) {
            main_BTN_left.setOnClickListener { view: View -> gameMechanics.moveCharacter(Directions.LEFT) }
            main_BTN_right.setOnClickListener { view: View -> gameMechanics.moveCharacter(Directions.RIGHT) }
        } else {
            main_BTN_left.visibility = View.INVISIBLE
            main_BTN_right.visibility = View.INVISIBLE
        }

    }
    private fun initGameMechanics() {
        gameMechanics.setGameManagerListener(gameManager)
        gameMechanics.setMainCharacterViews(main_IMG_mainCharacter)
        gameMechanics.setMainObstaclesViews(obstacles_grid)
        gameMechanics.setMainCoinsViews(coin_grid)
        gameMechanics.setMainLivesViews(lives_grid)
    }

    private fun initializeGrid(grid: LinearLayout) {
        for (i in 0 until totalRows) {
            for (j in 0 until totalColumns) {
                gameMechanics.hideImage(i, j, grid)
            }
        }
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun tiltCharacter(direction: Directions) {
                    gameMechanics.moveCharacter(direction)
                }
            }
        )
    }
    private fun calculateDelay(gameDifficulty: Difficulty) {
        when (gameDifficulty) {
            Difficulty.EASY -> {
                choosenGameDelay = Constants.Timer.SLOW_DELAY
            }

            Difficulty.MEDIUM -> {
                choosenGameDelay = Constants.Timer.MEDIUM_DELAY
            }

            Difficulty.HARD -> {
                choosenGameDelay = Constants.Timer.FAST_DELAY
            }
        }

    }
    private fun startGame() {
        if (gameJob == null) { // Ensure no duplicate game jobs are created
            gameJob = lifecycleScope.launch {
                while (true) {
                    refreshUI()
                    delay((choosenGameDelay))
                }
            }
        }
    }
    private fun refreshUI() { // Checking the status of game .
        gameMechanics.updateObstacles()
        gameMechanics.updateCoins()
        gameMechanics.updateLives()
        var heartPositionRemoved:Int = 0
        if (gameManager.isGameOver) { // Lost!
            changeActivity(gameManager.score)
        }
        if (gameManager.shouldCreateObstacle()) {
            gameMechanics.createObstacle()
        }
        if (gameManager.shouldCreateCoin()) {
            gameMechanics.createCoin()
        }
        if (gameManager.shouldCreateLife()) {
            gameMechanics.createLife()
        }
        gameManager.tick()
        main_LBL_score.text = gameManager.score.toString()
        if (gameManager.hits != 0) {
            heartPositionRemoved = main_IMG_hearts.size - gameManager.hits
            main_IMG_hearts[heartPositionRemoved].visibility =
                View.INVISIBLE
        }
        if (gameManager.lifeBonus != 0) {
            main_IMG_hearts[heartPositionRemoved].visibility =
                View.VISIBLE
            gameManager.setLifeBonus(0)
            gameManager.setHits(gameManager.hits-1)

        }
    }
    private fun changeActivity(score: Int) {
        val intent = Intent(this, ScoreActivity::class.java)
        var bundle = Bundle()
        bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}


package com.example.targil1.Activities

import android.annotation.SuppressLint
import com.example.targil1.enums.Directions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.example.targil1.CharacterMovement
import com.example.targil1.GameManager
import com.example.targil1.GridManager
import com.example.targil1.Interfaces.TiltCallback
import com.example.targil1.R
import com.example.targil1.Utilities.BackgroundMusicPlayer
import com.example.targil1.Utilities.SignalManager
import com.example.targil1.Utilities.Constants
import com.example.targil1.enums.Difficulty
import com.example.targil1.Utilities.TiltDetector
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {

    // Main character
    private lateinit var mainCharacterArray: Array<AppCompatImageView>

    // Buttons
    private lateinit var leftButton: AppCompatImageButton
    private lateinit var rightButton: AppCompatImageButton

    // Hearts array
    private lateinit var heartsArray: Array<AppCompatImageView>

    // Score
    private lateinit var scoreLabel: MaterialTextView

    // Grid objects
    private lateinit var obstaclesGrid: LinearLayout
    private lateinit var coinsGrid: LinearLayout
    private lateinit var livesGrid: LinearLayout

    // Managers
    private lateinit var characterMovement: CharacterMovement
    private lateinit var gridManager: GridManager
    private lateinit var gameManager: GameManager

    // Tilt sensor
    private lateinit var tiltDetector: TiltDetector

    private var gameJob: Job? = null

    private lateinit var sharedPreferences: SharedPreferences

    /// Game default settings
    private var buttonsMovement: Boolean = true // Default value
    private var gameDifficulty: Difficulty = Difficulty.EASY
    private var chosenGameDelay: Long = Constants.Timer.SLOW_DELAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("GAME_SETTINGS", MODE_PRIVATE)
        buttonsMovement = sharedPreferences.getBoolean("buttonsMovement", true)
        gameDifficulty =
            Difficulty.valueOf(sharedPreferences.getString("selectedDifficulty", "EASY") ?: "EASY")

        initBackGroundMusic()
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
        mainCharacterArray = arrayOf(
            findViewById(R.id.main_IMG_jerry1),
            findViewById(R.id.main_IMG_jerry2),
            findViewById(R.id.main_IMG_jerry3),
            findViewById(R.id.main_IMG_jerry4),
            findViewById(R.id.main_IMG_jerry5)

        )
        // Initially set the buttons.
        leftButton = findViewById(R.id.main_BTN_left)
        rightButton = findViewById(R.id.main_BTN_right)
        scoreLabel = findViewById(R.id.main_LBL_score)
        heartsArray = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )
        obstaclesGrid = findViewById(R.id.obstcale_grid_layout)
        coinsGrid = findViewById(R.id.coin_grid_layout)
        livesGrid = findViewById(R.id.lives_grid_layout)
    }

    private fun initBackGroundMusic() {
        BackgroundMusicPlayer.getInstance().stopMusic() // Stop the previous background music
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.tom_and_jerry_chase)
        BackgroundMusicPlayer.getInstance().playMusic()
    }
    @SuppressLint("SetTextI18n")
    private fun initViews() {
        gameManager = GameManager(this)
        initGridManager()
        initCharacterMovement()
        calculateDelay(gameDifficulty)
        initButtons()
        scoreLabel.text = gameManager.score.toString()
        refreshUI()
    }
    private fun initButtons() {
        if (buttonsMovement) {
            leftButton.setOnClickListener { view: View -> characterMovement.moveCharacter(Directions.LEFT) }
            rightButton.setOnClickListener { view: View -> characterMovement.moveCharacter(Directions.RIGHT) }
        } else {
            leftButton.visibility = View.INVISIBLE
            rightButton.visibility = View.INVISIBLE
        }
    }
    private fun initGridManager() {
        gridManager = GridManager()
        gridManager.setGridManagerListener(gameManager)
        gridManager.setMainGameViews(obstaclesGrid,coinsGrid,livesGrid)
    }
    private fun initCharacterMovement() {
        characterMovement = CharacterMovement()
        characterMovement.setCharacterViews(mainCharacterArray)
        characterMovement.setCharacterMovementListener(gridManager)
    }
    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun tiltCharacter(direction: Directions) {
                    characterMovement.moveCharacter(direction)
                }
            }
        )
    }

    private fun calculateDelay(gameDifficulty: Difficulty) {
        when (gameDifficulty) {
            Difficulty.EASY -> {
                chosenGameDelay = Constants.Timer.SLOW_DELAY
            }

            Difficulty.MEDIUM -> {
                chosenGameDelay = Constants.Timer.MEDIUM_DELAY
            }

            Difficulty.HARD -> {
                chosenGameDelay = Constants.Timer.FAST_DELAY
            }
        }

    }

    private fun startGame() {
        if (gameJob == null) { // Ensure no duplicate game jobs are created
            gameJob = lifecycleScope.launch {
                while (true) {
                    refreshUI()
                    delay((chosenGameDelay))
                }
            }
        }
    }

    private fun refreshUI() { // Checking the status of game .
        gridManager.updateObstacles()
        gridManager.updateCoins()
        gridManager.updateLives()
        var heartPositionRemoved: Int = 0
        if (gameManager.isGameOver) { // Lost!
            changeActivity(gameManager.score)
        }
        if (gameManager.shouldCreateObstacle()) {
            gridManager.createObstacle()
        }
        if (gameManager.shouldCreateCoin()) {
            gridManager.createCoin()
        }
        if (gameManager.shouldCreateLife()) {
            gridManager.createLife()
        }
        gameManager.tick()
        scoreLabel.text = gameManager.score.toString()
        if (gameManager.hits != 0) {
            heartPositionRemoved = heartsArray.size - gameManager.hits
            heartsArray[heartPositionRemoved].visibility =
                View.INVISIBLE
        }
        if (gameManager.lifeBonus != 0) {
            heartsArray[heartPositionRemoved].visibility =
                View.VISIBLE
            gameManager.setLifeBonus(0)
            gameManager.setHits(gameManager.hits - 1)
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


package com.example.targil1.Activities

import com.example.targil1.Utilities.Directions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.example.targil1.GameManager
import com.example.targil1.GameMechanics
import com.example.targil1.R
import com.example.targil1.Signal
import com.example.targil1.Utilities.Constants
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity(){

    // Main character
    private lateinit var main_IMG_mainCharacter: Array<AppCompatImageView>
    // Buttons
    private lateinit var main_BTN_left: AppCompatImageButton
    private lateinit var main_BTN_right: AppCompatImageButton
    // Hearts array
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    // Obstacles
    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>
    // Game Mechanics for movement
    private lateinit var gameMechanics: GameMechanics
    // Game manager for logic
    private lateinit var gameManager: GameManager

    private lateinit var main_LBL_score: MaterialTextView

    private val handler = Handler(Looper.getMainLooper())




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews() // Connecting the views of the main activity to their images.
        Signal.init(this)
        gameMechanics = GameMechanics()
        gameManager = GameManager()
        gameMechanics.setGameManagerListener(gameManager)
        gameMechanics.setMainCharacterViews(main_IMG_mainCharacter)
        gameMechanics.setMainObstaclesViews(main_IMG_obstacles)
        initViews()
        startGame() // This starts the game and obstacle generation
    }
    private fun findViews() {
        main_IMG_mainCharacter = arrayOf(
            findViewById(R.id.main_IMG_jerry1),
            findViewById(R.id.main_IMG_jerry2),
            findViewById(R.id.main_IMG_jerry3)
        )
        initializeMainCharacter();
        // Initially set the buttons.
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )
        main_IMG_obstacles = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_tom1),
                findViewById(R.id.main_IMG_tom2),
                findViewById(R.id.main_IMG_tom3)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_tom4),
                findViewById(R.id.main_IMG_tom5),
                findViewById(R.id.main_IMG_tom6)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_tom7),
                findViewById(R.id.main_IMG_tom8),
                findViewById(R.id.main_IMG_tom9)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_tom10),
                findViewById(R.id.main_IMG_tom11),
                findViewById(R.id.main_IMG_tom12)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_tom13),
                findViewById(R.id.main_IMG_tom14),
                findViewById(R.id.main_IMG_tom15)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_tom16),
                findViewById(R.id.main_IMG_tom17),
                findViewById(R.id.main_IMG_tom18)
            )
        )
        initializeObstacles();
    }
    private fun initializeObstacles() {
        for (row in main_IMG_obstacles) {
            for (view in row) {
                view.visibility = View.INVISIBLE
            }
        }
    }
    private fun initializeMainCharacter() {
        main_IMG_mainCharacter[0].visibility = View.INVISIBLE // First image
        main_IMG_mainCharacter[1].visibility = View.VISIBLE // Second image
        main_IMG_mainCharacter[2].visibility = View.INVISIBLE // Third image
    }

    private fun initViews() {
        main_LBL_score.text = gameManager.score.toString()
        main_BTN_left.setOnClickListener { view: View -> directionClicked(Directions.LEFT) }
        main_BTN_right.setOnClickListener { view: View -> directionClicked(Directions.RIGHT) }
        refreshUI()
    }
    private fun directionClicked(expected: Directions) {
        gameMechanics.moveCharacter(expected)
    }
  /*
   private fun startGame() {
        lifecycleScope.launch {
            while (!gameManager.isGameOver) {
                refreshUI()
               delay(Constants.Timer.DELAY)
            }
        }
    }
    */
  /*
private fun refreshUI() { // Checking the status of game .
 gameMechanics.updateObstacles()
  if (gameManager.isGameOver) { // Lost!
      Log.d("Game Status", "Game Over! " + gameManager.score)
      changeActivity("Game Over!", gameManager.score)
  } else { // Ongoing game:
      if (gameManager.obstacleFrequencies()) {
          gameMechanics.createObstacle()
      }
      gameManager.tick()
      main_LBL_score.text = gameManager.score.toString()
      if (gameManager.hits != 0 ) {
          main_IMG_hearts[main_IMG_hearts.size - gameManager.hits].visibility =
              View.INVISIBLE

      }

  }
}
*/
    private fun startGame() {
        lifecycleScope.launch {
            while (true) {
                refreshUI()
                delay(Constants.Timer.DELAY)
            }
        }
    }
    private fun refreshUI() { // Checking the status of game .
        gameMechanics.updateObstacles()
        if (gameManager.isGameOver) { // Lost!
            Log.d("Game Status", "Game Over! " + gameManager.score)
            Signal.getInstance().toast("New game!")
            for (view in main_IMG_hearts) {
                    view.visibility = View.VISIBLE
            }
            gameManager.setHits(0)
        }
            if (gameManager.obstacleFrequencies()) {
                gameMechanics.createObstacle()
            }
            gameManager.tick()
            main_LBL_score.text = gameManager.score.toString()
            if (gameManager.hits != 0 ) {
                main_IMG_hearts[main_IMG_hearts.size - gameManager.hits].visibility =
                    View.INVISIBLE

            }

        }



     private fun changeActivity(message: String, score: Int) {
         val intent = Intent(this, ScoreActivity::class.java)
         var bundle = Bundle()
         bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
         bundle.putString(Constants.BundleKeys.STATUS_KEY, message)
         intent.putExtras(bundle)
         startActivity(intent)
         finish()
     }
}


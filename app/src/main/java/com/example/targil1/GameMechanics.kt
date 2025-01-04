package com.example.targil1

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.example.targil1.Interfaces.GameManagerListener
import com.example.targil1.Utilities.SignalManager
import com.example.targil1.enums.Directions
import kotlin.random.Random

class GameMechanics() {

    // Variable for the view references
    lateinit var mainCharacterImages: Array<AppCompatImageView>
        private set
    lateinit var obstacles_matrix: LinearLayout
        private set
    lateinit var coins_matrix: LinearLayout
        private set
    lateinit var lives_matrix: LinearLayout
        private set
    private var gameManagerListener: GameManagerListener? = null

    private val totalRows = 8
    private val totalColumns = 5

    var characterCol: Int = 2
        private set
    var characterRow: Int = totalRows - 1
        private set
    var obstacleCol: Int = 0
        private set
    var obstacleRow: Int = 0
        private set
    var coinCol: Int = 0
        private set
    var coinRow: Int = 0
        private set
    var lifeCol: Int = 0
        private set
    var lifeRow: Int = 0
        private set

    // Function to set the views in the GameMechanics class
    fun setMainCharacterViews(mainCharacterImages: Array<AppCompatImageView>) {
        this.mainCharacterImages = mainCharacterImages
    }

    fun setMainObstaclesViews(obstaclesGrid: LinearLayout) {
        this.obstacles_matrix = obstaclesGrid
    }

    fun setMainCoinsViews(coinsGrid: LinearLayout) {
        this.coins_matrix = coinsGrid
    }
    fun setMainLivesViews(livesGrid: LinearLayout) {
        this.lives_matrix = livesGrid
    }

    fun setGameManagerListener(listener: GameManagerListener) {
        gameManagerListener = listener
    }
    // Function to move character
    fun moveCharacter(direction: Directions) {
        when (direction) {
            Directions.LEFT -> moveLeft()
            Directions.RIGHT -> moveRight()
        }
        updateCharacterVisibility()
    }

    private fun moveLeft() {
        if (characterCol > 0) {
            updateCharacterPosition(characterCol, characterCol - 1)
            characterCol--
        }
    }

    private fun moveRight() {
        if (characterCol < mainCharacterImages.size - 1) {
            updateCharacterPosition(characterCol, characterCol + 1)
            characterCol++
        }
    }

    private fun updateCharacterPosition(oldColumn: Int, newColumn: Int) {
        mainCharacterImages[oldColumn].visibility = View.INVISIBLE
        mainCharacterImages[newColumn].visibility = View.VISIBLE
    }

    private fun updateCharacterVisibility() {
        for (i in mainCharacterImages.indices) {
            mainCharacterImages[i].visibility =
                if (i == characterCol) View.VISIBLE else View.INVISIBLE
        }
    }

    // grids functions
    fun createObstacle() {
        val column = Random.nextInt(totalColumns)
        obstacleCol = column
        showImage(0, column, obstacles_matrix)
    }

    fun createCoin() {
        val column = Random.nextInt(totalColumns)
        coinCol = column
        if (obstacleCol != coinCol)
            showImage(0, column, coins_matrix)
    }
    fun createLife() {
        val column = Random.nextInt(totalColumns)
        lifeCol = column
        if (lifeCol != coinCol && lifeCol!=obstacleCol)
            showImage(0, column, lives_matrix)
    }

    fun updateObstacles() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, obstacles_matrix)
                if (cell.visibility == View.VISIBLE) {
                    obstacleRow = i
                    obstacleCol = j
                    if (checkCollision(obstacleRow, obstacleCol)) {
                        gameManagerListener?.obstacleCollisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, obstacles_matrix)
                        } else {
                            gameManagerListener?.increaseScore()
                        }
                    }
                    hideImage(i, j, obstacles_matrix)
                }
            }
        }
    }
    fun updateCoins() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, coins_matrix)
                if (cell.visibility == View.VISIBLE) {
                    coinRow = i
                    coinCol = j
                    if (checkCollision(coinRow, coinCol)) {
                        gameManagerListener?.coinCollisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, coins_matrix)
                        }
                    }
                    hideImage(i, j,coins_matrix)
                }
            }
        }
    }
    fun updateLives() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, lives_matrix)
                if (cell.visibility == View.VISIBLE) {
                    lifeRow = i
                    lifeCol = j
                    if (checkCollision(lifeRow, lifeCol)) {
                        gameManagerListener?.lifeCollisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, lives_matrix)
                        }
                    }
                    hideImage(i, j,lives_matrix)
                }
            }
        }
    }
    fun checkCollision(row: Int, col: Int): Boolean {
        // Check if the given cell is directly above the character (one row before the character's row)
        return row == characterRow - 1 && col == characterCol
    }

    fun getCellImage(row: Int, column: Int, grid: LinearLayout): AppCompatImageView {
        val layout = grid.getChildAt(row) as LinearLayout
        return layout.getChildAt(column) as AppCompatImageView
    }

    fun showImage(row: Int, column: Int, grid: LinearLayout) {
        val image = getCellImage(row, column, grid)
        image.visibility = View.VISIBLE
    }

    fun hideImage(row: Int, column: Int, grid: LinearLayout) {
        val image = getCellImage(row, column, grid)
        image.visibility = View.INVISIBLE
    }
}





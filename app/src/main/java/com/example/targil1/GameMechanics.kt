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
    lateinit var obstaclesImages: LinearLayout
        private set
    private var gameManagerListener: GameManagerListener? = null

    private val totalRows = 8
    private val totalColumns = 5

    var characterCol: Int = 2
        private set
    var characterRow: Int = totalRows - 1
    var obstaclesCol: Int = 0
        private set
    private var obstaclesRow: Int = 0
        private set


    // Function to set the views in the GameMechanics class
    fun setMainCharacterViews(mainCharacterImages: Array<AppCompatImageView>) {
        this.mainCharacterImages = mainCharacterImages
    }

    fun setMainObstaclesViews(obstaclesImages: LinearLayout) {
        this.obstaclesImages = obstaclesImages
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

    // Obstacles
    fun createObstacle() {
        val column = Random.nextInt(totalColumns)
        obstaclesCol = column
        showImage(0, column, obstaclesImages)
        Log.d("updateObstacles", "Obstacle created")
    }

    fun updateObstacles() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, obstaclesImages)
                if (cell.visibility == View.VISIBLE) {
                    obstaclesCol = j
                    obstaclesRow = i
                    if (checkCollision()) {
                        gameManagerListener?.collisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, obstaclesImages)
                        } else {
                            gameManagerListener?.increaseScore()
                        }
                    }
                    hideImage(i, j, obstaclesImages)
                }
            }
        }
    }
    fun checkCollision(): Boolean {
        // Check if the obstacle is directly above the character (one row before the character's row)
        if (obstaclesRow == characterRow - 1 && obstaclesCol == characterCol) {
            // Collision detected, return true
            return true
        }
        // No collision, return false
        return false
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





package com.example.targil1

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.targil1.Interfaces.GameManagerListener
import com.example.targil1.Utilities.Directions
import kotlin.random.Random

class GameMechanics() {

    // Variable for the view references
    lateinit var mainCharacterImages: Array<AppCompatImageView>
        private set
    lateinit var obstaclesImages: Array<Array<AppCompatImageView>>
        private set
    private var gameManagerListener: GameManagerListener? = null
    private val totalRows = 6
    private val totalColumns = 3
    var characterCol: Int = 1
        private set
    var obstaclesCol: Int = 0
        private set
    var obstaclesRow: Int = 0
        private set
    var characterRow: Int = 5

    // Function to set the views in the GameMechanics class
    fun setMainCharacterViews(mainCharacterImages: Array<AppCompatImageView>) {
        this.mainCharacterImages = mainCharacterImages
    }

    fun setMainObstaclesViews(obstaclesImages: Array<Array<AppCompatImageView>>) {
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
            mainCharacterImages[i].visibility = if (i == characterCol) View.VISIBLE else View.INVISIBLE
        }
    }
    fun createObstacle() {
        val column = Random.nextInt(totalColumns)
        obstaclesCol=column
        obstaclesImages[0][column].visibility = View.VISIBLE
        Log.d("updateObstacles", "Obstacle created")

    }
    fun updateObstacles() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                if (obstaclesImages[i][j].visibility == View.VISIBLE) {
                    obstaclesCol=j
                    obstaclesRow=i
                    if ((i == totalRows - 2) && characterCol == obstaclesCol) { //Last row and equal to character col
                        toastAndVibrate()
                        gameManagerListener?.gotHit()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            obstaclesImages[i + 1][j].visibility = View.VISIBLE
                        } else {
                            gameManagerListener?.increaseScore()
                        }
                    }
                    obstaclesImages[i][j].visibility = View.INVISIBLE
                }
            }
        }
    }
    private fun toastAndVibrate() {
        Signal.getInstance().toast("Meow!")
        Signal.getInstance().vibrate()
    }
}


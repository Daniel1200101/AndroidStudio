package com.example.targil1

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.targil1.Interfaces.CharacterMovementListener
import com.example.targil1.Utilities.Constants
import com.example.targil1.enums.Directions

class CharacterMovement()
{
    private lateinit var mainCharacterImages: Array<AppCompatImageView>

    private var characterCol: Int = Constants.CharacterStartPosition.CHARACTER_COL
    private var characterRow: Int = Constants.CharacterStartPosition.CHARACTER_ROW

    private var characterMovementListener: CharacterMovementListener? = null

    fun setCharacterViews(mainCharacterImages: Array<AppCompatImageView>) {
        this.mainCharacterImages = mainCharacterImages
        updateCharacterVisibility()
    }
    fun setCharacterMovementListener(listener: CharacterMovementListener) {
        characterMovementListener = listener
    }

    // Function to move character
    fun moveCharacter(direction: Directions) {
        when (direction) {
            Directions.LEFT -> moveLeft()
            Directions.RIGHT -> moveRight()
        }
        // Notify listener after movement is complete
        characterMovementListener?.onCharacterMoved(characterRow, characterCol)
        updateCharacterVisibility()

    }
    private fun moveLeft() {
        if (characterCol > 0) {
            updateCharacterPosition(characterCol, characterCol - 1)
            mainCharacterImages[characterCol-1].scaleX = -1f
            characterCol--
        }
    }

    private fun moveRight() {
        if (characterCol < mainCharacterImages.size - 1) {
            updateCharacterPosition(characterCol, characterCol + 1)
            mainCharacterImages[characterCol+1].scaleX = 1f
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
}

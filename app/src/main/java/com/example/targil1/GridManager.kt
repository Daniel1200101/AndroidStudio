package com.example.targil1

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.example.targil1.Interfaces.CharacterMovementListener
import com.example.targil1.Interfaces.GridManagerListener
import com.example.targil1.Utilities.Constants
import kotlin.random.Random

class GridManager : CharacterMovementListener {
    lateinit var obstaclesGrid: LinearLayout
        private set
    lateinit var coinsGrid: LinearLayout
        private set
    lateinit var livesGrid: LinearLayout
        private set
    private var gridManagerListener: GridManagerListener? = null


    private val totalRows = Constants.Grid.TOTAL_ROWS
    private val totalColumns = Constants.Grid.TOTAL_COLUMNS

    private var characterCol: Int = Constants.CharacterStartPosition.CHARACTER_COL
    private var characterRow: Int = Constants.CharacterStartPosition.CHARACTER_ROW
    private var obstacleCol: Int = 0
    private var obstacleRow: Int = 0
    private var coinCol: Int = 0
    private var coinRow: Int = 0
    private var lifeCol: Int = 0
    private var lifeRow: Int = 0

    fun setMainGameViews(
        obstaclesGrid: LinearLayout,
        coinsGrid: LinearLayout,
        livesGrid: LinearLayout
    ) {
        this.obstaclesGrid = obstaclesGrid
        this.coinsGrid = coinsGrid
        this.livesGrid = livesGrid

        initializeGrid(this.obstaclesGrid)
        initializeGrid(this.coinsGrid)
        initializeGrid(this.livesGrid)
    }

    fun setGridManagerListener(listener: GridManagerListener) {
        gridManagerListener = listener
    }

    // grids functions
    fun createObstacle() {
        val column = Random.nextInt(totalColumns)
        obstacleCol = column
        showImage(0, column, obstaclesGrid)
    }

    fun updateGridObjects() {
        updateObstacles()
        updateCoins()
        updateLives()
    }
    fun createCoin() {
        val column = Random.nextInt(totalColumns)
        coinCol = column
        if (coinCol != obstacleCol)
            showImage(0, column, coinsGrid)
    }

    fun createLife() {
        val column = Random.nextInt(totalColumns)
        lifeCol = column
        if (lifeCol != coinCol && lifeCol != obstacleCol)
            showImage(0, column, livesGrid)
    }

    fun updateObstacles() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, obstaclesGrid)
                if (cell.visibility == View.VISIBLE) {
                    obstacleRow = i
                    obstacleCol = j
                    if (checkCollision(obstacleRow, obstacleCol)) {
                        gridManagerListener?.obstacleCollisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, obstaclesGrid)
                        } else {
                            gridManagerListener?.increaseScore()
                        }
                    }
                    hideImage(i, j, obstaclesGrid)
                }
            }
        }
    }

    fun updateCoins() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, coinsGrid)
                if (cell.visibility == View.VISIBLE) {
                    coinRow = i
                    coinCol = j
                    if (checkCollision(coinRow, coinCol)) {
                        gridManagerListener?.coinCollisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, coinsGrid)
                        }
                    }
                    hideImage(i, j, coinsGrid)
                }
            }
        }
    }

    fun updateLives() {
        for (i in totalRows - 2 downTo 0) {
            for (j in 0 until totalColumns) {
                val cell = getCellImage(i, j, livesGrid)
                if (cell.visibility == View.VISIBLE) {
                    Log.d("position", "livesCol: $i, livesRow: $j") // Debug log
                    Log.d("position", "charRow: $characterRow,charCol: $characterCol") // Debug log
                    lifeRow = i
                    lifeCol = j
                    if (checkCollision(lifeRow, lifeCol)) {
                        gridManagerListener?.lifeCollisionDetected()
                    } else {
                        if (i != totalRows - 2) {
                            // not last obstacle's row
                            showImage(i + 1, j, livesGrid)
                        }
                    }
                    hideImage(i, j, livesGrid)
                }
            }
        }
    }

    private fun checkCollision(row: Int, col: Int): Boolean {
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

    override fun onCharacterMoved(row: Int, col: Int) {
        characterRow = row
        characterCol = col
        Log.d("characterPosition", "characterRow=$characterRow ,characterCol=$characterCol")
    }

    private fun initializeGrid(grid: LinearLayout) {
        for (i in 0 until Constants.Grid.TOTAL_ROWS) {
            for (j in 0 until Constants.Grid.TOTAL_COLUMNS) {
                hideImage(i, j, grid)
            }
        }
    }
}

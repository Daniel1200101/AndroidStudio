package com.example.targil1

import android.util.Log
import com.example.targil1.Interfaces.GameManagerListener
import com.example.targil1.Utilities.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


    class GameManager(  private val lifeCount: Int = 3):GameManagerListener {
        var count: Int = 0
            private set
        var score: Int = 0
            private set
        var hits: Int = 0
            private set
        val isGameOver: Boolean
            get() = hits == lifeCount

        override fun gotHit() {
            hits++
            Log.d("TAG", "Got hit: $hits")
        }
         fun setHits(newHits: Int) {
            hits=newHits
        }
        override fun increaseScore() {
            score+= Constants.GameLogic.distance_per_obstacle
        }
        fun obstacleFrequencies(): Boolean {
            return count % 4== 0
        }
        fun tick() {
            count++
        }
    }
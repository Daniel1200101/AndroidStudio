package com.example.targil1

import android.content.Context
import android.util.Log
import com.example.targil1.Interfaces.GameManagerListener
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.SignalManager
import com.example.targil1.Utilities.SingleSoundPlayer


class GameManager(context: Context, private val lifeCount: Int = 3):GameManagerListener {
        var count: Int = 0
            private set
        var score: Int = 0
            private set
        var hits: Int = 0
            private set
        val isGameOver: Boolean
            get() = hits == lifeCount
        private var obstacleFrequency: Int = 4
        private val singleSoundPlayer = SingleSoundPlayer(context)
         fun setHits(newHits: Int) {
            hits=newHits
        }
         override fun collisionDetected() {
                toastAndVibrate()
                playCollisionSound()
                hits++
                Log.d("TAG", "Got hit: $hits")
        }
        override fun increaseScore() {
            score+= Constants.GameLogic.distance_per_obstacle
            isScoreReachedMileStone()
        }
        fun shouldCreateObstacle(): Boolean {
            return count % obstacleFrequency== 0
        }
        fun tick() {
            count++
        }
        private var lastMilestoneReached = 0 // To track the last milestone reached and avoid duplicate toasts

        private fun isScoreReachedMileStone() {
            // Change frequency only when reaching a new milestone
            when {
                score >= 2 * Constants.Score.SCORE_MILESTONE && lastMilestoneReached < 2 -> {
                    obstacleFrequency = 2
                    SignalManager.getInstance().toast("${2 * Constants.Score.SCORE_MILESTONE} milestone reached, level up!")
                    lastMilestoneReached = 2 // Update to second milestone
                }
                score >= Constants.Score.SCORE_MILESTONE && lastMilestoneReached < 1 -> {
                    obstacleFrequency = 3
                    SignalManager.getInstance().toast("${Constants.Score.SCORE_MILESTONE} milestone reached, level up!")
                    lastMilestoneReached = 1 // Update to first milestone
                }
                else -> {
                    obstacleFrequency = 4  // Default frequency
                }
            }
        }
    private fun toastAndVibrate() {
        SignalManager.getInstance().toast("Meow!")
        SignalManager.getInstance().vibrate()
    }
    private fun playCollisionSound() {
        singleSoundPlayer.playSound(R.raw.cartoon_kitty_meow) // Play the collision sound using SingleSoundPlayer
    }
}


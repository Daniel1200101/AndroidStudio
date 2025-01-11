package com.example.targil1

import android.content.Context
import android.util.Log
import com.example.targil1.Interfaces.GridManagerListener
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.SignalManager
import com.example.targil1.Utilities.SingleSoundPlayer

class GameManager(context: Context, private val lifeCount: Int = 3) : GridManagerListener {
    var count: Int = 0
        private set
    var score: Int = 0
        private set
    var hits: Int = 0
        private set
    var lifeBonus: Int = 0
        private set
    val isGameOver: Boolean
        get() = hits == lifeCount

    private var obstacleFrequency: Int = 4
    private var coinFrequency: Int = 25
    private var lifeFrequency: Int = 50

    private val singleSoundPlayer = SingleSoundPlayer(context)

    fun setHits(newHits: Int) {
        hits = newHits
    }
    fun setLifeBonus(newLife: Int) {
        lifeBonus = newLife
    }

    override fun obstacleCollisionDetected() {
        toastAndVibrate()
        playCollisionSound()
        hits++
        Log.d("TAG", "Got hit: $hits")
    }

    override fun coinCollisionDetected() {
        playCoinSound()
        score += Constants.ScoreLogic.COIN_SCORE
    }
    override fun lifeCollisionDetected() {
        playLifeSound()
        if (hits > 0) {
            lifeBonus=1
        }
    }
    override fun increaseScore() {
        score += Constants.ScoreLogic.OBSTACLE_SCORE
        isScoreReachedMileStone()
    }

    fun shouldCreateObstacle(): Boolean {
        return count % obstacleFrequency == 0
    }

    fun shouldCreateCoin(): Boolean {
        return count % coinFrequency == 0
    }

    fun shouldCreateLife(): Boolean {
        return count % lifeFrequency == 0
    }

    fun tick() {
        count++
    }

    private var lastMilestoneReached =
        0 // To track the last milestone reached and avoid duplicate toasts

    private fun isScoreReachedMileStone() {
        // Change frequency only when reaching a new milestone
        when {
            score >= 5 * Constants.ScoreLogic.SCORE_MILESTONE && lastMilestoneReached < 2 -> {
                obstacleFrequency = 2
                SignalManager.getInstance()
                    .toast("${5 * Constants.ScoreLogic.SCORE_MILESTONE} milestone reached, level up!")
                lastMilestoneReached = 2 // Update to second milestone
            }

            score >= Constants.ScoreLogic.SCORE_MILESTONE && lastMilestoneReached < 1 -> {
                obstacleFrequency = 3
                SignalManager.getInstance()
                    .toast("${Constants.ScoreLogic.SCORE_MILESTONE} milestone reached, level up!")
                lastMilestoneReached = 1 // Update to first milestone
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

    private fun playCoinSound() {
        singleSoundPlayer.playSound(R.raw.coin_sound)
    }
    private fun playLifeSound() {
        singleSoundPlayer.playSound(R.raw.item_pick_up)
    }
}


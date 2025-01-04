package com.example.targil1.Interfaces

interface GameManagerListener {
    fun obstacleCollisionDetected()  // Called when got hit
    fun coinCollisionDetected()  // Called when got hit
    fun lifeCollisionDetected()  // Called when got hit
    fun increaseScore()  // Called when the score changes
}
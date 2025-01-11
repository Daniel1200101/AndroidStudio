package com.example.targil1.Interfaces

interface GridManagerListener {
    fun obstacleCollisionDetected()  // Called when got hit
    fun coinCollisionDetected()  // Called when got hit
    fun lifeCollisionDetected()  // Called when got hit
    fun increaseScore()  // Called when the score changes
}
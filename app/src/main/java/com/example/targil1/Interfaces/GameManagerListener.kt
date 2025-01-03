package com.example.targil1.Interfaces

interface GameManagerListener {
    fun collisionDetected()  // Called when got hit
    fun increaseScore()  // Called when the score changes
}
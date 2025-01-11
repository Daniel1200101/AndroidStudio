package com.example.targil1.Utilities

class Constants {
    object ScoreLogic {
        const val OBSTACLE_SCORE = 10
        const val COIN_SCORE = 50
        const val SCORE_MILESTONE= 500
    }
    object BundleKeys{
        const val SCORE_KEY :String = "SCORE_KEY"
        const val STATUS_KEY :String = "STATUS_KEY"
    }
    object Timer{
        const val SLOW_DELAY = 500L
        const val MEDIUM_DELAY = 250L
        const val FAST_DELAY = 200L
    }
    companion object {
        const val SP_FILE = "GAME_PREFS"
        const val SP_KEY_SCORES = "SCORES"
    }
    object TopScores{
        const val MAX_LIST = 5
    }
    object Grid{
        const val TOTAL_ROWS = 8
        const val TOTAL_COLUMNS = 5
    }
    object CharacterStartPosition{
        const val CHARACTER_ROW = 7
        const val CHARACTER_COL = 2
    }
    object Location{
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

}
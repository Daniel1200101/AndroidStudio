import android.content.Context
import android.content.SharedPreferences
import com.example.targil1.Utilities.Constants
import com.example.targil1.Utilities.ScoreData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SharedPreferencesManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.SP_FILE,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun init(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }

        fun getInstance(): SharedPreferencesManager {
            return instance ?: throw IllegalStateException(
                "SharedPreferencesManager must be initialized by calling init(context) before use."
            )
        }
    }

    fun saveScore(scoreData: ScoreData) {
        val scores = getScores().toMutableList()
        scores.add(scoreData)

        val scoresJson = gson.toJson(scores)
        sharedPreferences.edit().putString(Constants.SP_KEY_SCORES, scoresJson).apply()
    }

    fun getScores(): List<ScoreData> {
        val scoresJson = sharedPreferences.getString(Constants.SP_KEY_SCORES, "[]")
        val type = object : TypeToken<List<ScoreData>>() {}.type
        return gson.fromJson(scoresJson, type) ?: listOf()
    }

    fun getTopScores(limit: Int = 10): List<ScoreData> {
        return getScores()
            .sortedByDescending { it.score }
            .take(limit)
    }
    fun clearScores() {
        sharedPreferences.edit().remove(Constants.SP_KEY_SCORES).apply()
    }
}
package marcin.malocha.koleo.net

import marcin.malocha.koleo.net.results.StationsResult
import org.json.JSONArray
import java.net.URL

object NetUtils {
    private const val API_URL = "https://koleo.pl/api/android/v1/"

    fun getStations(): StationsResult {
        val stationsString = URL(API_URL + "stations.json").readText()
        val stations = JSONArray(stationsString)
        return StationsResult(true, "", stations)
    }
}
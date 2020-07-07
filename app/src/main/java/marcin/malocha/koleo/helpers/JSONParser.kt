package marcin.malocha.koleo.helpers

import marcin.malocha.koleo.model.Station
import org.json.JSONObject

object JSONParser {
    fun JSONToStation(jsonObject: JSONObject): Station {
        val ibnr = jsonObject.getString("ibnr")
        return Station(
            jsonObject.getInt("id"),
            jsonObject.getString("name"),
            jsonObject.getString("name_slug"),
            jsonObject.getString("latitude"),
            jsonObject.getString("longitude"),
            jsonObject.getInt("hits"),
            if (!ibnr.equals("null", true)) Integer.parseInt(ibnr) else null
        )
    }
}
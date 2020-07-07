package marcin.malocha.koleo.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import marcin.malocha.koleo.R
import marcin.malocha.koleo.adapters.viewsHolders.StationsViewHolder
import marcin.malocha.koleo.model.AppSystem
import marcin.malocha.koleo.model.Station

class StationsAdapter(private var stations: ArrayList<Station>, private val context: Context): RecyclerView.Adapter<StationsViewHolder>() {
    var allStations: ArrayList<Station> = stations

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationsViewHolder {
        return StationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.station_row, parent, false))
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    override fun onBindViewHolder(holder: StationsViewHolder, position: Int) = holder.bind(stations[position])

    fun refresh(stations: ArrayList<Station>) {
        this.stations = stations
        this.allStations = stations
        notifyDataSetChanged()
    }

    fun filter(value: String) {
        stations = ArrayList(allStations.filter { x -> x.name.contains(value, true) || x.nameSlug.contains(value, true) || AppSystem.selectedStations.contains(x) })
        notifyDataSetChanged()
    }

    fun addSelectedStation(station: Station) {
        AppSystem.selectedStations.add(station)
        if (AppSystem.selectedStations.size == 1) {
            val view = (context as Activity).first_selected_station
            view.visibility = View.VISIBLE
            view.first_station_label.text = station.name
        }
        else {
            val view = (context as Activity).second_selected_station
            view.visibility = View.VISIBLE
            view.second_station_label.text = station.name
        }
    }

    fun removeSelectedStation(station: Station) {
        AppSystem.selectedStations.remove(station)

        if (AppSystem.selectedStations.size == 0) {
            (context as Activity).first_selected_station.visibility = View.GONE
        }
        else {
            (context as Activity).second_selected_station.visibility = View.GONE
        }
    }
}
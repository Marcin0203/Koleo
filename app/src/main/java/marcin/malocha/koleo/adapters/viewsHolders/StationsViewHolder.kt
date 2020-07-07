package marcin.malocha.koleo.adapters.viewsHolders

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.station_row.view.*
import marcin.malocha.koleo.MainActivity
import marcin.malocha.koleo.R
import marcin.malocha.koleo.model.AppSystem
import marcin.malocha.koleo.model.Station

class StationsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(station: Station) {
        val latitude = station.latitude
        val longitude = station.longitude
        itemView.name.text = station.name
        itemView.name_slug.text = station.nameSlug
        itemView.coordinates.text = String.format("$latitude, $longitude")
        itemView.hits.text = station.hits.toString()
        itemView.ibnr.text = if (station.ibnr == null) itemView.context.getText(R.string.epmty_string) else station.ibnr.toString()

        setBackground(AppSystem.selectedStations.contains(station))

        itemView.setOnClickListener {
            when {
                AppSystem.selectedStations.contains(station) -> {
                    setBackground(false)
                    (itemView.context as MainActivity).stationsAdapter?.removeSelectedStation(station)
                }
                AppSystem.selectedStations.size < 2 -> {
                    setBackground(true)
                    (itemView.context as MainActivity).stationsAdapter?.addSelectedStation(station)
                }
                else -> {
                    Toast.makeText(itemView.context, itemView.context.getText(R.string.selected_max_stations), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setBackground(isSelected: Boolean) {
        itemView.background = if (isSelected) itemView.context.getDrawable(R.drawable.rounded_selected_item) else itemView.context.getDrawable(R.drawable.rounded_white_background)
        itemView.selected_image.alpha = if (isSelected) 1f else 0f
    }
}
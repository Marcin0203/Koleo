package marcin.malocha.koleo

import android.content.Context
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import marcin.malocha.koleo.adapters.StationsAdapter
import marcin.malocha.koleo.database.DatabaseHelper
import marcin.malocha.koleo.helpers.JSONParser
import marcin.malocha.koleo.model.AppSystem
import marcin.malocha.koleo.net.NetUtils
import marcin.malocha.koleo.net.results.StationsResult
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {
    lateinit var DBHelper : DatabaseHelper
    var stationsAdapter: StationsAdapter? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DBHelper = DatabaseHelper(this)
        val stations = DBHelper.getStations()

        stationsAdapter = StationsAdapter(stations, this)
        stations_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = stationsAdapter
        }

        var refreshStations = true
        getLastRefreshStationsDate()?.let {
            val now = Calendar.getInstance()
            now.add(Calendar.DAY_OF_YEAR, -1)
            refreshStations = it.before(now.time)
        }

        if (stations.isNullOrEmpty() || refreshStations) {
            getStationsFromApi()
        }

        button_calculate.setOnClickListener {
            val latitude1: Double
            val longitude1: Double

            val latitude2: Double
            val longitude2: Double

            try {
                latitude1 = AppSystem.selectedStations[0].latitude.toDouble()
                longitude1 = AppSystem.selectedStations[0].longitude.toDouble()

                latitude2 = AppSystem.selectedStations[1].latitude.toDouble()
                longitude2 = AppSystem.selectedStations[1].longitude.toDouble()

                val location1 = Location("")
                location1.latitude = latitude1
                location1.longitude = longitude1

                val location2 = Location("")
                location2.latitude = latitude2
                location2.longitude = longitude2

                Toast.makeText(this, String.format("Odległość pomiędzy wybranymi stacjami wynosi: %.2f km", (location1.distanceTo(location2) / 1000)), Toast.LENGTH_LONG).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Nie można obliczyć odległości pomiędzy tymi stacjami", Toast.LENGTH_LONG).show()
            } catch (e: IndexOutOfBoundsException) {
                Toast.makeText(this, "Proszę zaznaczyć dwie stacje", Toast.LENGTH_LONG).show()
            }
        }

        search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                stationsAdapter!!.filter(s.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        first_station_delete_button.setOnClickListener {
            stationsAdapter?.removeSelectedStation(AppSystem.selectedStations[0])
            stationsAdapter?.notifyDataSetChanged()
        }

        second_station_delete_button.setOnClickListener {
            stationsAdapter?.removeSelectedStation(AppSystem.selectedStations[1])
            stationsAdapter?.notifyDataSetChanged()
        }
    }

    private fun getStationsFromApi() {
        loading_view.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        Observable.fromCallable { NetUtils.getStations() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<StationsResult> {
                override fun onComplete() {
                    stationsAdapter?.refresh(DBHelper.getStations())
                    saveRefreshStationsDate()
                    loading_view.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: StationsResult) {
                    if (t.success) {
                        for (i in 0 until t.stations.length()) {
                            DBHelper.insertStation(JSONParser.JSONToStation(t.stations.getJSONObject(i)))
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    loading_view.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(this@MainActivity, "Błąd pibierania danych. Proszę uruchomić ponownie aplikację.", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun saveRefreshStationsDate() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.last_refresh_date_key), dateFormat.format(Calendar.getInstance().time))
            commit()
        }
    }

    private fun getLastRefreshStationsDate(): Date? {
        var date: Date? = null
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val dateString = sharedPref.getString(getString(R.string.last_refresh_date_key), null)

        dateString?.let {
            date = dateFormat.parse(dateString)
        }

        return date
    }
}

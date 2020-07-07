package marcin.malocha.koleo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import marcin.malocha.koleo.R
import marcin.malocha.koleo.model.DBKoleo
import marcin.malocha.koleo.model.Station

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val context = context
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Koleo.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBKoleo.StationEntry.TABLE_NAME + " (" +
                    DBKoleo.StationEntry.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, " +
                    DBKoleo.StationEntry.COLUMN_NAME + " TEXT, " +
                    DBKoleo.StationEntry.COLUMN_NAME_SLUG + " TEXT, " +
                    DBKoleo.StationEntry.COLUMN_LATITUDE + " REAL, " +
                    DBKoleo.StationEntry.COLUMN_LONGITUDE + " REAL, " +
                    DBKoleo.StationEntry.COLUMN_HITS + " INTEGER, " +
                    DBKoleo.StationEntry.COLUMN_IBNR + " INTEGER)"

        private const val SQL_DELETE_ENTRIES = "DROP IF EXISTS " + DBKoleo.StationEntry.TABLE_NAME
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            rebuildDatabase(db)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion < oldVersion) {
            rebuildDatabase(db)
        }
    }

    fun getStations(): ArrayList<Station> {
        val stations = ArrayList<Station>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBKoleo.StationEntry.TABLE_NAME + " ORDER BY " + DBKoleo.StationEntry.COLUMN_NAME + " ASC", null)
        } catch (e: SQLiteException) {
            return stations
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                var name = cursor.getString(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_NAME))
                if (name.equals("null", true))
                    name = context.getString(R.string.epmty_string)

                var nameSlug = cursor.getString(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_NAME_SLUG))
                if (nameSlug.equals("null", true))
                    nameSlug = context.getString(R.string.epmty_string)

                var latitude = cursor.getString(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_LATITUDE))
                if (latitude.equals("null", true))
                    latitude = context.getString(R.string.epmty_string)

                var longitude = cursor.getString(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_LONGITUDE))
                if (longitude.equals("null", true))
                    longitude = context.getString(R.string.epmty_string)

                stations.add(Station(
                    cursor.getInt(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_ID)),
                    name,
                    nameSlug,
                    latitude,
                    longitude,
                    cursor.getInt(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_HITS)),
                    cursor.getIntOrNull(cursor.getColumnIndex(DBKoleo.StationEntry.COLUMN_IBNR))
                ))
                cursor.moveToNext()
            }
        }
        return stations
    }

    @Throws(SQLiteConstraintException::class)
    fun insertStation(station: Station): Long {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBKoleo.StationEntry.COLUMN_ID, station.id)
        values.put(DBKoleo.StationEntry.COLUMN_NAME, station.name)
        values.put(DBKoleo.StationEntry.COLUMN_NAME_SLUG, station.nameSlug)
        values.put(DBKoleo.StationEntry.COLUMN_LATITUDE, station.latitude)
        values.put(DBKoleo.StationEntry.COLUMN_LONGITUDE, station.longitude)
        values.put(DBKoleo.StationEntry.COLUMN_HITS, station.hits)
        values.put(DBKoleo.StationEntry.COLUMN_IBNR, station.ibnr)

        val test =  db.insert(DBKoleo.StationEntry.TABLE_NAME, null, values)
        return test
    }

    private fun rebuildDatabase(db: SQLiteDatabase) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}
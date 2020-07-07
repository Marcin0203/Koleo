package marcin.malocha.koleo.model

import android.provider.BaseColumns

object DBKoleo {
    class StationEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "stations"
            const val COLUMN_ID = "id"
            const val COLUMN_NAME = "name"
            const val COLUMN_NAME_SLUG = "nameSlug"
            const val COLUMN_LATITUDE = "latitude"
            const val COLUMN_LONGITUDE = "longitude"
            const val COLUMN_HITS = "hits"
            const val COLUMN_IBNR = "ibnr"
        }
    }
}
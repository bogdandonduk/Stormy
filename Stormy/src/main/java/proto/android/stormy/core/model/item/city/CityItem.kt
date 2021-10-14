package proto.android.stormy.core.model.item.city

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import proto.android.stormy.core.model.cache.city.CityDatabase
import proto.android.stormy.core.model.item.CoreItem
import proto.android.stormy.core.model.item.weather.Weather

@Entity(tableName = CityDatabase.Utils.TABLE_CITIES_NAME)
data class CityItem(
//    @SerializedName("geonameid")
    override var intrinsicId: Long,

//    @SerializedName("name")
    override var name: String,

//    @SerializedName("country code")
    override var countryCode: String,

    override var rawDataText: String,

    @PrimaryKey(autoGenerate = true)
    override var persistenceId: Long = 0
) : CoreItem {
    @Ignore
    override var weather: Weather? = null

    override var imageData: ByteArray? = null

    override var radarImageData: ByteArray? = null
}
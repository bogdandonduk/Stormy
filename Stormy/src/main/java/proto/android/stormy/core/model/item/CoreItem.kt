package proto.android.stormy.core.model.item

import androidx.room.Entity
import proto.android.stormy.core.model.item.weather.Weather

@Entity
interface CoreItem {
    var intrinsicId: Long
    var persistenceId: Long

    var name: String
    var countryCode: String

    var imageData: ByteArray?

    var radarImageData: ByteArray?

    var rawDataText: String

    var weather: Weather?
}
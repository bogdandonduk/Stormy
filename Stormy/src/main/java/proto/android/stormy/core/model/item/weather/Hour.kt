package proto.android.stormy.core.model.item.weather

data class Hour(
    var intrinsicId: Int,
    var hour: Int,
    var rainChance: Double,
    var windSpeed: Double,
    var humidity: Double,
    var weatherType: String,
    var temperature: Int
)
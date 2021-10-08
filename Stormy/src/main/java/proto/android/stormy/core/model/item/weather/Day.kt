package proto.android.stormy.core.model.item.weather

data class Day(
    var intrinsicId: Int,
    var weatherType: String,
    var hours: List<Hour>
) {
    constructor(intrinsicId: Int, weatherType: String, vararg hours: Hour) : this(intrinsicId, weatherType, hours.toList())
}
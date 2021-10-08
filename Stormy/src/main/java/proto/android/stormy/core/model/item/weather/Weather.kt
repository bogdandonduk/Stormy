package proto.android.stormy.core.model.item.weather

data class Weather(var days: List<Day>) {
    constructor(vararg days: Day) : this(days.toList())
}
package proto.android.stormy.core.model.fetch

import proto.android.stormy.core.model.item.CoreItem

interface Fetcher<ItemType : CoreItem> {
    var reattemptStrategy: RequestReattemptStrategy

    suspend fun fetchSpecific(intrinsicId: Long) : ItemType?

    suspend fun fetchAll() : List<ItemType>?

    enum class RequestReattemptStrategy(var attemptsNumber: Int) {
        UNOBTRUSIVE(attemptsNumber = 1),
        PREFERRED(attemptsNumber = 3),
        STUBBORN(attemptsNumber = 5)
    }
}
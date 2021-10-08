package proto.android.stormy.core.model.cache

import proto.android.stormy.core.model.item.CoreItem

interface CacheDao<ItemType : CoreItem>{
    suspend fun add(item: ItemType)

    suspend fun get(itemIntrinsicId: Long) : ItemType?

    suspend fun update(item: ItemType)

    suspend fun delete(item: ItemType)

    suspend fun deleteAll()
}
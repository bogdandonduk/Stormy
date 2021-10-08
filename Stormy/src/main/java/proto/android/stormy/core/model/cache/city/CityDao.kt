package proto.android.stormy.core.model.cache.city

import androidx.room.*
import proto.android.stormy.core.model.cache.CacheDao
import proto.android.stormy.core.model.item.city.CityItem

@Dao
interface CityDao : CacheDao<CityItem> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun add(item: CityItem)

    @Query("SELECT * FROM ${CityDatabase.Utils.TABLE_CITIES_NAME} WHERE intrinsicId = :itemIntrinsicId")
    override suspend fun get(itemIntrinsicId: Long) : CityItem

    @Update
    override suspend fun update(item: CityItem)

    @Delete
    override suspend fun delete(item: CityItem)

    @Query("DELETE FROM ${CityDatabase.Utils.TABLE_CITIES_NAME}")
    override suspend fun deleteAll()
}
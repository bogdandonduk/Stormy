package proto.android.stormy.core.model.cache.city

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import proto.android.stormy.core.model.item.city.CityItem

@Database(entities = [CityItem::class], version = CityDatabase.Utils.DATABASE_VERSION, exportSchema = false)
abstract class CityDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: CityDatabase? = null

        fun getSingleton(context: Context) : CityDatabase {
            if(instance == null)
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context,
                        CityDatabase::class.java,
                        Utils.CITIES_DATABASE_NAME
                    ).build()
                }

            return instance!!
        }
    }

    abstract fun getDao() : CityDao

    object Utils {
        const val TABLE_CITIES_NAME = "table_cities"
        const val DATABASE_VERSION = 1
        const val CITIES_DATABASE_NAME = "database_cities"
    }
}
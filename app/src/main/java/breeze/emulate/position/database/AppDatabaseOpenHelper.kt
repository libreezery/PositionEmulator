package breeze.emulate.position.database

import android.content.Context
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import breeze.emulate.position.database.AppDatabaseOpenHelper
import android.database.sqlite.SQLiteDatabase
import breeze.emulate.position.bean.AppLocationBean
import breeze.emulate.position.bean.CoordinateBean
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

class AppDatabaseOpenHelper(context: Context?) : OrmLiteSqliteOpenHelper(context, dbName, null, dbVersion) {

    companion object Configure{
        private const val dbName = "location.db"
        private const val dbVersion = 1
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase, connectionSource: ConnectionSource) {
        TableUtils.createTableIfNotExists(connectionSource,AppLocationBean::class.java)
        TableUtils.createTableIfNotExists(connectionSource,CoordinateBean::class.java)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, connectionSource: ConnectionSource, oldVer: Int, newVer: Int) {
        for (v in oldVer until newVer) {
            // 操作
        }
    }

}
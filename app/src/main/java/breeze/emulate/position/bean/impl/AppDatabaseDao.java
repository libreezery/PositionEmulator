package breeze.emulate.position.bean.impl;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import breeze.emulate.position.database.AppDatabaseOpenHelper;
import kotlin.reflect.KClass;

public abstract class AppDatabaseDao {

    protected volatile static AppDatabaseOpenHelper HELPER;
    protected final           Context               context;

    public AppDatabaseDao(Context context) {
        if (HELPER == null)
            HELPER = new AppDatabaseOpenHelper(context);
        this.context = context;
    }

}

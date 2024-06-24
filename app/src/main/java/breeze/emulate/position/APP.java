package breeze.emulate.position;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import breeze.emulate.position.service.AppExceptionCatcher;
import breeze.emulate.position.tools.AppTools;

public class APP extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionCatcher());
        context = getApplicationContext();

        try {
            FileHandler fileHandler = new FileHandler(AppTools.getLoggerFile(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            Logger aDefault = Logger.getGlobal();
            aDefault.setLevel(Level.ALL);
            aDefault.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }


   /*     LitePalDB litePalDB = new LitePalDB("Location2.db",1);
        litePalDB.addClassName(AppLocationBean.class.getName());
        litePalDB.addClassName(CoordinateBean.class.getName());
        LitePal.use(litePalDB);*/
    }



}

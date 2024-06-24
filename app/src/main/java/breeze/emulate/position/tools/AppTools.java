package breeze.emulate.position.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import breeze.emulate.position.APP;

public class AppTools {
    public static File getSaveCoordinatesDir(Context context) {
        File file = new File(context.getExternalFilesDir("/").getAbsolutePath() + "/coordinates/");
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static File getSaveFilePath(Context context) {
        File file = new File(getSaveCoordinatesDir(context) +"/" + getTime("yyyyMMddHHmm") + ".cor");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String format) {
        return new SimpleDateFormat(format).format(System.currentTimeMillis());
    }

    public static String getLoggerFile() {
        return APP.getContext().getExternalFilesDir("logger")+"/logger.log";
    }
}

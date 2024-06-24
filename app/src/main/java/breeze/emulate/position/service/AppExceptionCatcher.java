package breeze.emulate.position.service;

import androidx.annotation.NonNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppExceptionCatcher implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Logger.getGlobal().log(Level.SEVERE,"App error:",e);
    }
}

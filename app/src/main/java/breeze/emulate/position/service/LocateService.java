package breeze.emulate.position.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class LocateService extends Service implements LocateServiceInterface {

    private LocationManager locationManager;

    private String provider;

    private Timer timer;

    private static final String TAG = "LocateService";

    public boolean b;

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private Context context;

    public LocateService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        timer = new Timer();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        provider = intent.getStringExtra("provider");
        return new UserBinder(this);
    }

    @Override
    public void changeProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public static class UserBinder extends Binder {
        private final LocateService locateService;

        public UserBinder(LocateService locateService) {
            this.locateService = locateService;
        }

        public LocateService getLocateService() {
            return locateService;
        }
    }

    @SuppressLint("MissingPermission")
    public void obtainLocation(GetLocationListener locationListener) {
        TimerTask timerTask = new TimerTask() {
            @SuppressLint({"SimpleDateFormat", "MissingPermission"})
            @Override
            public void run() {
                StringBuilder stringBuilder = new StringBuilder();
                if (b) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        locationManager.getCurrentLocation(provider, new CancellationSignal(), getMainExecutor(), location -> {
                            locationListener.onResponse(location);
                            stringBuilder.append(TAG + "是否有数据:").append(location != null).append("\n");
                        });
                    } else {
                        // get location by getLastKnownLocation
                        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                        locationListener.onResponse(lastKnownLocation);
                        Log.d(TAG, "run: 是否有数据:" + (lastKnownLocation != null));
                    }

                    stringBuilder.append(TAG + ":run: 运行模式:").append(provider).append("\n");
                    stringBuilder.append(TAG + ":run: 运行状态:").append(b).append("\n");
                    stringBuilder.append(TAG + ":run: 当前时间:").append(new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())).append("\n");

                    Intent intent1 = new Intent("com.example.myapplication.LOG");
                    intent1.putExtra("logs", stringBuilder.toString());
                    sendBroadcast(intent1);
                }

            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public interface GetLocationListener {
        void onResponse(Location location);
    }
}
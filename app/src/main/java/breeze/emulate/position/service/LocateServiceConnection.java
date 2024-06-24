package breeze.emulate.position.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class LocateServiceConnection implements ServiceConnection {

    private LocateService locateService;

    private LocateService.GetLocationListener getLocationListener;

    private static final String TAG = "LocateServiceConnection";

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LocateService.UserBinder userBinder = (LocateService.UserBinder) service;
        locateService = userBinder.getLocateService();
        Log.d(TAG, "onServiceConnected: 服务连接成功");

        Intent intent1 = new Intent("com.example.myapplication.LOG");
        intent1.putExtra("logs", TAG + ":服务连接成功");
        locateService.sendBroadcast(intent1);
        bindGetLocationListener(this.getLocationListener);
    }

    public LocateServiceInterface getLocateService() {
        return locateService;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    @Override
    public void onNullBinding(ComponentName name) {

    }

    public void bindGetLocationListener(LocateService.GetLocationListener locationListener) {
        if (getLocationListener == null) {
            this.getLocationListener = locationListener;
        }
        if (locateService != null) {
            locateService.obtainLocation(locationListener);
        }
    }

    public void changeState(boolean b) {
        if (locateService != null) {
            locateService.b = b;
        }
    }
}

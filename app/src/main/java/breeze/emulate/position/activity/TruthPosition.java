package breeze.emulate.position.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import breeze.emulate.position.R;
import breeze.emulate.position.service.LocateService;
import breeze.emulate.position.service.LocateServiceConnection;
import breeze.emulate.position.tools.AppTools;
import breeze.emulate.position.widget.AppEditTextDialog.AppEditTextDialog;

public class TruthPosition extends AppCompatActivity {

    private TextView textView;

    private static final String TAG = "MainActivity";

    private LocationManager locationManager;

    private String locationProvider = "GPS";

    private final List<String> providerList = new ArrayList<>();

    private ArrayAdapter<String> arrayAdapter;

    private boolean isStartService = false;

    private MaterialToolbar toolbar;

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private EditText editText;

    private TextView logText;

    private Intent intentService;

    private MainBroadcast mainBroadcast;

    private Switch aSwitch;

    private boolean isAutoRecord = false;

    private File savePosition;

    private String fileName;

    private final LocateServiceConnection locateServiceConnection = new LocateServiceConnection();

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.main_position);
        editText = findViewById(R.id.main_logs);
        aSwitch = findViewById(R.id.location_auto_record);
        logText = findViewById(R.id.log_text);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Spinner spinner = findViewById(R.id.mainSpinner);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, providerList);
        Button startService = findViewById(R.id.mainStartService);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationProvider = providerList.get(position);
                toolbar.setSubtitle(locationProvider);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                locationProvider = "GPS";
                toolbar.setSubtitle(locationProvider);
            }
        });

        startService.setOnClickListener(v -> {
            if (!isStartService) {
                // close
                ((Button) (v)).setText("停用服务定位");
            } else {
                ((Button) (v)).setText("启用服务定位");
            }
            locateServiceConnection.changeState(!isStartService);
            isStartService = !isStartService;
        });

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isAutoRecord = isChecked);

        mainBroadcast = new MainBroadcast();
        IntentFilter intentFilter = new IntentFilter("com.example.myapplication.LOG");
        registerReceiver(mainBroadcast, intentFilter);

        checkProvider(null);
        toolbar.setSubtitle(locationProvider);

        savePosition = AppTools.getSaveCoordinatesDir(this);
        fileName = AppTools.getTime("MM-dd HH:mm")+".cor";

        intentService = new Intent(this, LocateService.class);
        intentService.putExtra("provider", locationProvider);
        startService(intentService);
        bindService(intentService, locateServiceConnection, BIND_AUTO_CREATE);
        locateServiceConnection.bindGetLocationListener(this::setText);

        Intent intent1 = new Intent("com.example.myapplication.LOG");
        intent1.putExtra("logs", TAG + ":Service start!");
        sendBroadcast(intent1);

        showEditTextDialog();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10001);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainBroadcast != null) {
            unregisterReceiver(mainBroadcast);
        }
        if (intentService != null) {
            stopService(intentService);
        }
        if (fileOutputStream!=null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取定位
     *
     * @param view 视图
     */
    public void getLocation(View view) {
        if (checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(locationProvider, new CancellationSignal(), executor, new Consumer<Location>() {
                    @Override
                    public void accept(Location location) {
                        setText(location);
                    }
                });
            } else {
                // get location by getLastKnownLocation
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                setText(lastKnownLocation);
            }
        }
    }

    private Location currentLocation;
    public void setText(Location location) {
        textView.post(() -> {
            String stringBuilder;
            if (location != null) {
                currentLocation = location;
                stringBuilder = "经度:" + location.getLongitude() +
                        "\n" +
                        "纬度:" + location.getLatitude();
                if (isAutoRecord) {
                    String lat = String.valueOf(location.getLatitude());
                    String lon = String.valueOf(location.getLongitude());
                    if (lat.length()>=14 && lon.length()>=14){
                        record(null);
                    }
                }
            } else {
                stringBuilder = locationProvider + "未获取到信息";
            }
            textView.setText(stringBuilder);
        });
    }

    public void checkProvider(View view) {
        if (checkPermission()) {
            providerList.clear();
            List<String> allProviders = locationManager.getAllProviders();
            for (String pro : allProviders) {
                StringBuilder stringBuilder = new StringBuilder();
                LocationProvider provider = locationManager.getProvider(pro);
                if (locationManager.isProviderEnabled(pro)) {
                    stringBuilder.append(TAG + ":定位方式:").append(provider.getName()).append("\n");
                    stringBuilder.append(TAG + ":精确度:").append(provider.getAccuracy()).append("\n");
                    stringBuilder.append(TAG + ":是否支持速度:").append(provider.supportsSpeed()).append("\n");

                    providerList.add(provider.getName());
                }
                Intent intent1 = new Intent("com.example.myapplication.LOG");
                intent1.putExtra("logs", stringBuilder.toString());
                sendBroadcast(intent1);
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void changeProvider(View view) {
        locateServiceConnection.getLocateService().changeProvider(locationProvider);
    }

    private FileOutputStream fileOutputStream;
    public void record(View view) {
        try {
            File file = new File(savePosition.getAbsolutePath()+"/"+fileName);
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            }
            if (fileOutputStream == null) {
                fileOutputStream = new FileOutputStream(file,true);
            }
            if (currentLocation!=null) {
                fileOutputStream.write((currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "\n").getBytes());
            }
            Intent intent1 = new Intent("com.example.myapplication.LOG");
            intent1.putExtra("logs", ":记录成功!\n");
            sendBroadcast(intent1);
        } catch (Exception e) {
            Log.d(TAG, "record: error:"+e);
        }
    }

    public class MainBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.myapplication.LOG".equalsIgnoreCase(intent.getAction())) {
                String logs = intent.getStringExtra("logs");
               /* logText.getEditableText().insert(0, "\n");
                logText.getEditableText().insert(0, logs);*/
                Editable editableText = (Editable) logText.getText();
                editableText.insert(0, logs);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.truth_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_clear) {
            logText.setText("");
        } else if (itemId == R.id.menu_rename) {
            showEditTextDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditTextDialog() {
        AppEditTextDialog appEditTextDialog = new AppEditTextDialog.Builder(this)
                .setTitle("文件命名")
                .setMessage("为储存的文件进行命名")
                .setCancelable(false)
                .setPositiveButton("确定", (a,b) -> {
                    fileName = a+".cor";
                    b.dismiss();
                })
                .build();
        appEditTextDialog.show();
    }
}
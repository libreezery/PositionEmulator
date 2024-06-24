package breeze.emulate.position;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import breeze.emulate.position.activity.ImportCoordinateActivity;
import breeze.emulate.position.activity.TruthPosition;
import breeze.emulate.position.bean.AppCampusLocationData;
import breeze.emulate.position.bean.CoordinateBean;
import breeze.emulate.position.database.AppDatabaseOpenHelper;
import brz.breeze.app_utils.BAppCompatActivity;

public class MainActivity extends BAppCompatActivity {

    private static final String TAG = "MainActivity";

    private MapView mapView;

    private AMap map;

    private Marker selectedMarker = null;

    private TextView speed_text;

    private final List<LatLng> latLngList = new ArrayList<>();

    private Button delete_marker_btn;

    private SeekBar seekBar;

    private SQLiteDatabase writableDatabase;

    private Button start_button;

    private CheckBox checkBox_not_add_mark;

    private EditText edit_lon, edit_lat;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = find(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView = find(R.id.main_mapview);
        mapView.onCreate(savedInstanceState);
        boolean hooked = getHooked();
        if (hooked) {
            toolbar.setSubtitle("模块运行正常");
            init();
            initData();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("模块未启动，请先启动模块再进入应用")
                    .setPositiveButton("确定", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void init() {
        delete_marker_btn = find(R.id.main_delete_marker);
        seekBar = find(R.id.main_speed_change);
        start_button = find(R.id.main_start_btn);
        checkBox_not_add_mark = find(R.id.main_not_add_mark);
        edit_lat = find(R.id.main_edit_latitude);
        edit_lon = find(R.id.main_edit_longitude);
        speed_text = find(R.id.main_speed_text);

        map = mapView.getMap();
        // 初始化地图信息
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(3000);
        myLocationStyle.showMyLocation(true);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        map.setMyLocationEnabled(true);
        map.setMyLocationStyle(myLocationStyle);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        // 点击地图生成标点
        map.setOnMapClickListener(latLng -> {
            if (isAddMark) {
                addMarker(latLng);
            }
            latLngList.add(latLng);
            map.addPolyline(new PolylineOptions().addAll(latLngList).width(10).color(Color.parseColor("#009688")));
        });

        map.setOnMarkerClickListener(marker -> {
            selectedMarker = marker;
            checkMarkerDeletable();
            return false;
        });

    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("标记点");
        markerOptions.snippet("经度:" + latLng.longitude + "|纬度:" + latLng.latitude);
        map.addMarker(markerOptions);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {
        // 初始化数据库
        AppDatabaseOpenHelper appDatabaseOpenhelper = new AppDatabaseOpenHelper(this);
        writableDatabase = appDatabaseOpenhelper.getWritableDatabase();
        // 设置控件操作
        delete_marker_btn.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("是否删除这个标记点")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (selectedMarker != null) {
                            selectedMarker.destroy();
                            LatLng position = selectedMarker.getPosition();
                            latLngList.remove(position);
                            selectedMarker = null;
                        }
                        checkMarkerDeletable();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 设置权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 申请权限
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("为保证应用正常运行需要向用户获取权限")
                        .setPositiveButton("确定", (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 0))
                        .setNegativeButton("退出", (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            }
        }

        seekBar.setMax(15);
        seekBar.setProgress(preference.getInt("speedMillion", 3));
        speed_text.setText("速度:" + preference.getInt("speedMillion", 3) + "秒/段");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                preference.edit().putInt("speedMillion", progress).apply();
                speedMillion = progress;
                speed_text.setText("速度:" + progress + "秒/段");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        checkBox_not_add_mark.setOnCheckedChangeListener((buttonView, isChecked) -> isAddMark = !isChecked);
    }

    private boolean isAddMark    = true;
    private int     speedMillion = 3;

    private void checkMarkerDeletable() {
        delete_marker_btn.setEnabled(selectedMarker != null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (writableDatabase != null) {
            writableDatabase.execSQL("delete from coordinate");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private boolean getHooked() {
        return false;
    }

    public void deleteAllMarkers(View view) {
        List<Marker> mapScreenMarkers = map.getMapScreenMarkers();
        if (!mapScreenMarkers.isEmpty()) {
            for (Marker marker : mapScreenMarkers) {
                marker.destroy();
            }
            latLngList.clear();
            toast("删除成功");
        } else {
            toast("没有标记点");
        }
    }

    private boolean isRUN = false;

    public void startEmulatePosition(View view) {
        isRUN = !isRUN;
        Intent intent = new Intent("breeze.emulate.position.hook.UPDATE_SETTINGS");
        intent.putExtra("isRun", isRUN);
        intent.putExtra("speedMillion", speedMillion);
        sendBroadcast(intent);
        start_button.setText(isRUN ? "关闭模拟定位" : "开始模拟定位");
    }

    public void saveMarkers(View view) {
        if (latLngList.size() > 0) {
            for (LatLng latLng : latLngList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("lon", latLng.longitude);
                contentValues.put("lat", latLng.latitude);
                writableDatabase.insert("coordinate", null, contentValues);
            }
            toast("保存成功");
        }
    }

    public void clearCoordinate(View view) {
        writableDatabase.execSQL("delete from coordinate");
        toast("清理成功");
    }

    public void jumpTo(View view) {
        String longitude = edit_lon.getText().toString();
        String latitude  = edit_lat.getText().toString();
        if (!longitude.isEmpty() && !latitude.isEmpty()) {
            if (longitude.equals("0000")) {
                // TODO:导入学校数据
                writableDatabase.execSQL("delete from coordinate");
                List<CoordinateBean> campusCoordinate = AppCampusLocationData.getCampusCoordinate();
                for (CoordinateBean coordinateBean : campusCoordinate) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("lon", coordinateBean.longitude);
                    contentValues.put("lat", coordinateBean.latitude);
                    writableDatabase.insert("coordinate", null, contentValues);
                }
                toast("川成坐标导入成功");
            } else {
                double       l            = Double.parseDouble(longitude);
                double       lat          = Double.parseDouble(latitude);
                LatLng       latLng       = new LatLng(lat, l);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 0, 0));
                map.moveCamera(cameraUpdate);
                addMarker(latLng);
            }
        } else {
            toast("请输入经纬度");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.main_obtain_truth_position) {
            startActivity(new Intent(this, TruthPosition.class));
        } else if (item.getItemId() == R.id.main_import_truth_position) {
            startActivityForResult(new Intent(this, ImportCoordinateActivity.class), 1002);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
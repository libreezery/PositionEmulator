package breeze.emulate.position.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import breeze.emulate.position.R;
import breeze.emulate.position.adapter.CoordinateAdapter;
import breeze.emulate.position.tools.AppTools;
import brz.breeze.app_utils.BAppCompatActivity;

public class ImportCoordinateActivity extends BAppCompatActivity {

    private ListView listView;

    private CoordinateAdapter coordinateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_coordinate);
        init();
        initData();
    }

    @Override
    public void init() {
        MaterialToolbar materialToolbar = find(R.id.toolbar);
        setSupportActionBar(materialToolbar);
        listView = findViewById(R.id.coordinate_listview);
    }

    @Override
    public void initData() {
        // get path
        File[] a = AppTools.getSaveCoordinatesDir(this).listFiles();
        if (a!=null) {
            List<File> files = Arrays.asList(a);
            coordinateAdapter = new CoordinateAdapter(this,files);
            listView.setAdapter(coordinateAdapter);
            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                new AlertDialog.Builder(ImportCoordinateActivity.this)
                        .setTitle("提示")
                        .setMessage("是否删除?")
                        .setPositiveButton("确定", (dialog, which) -> {
                            ((File)coordinateAdapter.getItem(position)).delete();
                            coordinateAdapter.delete(position);
                        })
                        .setNeutralButton("全部删除", (dialog, which) -> {
                            coordinateAdapter.deleteAll();
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
                return true;
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
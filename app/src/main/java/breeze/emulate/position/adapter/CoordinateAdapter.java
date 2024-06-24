package breeze.emulate.position.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import breeze.emulate.position.R;

public class CoordinateAdapter extends BaseAdapter {


    private Context context;

    private List<File> files;

    public CoordinateAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }

    public void delete(int position) {
        files.remove(position);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        files.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = files.get(position);
        @SuppressLint("ViewHolder") View inflate = LayoutInflater.from(context).inflate(R.layout.view_item, null);
        TextView title = inflate.findViewById(R.id.item_name);
        title.setText(file.getName());
        return inflate;
    }
}

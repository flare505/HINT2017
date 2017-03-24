package com.example.jit.checkmate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jit on 24-03-2017.
 */

public class CustonAdapter extends BaseAdapter {

    ArrayList<String> Alist;
    Context act;
    private static LayoutInflater inf;

    public CustonAdapter(Context act, ArrayList<String> alist) {
        this.act = act;
        this.Alist = new ArrayList<String>(alist);
        inf = LayoutInflater.from(act);
    }

    @Override
    public int getCount() {
        return Alist.size();
    }

    @Override
    public String getItem(int i) {
        return Alist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if (view == null)
            vi = inf.inflate(R.layout.custom_list, null);

        TextView tv1 = (TextView) vi.findViewById(R.id.tv1);

        final String entity = Alist.get(position);

        tv1.setText(entity);

        return vi;
    }
}

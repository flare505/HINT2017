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

    ArrayList<Person> Alist;
    Context act;
    private static LayoutInflater inf;

    public CustonAdapter(Context act, ArrayList<Person> alist) {
        this.act = act;
        this.Alist = new ArrayList<Person>(alist);
        inf = LayoutInflater.from(act);
    }

    @Override
    public int getCount() {
        return Alist.size();
    }

    @Override
    public Person getItem(int i) {
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
        TextView details = (TextView)vi.findViewById(R.id.details);
        ImageView calling_image = (ImageView)vi.findViewById(R.id.calling_img);

        final Person person = Alist.get(position);

        tv1.setText(person.getName().toString());
        details.setText(" distance : "+person.getDistance()+" km away\n contact no : "+person.getContactno()+"\n desc : "+person.getDesc());

        calling_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = person.getContactno();
                if(num==null){
                    Toast.makeText(view.getContext(),"Contact number is not available !",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + num));
                if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) view.getContext(),new String[]{android.Manifest.permission.CALL_PHONE},1);
                    return;
                }
                Toast.makeText(view.getContext(),"calling ...",Toast.LENGTH_LONG).show();
                view.getContext().startActivity(callIntent);
            }
        });
        return vi;
    }
}

package com.example.jit.checkmate;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindMates extends AppCompatActivity {

    DatabaseReference mRootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mates);
        Bundle b = getIntent().getExtras();
        String type = b.getString("type");
        String entry = b.getString("entry");

        final ListView mate_list = (ListView)findViewById(R.id.mate_list);

        mRootRef = FirebaseDatabase.getInstance().getReference().child(type).child(entry);

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> Clist = new ArrayList<>();
                for(DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    String location = postsnapshot.getKey();
                    Clist.add(location);
                }
                ListAdapter la = new CustonAdapter(FindMates.this,Clist);
                mate_list.setAdapter(la);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void Register(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        mRootRef.child(name).setValue("iiita");
    }
}

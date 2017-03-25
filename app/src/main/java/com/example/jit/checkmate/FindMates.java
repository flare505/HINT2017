package com.example.jit.checkmate;

import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class FindMates extends AppCompatActivity {

    DatabaseReference mRootRef;
    Location mylocation;
    String desc,email,id;
    FirebaseUser user;
    Date today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mates);
        Bundle b = getIntent().getExtras();
        String type = b.getString("type");
        String entry = b.getString("entry");
        mylocation= b.getParcelable("location");
        Log.i("hello", "running askfnskjfdwe");
        Log.d("hello", "lats and longs passed to this activity : " + String.valueOf(mylocation.getLatitude()) + " " + String.valueOf(mylocation.getLongitude()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            email = user.getEmail();
            String tokens[] = email.split("@");
            id = tokens[0];
        }
        today = new Date();
        Toast.makeText(FindMates.this, "...Loading...", Toast.LENGTH_LONG).show();
        final ListView mate_list = (ListView)findViewById(R.id.mate_list);
        mRootRef = FirebaseDatabase.getInstance().getReference().child(type).child(entry);

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<Person> Clist = new ArrayList<>();
                for(DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    final String id = postsnapshot.getKey();
                    final Person person = postsnapshot.getValue(Person.class);
                    Location loc =new Location("");
                    loc.setLongitude(person.getLng());
                    loc.setLatitude(person.getLat());
                    double d = 0;
                    if(mylocation!=null)
                        d = mylocation.distanceTo(loc);
                    person.setDistance(d);
                    final DatabaseReference cref = FirebaseDatabase.getInstance().getReference().child("user");
                    cref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String contactno = dataSnapshot.child(id).getValue(String.class);
                            person.setContactno(contactno);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Date date = person.getDate();
                    if(today.before(date))
                        Clist.add(person);
                }
                Collections.sort(Clist, new Comparator<Person>() {
                    @Override
                    public int compare(Person p1, Person p2) {
                        return Double.compare(p1.getDistance(),p2.getDistance());
                    }
                });

                ListAdapter la = new CustomAdapter(FindMates.this,Clist);
                mate_list.setAdapter(la);

            /*    new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ListAdapter la = new CustomAdapter(FindMates.this,Clist);
                        mate_list.setAdapter(la);
                    }
                }).start();*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void Register(View view){

        if(user==null){
            Toast.makeText(FindMates.this,"user is null, may be internet is not connected",Toast.LENGTH_SHORT).show();
            return;
        }

        final AlertDialog.Builder alert= new AlertDialog.Builder(FindMates.this);
        final View dialogview = getLayoutInflater().inflate(R.layout.send_desc,null);
        final EditText input = (EditText)dialogview.findViewById(R.id.desc_edit);
        final EditText hour_input = (EditText)dialogview.findViewById(R.id.hour_edit);
        hour_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(dialogview);
        alert.setTitle("Add Description");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                desc = input.getEditableText().toString();
                int extra_hour = Integer.parseInt(hour_input.getText().toString());
                Date today = new Date();
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(today);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = 1+calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);

                int new_hour= (hour+extra_hour)%24;
                int new_day = day+(hour+extra_hour)/24;
                String ss = new_day+"/"+month+"/"+year+" "+new_hour+":"+minute;
                Date newdate = today;
                try {
                    newdate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(ss);
                }catch (Exception e){
                }
                //Toast.makeText(getContext(),description,Toast.LENGTH_LONG).show();
                mRootRef.child(id).setValue(new Person(user.getDisplayName(),mylocation.getLatitude(),mylocation.getLongitude(),desc,newdate));
         //       mRootRef.child(id).setValue(new Person(user.getDisplayName(),21.7,83.5,desc,newdate));
            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    public void IamDone(View view){
        try {
            mRootRef.child(id).removeValue();
        }catch (Exception e){

        }

        super.onBackPressed();
    }
}

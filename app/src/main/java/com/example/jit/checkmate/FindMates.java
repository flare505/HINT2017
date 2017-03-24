package com.example.jit.checkmate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FindMates extends AppCompatActivity {

    DatabaseReference mRootRef;
    Location mylocation;
    String desc,email,id;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mates);
        Bundle b = getIntent().getExtras();
        String type = b.getString("type");
        String entry = b.getString("entry");
        mylocation= b.getParcelable("location");

        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        String tokens[] = email.split("@");
        id = tokens[0];

    //    Toast.makeText(FindMates.this,"location : "+mylocation.getLatitude()+" "+mylocation.getLongitude(),Toast.LENGTH_LONG).show();

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
                    double d = mylocation.distanceTo(loc);
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
                    Clist.add(person);
                }
                Collections.sort(Clist, new Comparator<Person>() {
                    @Override
                    public int compare(Person p1, Person p2) {
                        return Double.compare(p1.getDistance(),p2.getDistance());
                    }
                });

                ListAdapter la = new CustonAdapter(FindMates.this,Clist);
                mate_list.setAdapter(la);

            /*    new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ListAdapter la = new CustonAdapter(FindMates.this,Clist);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Description"); //Set Alert dialog title here
        //alert.setMessage("Enter Your Name Here"); //Message here

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                desc = input.getEditableText().toString();
                //Toast.makeText(getContext(),description,Toast.LENGTH_LONG).show();
                mRootRef.child(id).setValue(new Person(user.getDisplayName(),mylocation.getLatitude(),mylocation.getLongitude(),desc));
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
        mRootRef.child(id).removeValue();
        super.onBackPressed();
    }
}

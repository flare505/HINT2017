package com.example.jit.checkmate;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    String destination;
    public static TextView dest, dest1;
    public static Button select_dest, select_rest;
    public static double lat = 0, lng = 0;
    LocationManager locationManager;
    public static Location location;
    String mprovider;
    Geocoder geocoder;
//    double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Your Interests");

        //location manager
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);
        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(mprovider);
//            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(MainActivity.this, "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this,"Sign out",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this,SignIn.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("hello", " on location changed");
//        Log.d("hello", String.valueOf(location.getLatitude()));
        lat = location.getLatitude();
        lng = location.getLongitude();
        Log.d("hello", " lat is : " + String.valueOf(lat));
    }

    //location fetcher

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        String description = "";
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView;
            final int d = getArguments().getInt(ARG_SECTION_NUMBER);
            if (d == 1) {
                rootView = inflater.inflate(R.layout.fragment1, container, false);
                //rootView.setBackground(getResources().getDrawable(R.drawable.roadtrip));
                //dest = (TextView)rootView.findViewById(R.id.dest);
                select_dest = (Button)rootView.findViewById(R.id.select_dest);
                select_dest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(getContext(),MapActivity.class);
                        getActivity().startActivityForResult(in, 1);
                    }
                });
                Button find_mate = (Button)rootView.findViewById(R.id.find_mate);
                find_mate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String dst = select_dest.getText().toString();
                        if(dst.length()==0){
                            Toast.makeText(getContext(),"Select you destination !",Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        Log.d("hello", "Lat : " +  String.valueOf(location.getLatitude()) + " and longitude : " + String.valueOf(location.getLongitude()));

                        Intent in = new Intent(getContext(),FindMates.class);
                        in.putExtra("entry",dst);
                        in.putExtra("type","travel");
                        in.putExtra("location",location);
                        Log.d("hello", String.valueOf(lat) + " and long : " + String.valueOf(lng));
//                        Log.d("hello", "Lat : " +  String.valueOf(location.getLatitude()) + " and longitude : " + String.valueOf(location.getLongitude()));
                        if(location==null) {
//
                            Toast.makeText(getContext() , "Please wait a bit", Toast.LENGTH_LONG).show();
//startActivity(in);
                        }else{
                            startActivity(in);
                        }
                    }
                });

            } else if (d == 2) {
                rootView = inflater.inflate(R.layout.fragment2, container, false);
                //rootView.setBackground(getResources().getDrawable(R.drawable.sports));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.select_dialog_item, SPORTS);
                final AutoCompleteTextView textView = (AutoCompleteTextView)
                        rootView.findViewById(R.id.countries_list);
                final Button findmate = (Button)rootView.findViewById(R.id.findmate);
                findmate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String game = textView.getText().toString();
                        if(game.length()==0){
                            textView.setError("Select any sports");
                            return;
                        }
                        Intent in = new Intent(getContext(),FindMates.class);
                        in.putExtra("entry",game);
                        in.putExtra("type","sports");
                        in.putExtra("location",location);
                        startActivity(in);
                    }
                });
                textView.setAdapter(adapter);
                textView.setThreshold(1);
                textView.setTextColor(Color.RED);
            } else {
                rootView = inflater.inflate(R.layout.fragment3, container, false);
                //rootView.setBackground(getResources().getDrawable(R.drawable.food));
                select_rest = (Button)rootView.findViewById(R.id.select_rest);
                //dest1 = (TextView)rootView.findViewById(R.id.dest1);
                select_rest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("hello", " now going to find restaurant hahah !!!");
                        int PLACE_PICKER_REQUEST = 2;
//                        getActivity().startActivityForResult(in, 2);
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            getActivity().startActivityForResult(builder.build((Activity) rootView.getContext()), PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Button find_mate = (Button)rootView.findViewById(R.id.find_mate);
                find_mate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String dst = select_rest.getText().toString();
                        if(dst.length()==0){
                            Toast.makeText(getContext(),"Select your restaurant !",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent in = new Intent(getContext(),FindMates.class);
                        in.putExtra("entry",dst);
                        in.putExtra("type","food");
                        in.putExtra("location",location);
                        startActivity(in);
                    }
                });
            }

            return rootView;
        }
        private static final String[] SPORTS = new String[] {
                "Basketball","Table Tennis","Lawn Tennis", "Badminton", "Cricket", "Dota", "Counter Strike"
        };
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TRAVEL";
                case 1:
                    return "SPORTS";
                case 2:
                    return "FOOD";
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("hello", String.valueOf(requestCode) + " result is here ");
//        destination = data.getStringExtra("result");
//        Log.d("hello", destination + " is my destination");
        Place place1 = PlacePicker.getPlace(data, this);
//        Log.d("hello", " the place is : " + place1.getAddress());
        if (requestCode == 1) {
//            destination = data.getStringExtra("result");
//            Log.d("hello", destination + " is our destination 1");
            if(resultCode == Activity.RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                select_dest.setText(place.getName());
//                Log.d("hello", " result 1 received ");
            }
            if (resultCode == Activity.RESULT_CANCELED) {

                //Write your code if there's no result
            }
        }
        if(requestCode == 2){
            Log.d("hello", " mil gya restaurant hahaha a, ");
//            Log.d("hello", destination + " is our destination  2");
//            destination = data.getStringExtra("result");
            if(resultCode == Activity.RESULT_OK){
                String address = (String) place1.getAddress();
                String str[] = address.split(",");
                destination = str[0] + ", " + str[1] + ", " + str[2];
                select_rest.setText(destination);
//                Log.d("hello", " another destination " + destination);
//                Log.d("hello", " result 2 received ");
            }
            if(resultCode == Activity.RESULT_CANCELED){
                //no result.
            }
        }
    }//onActivityResult
}

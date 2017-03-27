package com.what_the_hack.bsby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class HospitalDetails extends AppCompatActivity implements OnMapReadyCallback{


    // GLobal values

    GoogleMap mMap;
    Double latitude = 26.9124;
    Double longitude = 75.7873;

    //Data
    String name ="Municipal Hospital";
    String distance = "26";
    double rating = 4.0;
    String address = "Jaipur , Madhya Marg";
    String mobNumber ="";
    String emailAddress ="";
    String openingTime ="morning";
    String endingTime = "night";
    String description ="Good";

    // Variable for all the views

    TextView hosName;
    TextView hosDistance;
    RatingBar ratingBar;
    TextView hosAddress;
    TextView hosTime;
    TextView hosDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting hospital_deatils xml
        setContentView(R.layout.hospital_details);
        Intent intent = this . getIntent();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //
        Button btn = (Button) findViewById(R. id. appointment_button) ;
        btn . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HospitalDetails.this , AppointmentActivity.class) ;
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
            }
        } );
        Button btn1 = (Button) findViewById(R. id. feedback_button) ;
        btn1 . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HospitalDetails.this , FeedBackActivity.class) ;
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
            }
        } );
        populateData(intent);
        fillView();
    }

    void fillView()
    {
        hosName = (TextView) findViewById(R.id.hosName);
        hosDistance = (TextView) findViewById(R.id.hosDistance);
        ratingBar = (RatingBar) findViewById(R.id.pop_ratingbar);
        hosAddress = (TextView) findViewById(R.id.hosAddress);
        hosTime = (TextView) findViewById(R.id.hosTime);
        hosDescription = (TextView) findViewById(R.id.hosDescription);
        hosAddress.setText(address);
        hosName.setText(name);
        hosTime.setText("OPENING : " + openingTime + "\nCLOSING : " + endingTime);
        hosDistance.setText("DISTANCE : " + distance);
        hosDescription.setText(description);
        float r = (float) rating ;
        ratingBar.setRating(r);
    }

    // Populate data for Hospital

    void populateData(Intent intent)
    {
        if(intent.getStringExtra("hosp_names") != null)
        {name = intent.getStringExtra("hosp_names");}

        if(intent.getStringExtra("openingTimes") != null)
        { openingTime = intent.getStringExtra("openingTimes");}

        if(intent.getStringExtra("closingTimes") != null)
        { endingTime = intent.getStringExtra("closingTimes");}

        if(intent.getStringExtra("descriptions") != null)
        { description = intent.getStringExtra("descriptions");}

        if(intent.getStringExtra("contactNos") != null)
        { mobNumber = intent.getStringExtra("contactNos");}

        if(intent.getStringExtra("emails") != null)
        { emailAddress = intent.getStringExtra("emails");}

        if(intent.getStringExtra("addresses") != null)
        { address = intent.getStringExtra("addresses");}

        if(intent.getStringExtra("lats") != null && intent.getStringExtra("lngs") != null )
        {
            latitude = Double.parseDouble(intent.getStringExtra("lats"));
            longitude = Double.parseDouble(intent.getStringExtra("lngs"));
        }

        if(intent.getStringExtra("distances") != null)
        {
            distance = intent.getStringExtra("distances");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng pu = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pu, 13.5f));
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            //
        }

    }

    public void openFeedback(View view) {
        //startActivity(new Intent(this,FeedBack.class));
    }

    public void callHospital(View view)
    {
        if(mobNumber . length() == 0)
            return ;
        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobNumber)));
        }

    }

    public void sendEmail(View view)
    {
        if(emailAddress == "")
            return ;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    /*
     private void centerMapOnMyLocation(GoogleMap map) {

     try{
     map.setMyLocationEnabled(true);

     if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
     == PackageManager.PERMISSION_GRANTED) {
     map.setMyLocationEnabled(true);
     } else {
     Toast.makeText(HospitalDetails.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
     if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
     == PackageManager.PERMISSION_GRANTED) {
     map.setMyLocationEnabled(true);
     }
     }

     if(map != null)
     {
     LocationManager locationManager = (LocationManager)
     getSystemService(Context.LOCATION_SERVICE);

     Criteria criteria = new Criteria();

     Location location = locationManager.getLastKnownLocation(locationManager
     .getBestProvider(criteria, false));


     map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,
     15));
     }



     }
     catch (Exception e)
     {
     Log.e("Map","zooming");
     }
     }*/
}

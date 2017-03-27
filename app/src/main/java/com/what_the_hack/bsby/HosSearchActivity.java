package com.what_the_hack.bsby;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HosSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener , LocationListener {

    private double latitude, longitude;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<String> hosp_names = new ArrayList<>() ;
    private ArrayList<String> openingTimes  = new ArrayList<>() ;
    private ArrayList<String> closingTimes = new ArrayList<>() ;
    private ArrayList<String> lats = new ArrayList<>() ;
    private ArrayList<String> lngs = new ArrayList<>() ;
    private ArrayList<String> descriptions = new ArrayList<>() ;
    private ArrayList<String> contactNos = new ArrayList<>() ;
    private ArrayList<String> emails = new ArrayList<>() ;
    private ArrayList<String> addresses = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hos_search);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Button btn = (Button) findViewById(R. id. searchButton) ;
        btn . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_hos();
            }
        } );
    }

    @Override
    public void onStart() {
        super . onStart();
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(! lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // If GPS is disabled
            Toast.makeText(getApplicationContext(), "Please enable GPS to send emergency alert ...", Toast.LENGTH_SHORT).show();
            ShowGPSSettings(HosSearchActivity.this);
        }
        mGoogleApiClient.connect();
    }



    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(500);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            //
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        //setValues(longitude , latitude);
        //Toast.makeText(this, longitude + " " + latitude , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient . disconnect();
        super.onDestroy();
    }


    void search_hos() {
        EditText eCity = (EditText)findViewById(R . id . city) ;
        final String city = eCity  . getText() . toString() ;

        if(city . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter City ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        EditText eDistrict = (EditText) findViewById(R . id . district) ;
        final String district = eDistrict . getText() . toString() ;

        if(district . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter District ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        class wrapper {
            int status ;
        }

        class SendPostReqAsyncTask extends AsyncTask<String, Void, wrapper> {
            private wrapper w = new wrapper();
            private ProgressDialog dialog = ProgressDialog.show(HosSearchActivity.this, "",
                    "Searching Hospitals . Please wait...", true);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(checkConnection(getApplicationContext())) {
                    dialog.show();
                    dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode,
                                             KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss();
                            }
                            return true;
                        }
                    });
                }
            }

            @Override
            protected wrapper doInBackground(String... params) {
                String data = "";
                try {
                    data = URLEncoder.encode("City", "UTF-8")
                            + "=" + URLEncoder.encode(city , "UTF-8") + "&" + URLEncoder.encode("District", "UTF-8")
                            + "=" + URLEncoder.encode(district , "UTF-8") ;
                } catch (UnsupportedEncodingException e) {
                    //
                }
                BufferedReader reader = null;

                // Send data
                try {
                    // Define URL where to send data
                    // https://bsby.000webhostapp.com/fix_appointment.php
                    URL url = new URL("https://bsby.000webhostapp.com/hos_detail.php") ;

                    // Send POST data request
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    int fetchedData = reader.read();
                    // Reading json string from server
                    String json_str = "" ;
                    while (fetchedData != -1) {
                        char current = (char) fetchedData;
                        fetchedData = reader.read();
                        json_str = json_str + current;
                    }
                    if(json_str . length() == 0) {
                        w.status = 2 ;
                    } else {
                        w.status = 1 ;
                        json_str = "{ \"bsby\": " + json_str + " }";
                        final JSONObject obj = new JSONObject(json_str);

                        final JSONArray geodata = obj.getJSONArray("bsby");
                        final int n = geodata.length();
                        if (n == 0)
                            return null;
                        for (int i = 0 ; i < n ; i++) {
                            //final JSONObject bsby = obj . getJSONObject("hof_Details") ;
                            final JSONObject bsby = geodata.getJSONObject(i);
                            hosp_names . add(bsby . getString("H_Name")) ;
                            openingTimes . add(bsby . getString("Opening_Time")) ;
                            closingTimes . add(bsby . getString("Closing_Time")) ;
                            descriptions . add(bsby . getString("Description")) ;
                            contactNos . add(bsby . getString("Contact_No")) ;
                            emails . add(bsby . getString("Email")) ;
                            addresses . add(bsby . getString("address")) ;
                            lats . add(bsby . getString("Latitude")) ;
                            lngs . add(bsby . getString("Longitude")) ;
                        }
                    }
                } catch (Exception ex) {
                    ex . printStackTrace();
                    Log.v("BSBY" , "Error sending Login data ...") ;
                } finally {
                    try {
                        if(reader != null)
                            reader.close();
                    } catch(IOException ex) {
                        Log.v("BSBY" , "BufferedReader not closed ...") ;
                    }
                }
                return w;
            }

            @Override
            protected void onPostExecute(wrapper w) {
                super.onPostExecute(w);
                dialog . dismiss() ;
                int data = w . status ;
                if(data == 1) {
                    SaveSharedPreference . setLog(getApplicationContext() , "1");
                    //String text = "Logged in successfully ..." ;
                    //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    ArrayList<String> distances = new ArrayList<>() ;
                    for(int i = 0 ; i < lats . size() ; i ++) {
                        double dist = distance(latitude , longitude , Double . parseDouble(lats . get(i)) , Double . parseDouble(lngs . get(i)) , "K") ;
                        distances . add(dist + "") ;
                    }
                    Intent intent1 = new Intent(HosSearchActivity.this , ListHospitalActivity.class) ;
                    intent1 . putStringArrayListExtra("hosp_names" , hosp_names) ;
                    intent1 . putStringArrayListExtra("openingTimes" , openingTimes) ;
                    intent1 . putStringArrayListExtra("closingTimes" , closingTimes) ;
                    intent1 . putStringArrayListExtra("descriptions" , descriptions) ;
                    intent1 . putStringArrayListExtra("contactNos" , contactNos) ;
                    intent1 . putStringArrayListExtra("emails" , emails) ;
                    intent1 . putStringArrayListExtra("addresses" , addresses) ;
                    intent1 . putStringArrayListExtra("lats" , lats) ;
                    intent1 . putStringArrayListExtra("lngs" , lngs) ;
                    intent1 . putStringArrayListExtra("distances" , distances) ;
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    finish();
                } else {
                    if(data == 2) {
                        String text = "Error in logging in ...";
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        if (checkConnection(getApplicationContext()))
            AsyncTaskCompat.executeParallel(sendPostReqAsyncTask);
        else {
            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            showInternetNotAvailableAlert(HosSearchActivity.this);
        }
    }


    // Function to check Internet connectivity
    boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting() ;
    }

    // Function to show NO INTERNET alert dialog
    public void showInternetNotAvailableAlert(Activity activity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("NO INTERNET")
                    .setMessage("Please enable internet")
                    .setCancelable(true)
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog . cancel() ;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch(Exception e) {
            //
        }
    }

    // Function to check GPS connectivity and show alert dialog if there is no GPS
    public void ShowGPSSettings(Activity activity) {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(! lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // If GPS is disabled
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("NO GPS")
                        .setMessage("Please select High Accuracy Location Mode")
                        .setCancelable(true)
                        .setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog . cancel() ;
                            }
                        })
                        .setNegativeButton("GPS Settings",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) ;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } catch(Exception e) {
                //
            }
        }
    }

    // Function to take run-time permissions in Android Marshmallow
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ShowGPSSettings(HosSearchActivity.this) ;
                this.recreate() ;
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getApplicationContext(), "You must grant permission to access the gps and use map ...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setValues(double longitute,double latitude) {
        List<Address> addresses = null;
        String city ;
        String district ;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(longitute,latitude,1);
            city = addresses.get(0).getLocality();
            district = addresses.get(0).getSubLocality();
            Toast.makeText(this, city + "  "+ district, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e . printStackTrace();
            city = "ENTER CITY HERE";
            district = "ENTER DISTRICT HERE";
        }
        EditText editTextCity = (EditText) findViewById(R.id.city) ;
        editTextCity.setText(city);
        EditText edittextDistrict = (EditText) findViewById(R.id.district) ;
        edittextDistrict.setText(district);
    }

    /*
    // Function to calculate distance between two locations using latitude , longitude
    public double CalculateDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// Radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c * 1000 ;
    }
    */

    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}

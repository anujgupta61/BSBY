package com.what_the_hack.bsby;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn = (Button) findViewById(R. id. verifyButton) ;
        btn . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_user();
            }
        } );
    }

    void verify_user() {
        EditText eClientID = (EditText)findViewById(R.id.client_id) ;
        final String client_id = eClientID  . getText() . toString() ;

        if(client_id . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter Client ID ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        EditText eBhamashahId = (EditText) findViewById(R . id . bhamashah_id) ;
        final String bhamashah_id = eBhamashahId . getText() . toString() ;

        if(bhamashah_id . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter your Bhamashah ID ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        if(bhamashah_id . length() != 7) {
            Toast . makeText(getApplicationContext() , "Please enter 7-character Bhamashah ID ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        class wrapper {
            int status ;
        }

        class SendPostReqAsyncTask extends AsyncTask<String, Void, wrapper> {
            private wrapper w = new wrapper();
            private ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                    "Verifying . Please wait...", true);

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
                    data = URLEncoder.encode("bhamashah_id", "UTF-8")
                            + "=" + URLEncoder.encode(bhamashah_id, "UTF-8") + "&" + URLEncoder.encode("client_id", "UTF-8")
                            + "=" + URLEncoder.encode(client_id, "UTF-8") ;
                } catch (UnsupportedEncodingException e) {
                    Log.v("BSBY" , "Login data not encoded ...") ;
                }
                BufferedReader reader = null;

                // Send data
                try {
                    // Define URL where to send data
                    // https://bsby.000webhostapp.com/fix_appointment.php
                    URL url = new URL("https://apitest.sewadwaar.rajasthan.gov.in/app/live/Service/hofAndMember/ForApp/" + bhamashah_id +"?client_id=" + client_id);

                    // Send POST data request
                    URLConnection conn = url.openConnection();
                    /*
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
                    */
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
                        //json_str = "{ \"bsby\": [" + json_str + "] }";
                        final JSONObject obj = new JSONObject(json_str);
                        /*
                        final JSONArray geodata = obj.getJSONArray("");
                        final int n = geodata.length();
                        if (n == 0)
                            return null;
                        for (int i = 0 ; i < n ; i++) {
                        */
                            final JSONObject bsby = obj . getJSONObject("hof_Details") ;
                            //final JSONObject bsby = geodata.getJSONObject(i);
                            String name = bsby . getString("NAME_ENG") ;
                            String bms_id = bsby . getString("BHAMASHAH_ID") ;
                            String aadhar_id = bsby . getString("AADHAR_ID") ;
                            String dob  = bsby . getString("DOB") ;
                            String eco_gp  = bsby . getString("ECONOMIC_GROUP") ;
                            SaveSharedPreference . setName(getApplicationContext() , name);
                            //SaveSharedPreference . setBmshId(getApplicationContext() , bms_id);
                            SaveSharedPreference . setAadharNo(getApplicationContext() ,  aadhar_id);
                            SaveSharedPreference . setDOB(getApplicationContext() , dob);
                            SaveSharedPreference . setEcoGrp(getApplicationContext() , eco_gp);
                        //}
                    }
                } catch (Exception ex) {
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
                    String text = "Logged in successfully ..." ;
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(LoginActivity.this , UserDetails.class) ;
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
            showInternetNotAvailableAlert(LoginActivity.this);
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
}

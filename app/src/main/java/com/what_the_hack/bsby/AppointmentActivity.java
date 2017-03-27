package com.what_the_hack.bsby;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;

import static com.what_the_hack.bsby.R.id.date;

public class AppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        Button btn = (Button) findViewById(R. id. appointmentButton) ;
        btn . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fix_appointment();
            }
        } );
    }

    void fix_appointment() {
        EditText eName = (EditText)findViewById(R.id.name) ;
        final String name = eName  . getText() . toString() ;

        if(name . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter name ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        EditText econtactNo = (EditText) findViewById(R . id . contact) ;
        final String contact_no = econtactNo . getText() . toString() ;

        if(contact_no . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter your contact number ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        if(contact_no . length() != 10) {
            Toast . makeText(getApplicationContext() , "Please enter 10-digit contact number ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        EditText eDisease = (EditText) findViewById(R . id . disease) ;
        final String disease = eDisease . getText() . toString() ;

        if(disease . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter your disease ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        final String hosName = "BSBY hospital" ; // Get this from calling Intent

        EditText eDate = (EditText) findViewById(date) ;
        final String date = eDate . getText() . toString() ;

        if(date . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter appointment date ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        EditText eTime = (EditText) findViewById(R . id . time) ;
        final String time = eTime . getText() . toString() ;

        if(time . length() == 0) {
            Toast . makeText(getApplicationContext() , "Please enter appointment time ..." , Toast . LENGTH_SHORT) . show() ;
            return ;
        }

        class wrapper {
            int status ;
            int id ;
            String docName ;
            int roomNo ;
        }

        class SendPostReqAsyncTask extends AsyncTask<String, Void, wrapper> {
            private wrapper w = new wrapper();
            private ProgressDialog dialog = ProgressDialog.show(AppointmentActivity.this, "",
                    "Fixing appointment . Please wait...", true);

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
                    data = URLEncoder.encode("name", "UTF-8")
                            + "=" + URLEncoder.encode(name, "UTF-8") + "&" + URLEncoder.encode("contact", "UTF-8")
                            + "=" + URLEncoder.encode(contact_no, "UTF-8") + "&" + URLEncoder.encode("disease", "UTF-8")
                            + "=" + URLEncoder.encode(disease, "UTF-8") + "&" + URLEncoder.encode("hosName", "UTF-8")
                            + "=" + URLEncoder.encode(hosName, "UTF-8") + "&" + URLEncoder.encode("date", "UTF-8")
                            + "=" + URLEncoder.encode(date, "UTF-8") + "&" + URLEncoder.encode("time", "UTF-8")
                            + "=" + URLEncoder.encode(time, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.v("BSBY" , "Appointment data not encoded ...") ;
                }

                BufferedReader reader = null;

                // Send data
                try {
                    // Define URL where to send data
                    // https://hornwave.000webhostapp.com/register_vehicle.php
                    URL url = new URL("https://bsby.000webhostapp.com/fix_appointment.php");

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
                    if(json_str . length() == 1) {
                        w.status = 2;
                    } else {
                        w.status = 1;
                        json_str = "{ \"bsby\": [" + json_str + "] }";
                        final JSONObject obj = new JSONObject(json_str);
                        final JSONArray geodata = obj.getJSONArray("bsby");
                        final int n = geodata.length();
                        if (n == 0)
                            return null;
                        for (int i = 0 ; i < n ; i++) {
                            final JSONObject bsby = geodata.getJSONObject(i);
                            w.id = bsby.getInt("id");
                            w.docName = bsby.getString("doctor_name");
                            w.roomNo = bsby.getInt("room_no");
                        }
                    }
                } catch (Exception ex) {
                    Log.v("BSBY" , "Error sending Appointment data ...") ;
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
                if (data == 1) {
                    String text = "Appointment successfully fixed ..." ;
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(AppointmentActivity.this , AppointmentConfirmation.class) ;
                    intent1 . putExtra("id" , w . id + "") ;
                    intent1 . putExtra("doc_name" , w . docName) ;
                    intent1 . putExtra("room_no" , w. roomNo + "") ;
                    intent1 . putExtra("hos_name" , hosName) ;
                    intent1 . putExtra("pat_name" , name) ;
                    intent1 . putExtra("disease" , disease) ;
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    finish();
                } else {
                    if(data == 2) {
                        String text = "Error in fixing appointment ...";
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
            showInternetNotAvailableAlert(AppointmentActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        int mYear, mMonth, mDay, mHour, mMinute ;
        if (v . getId() == R.id.datePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            EditText txtDate = (EditText) findViewById(R . id . date) ;
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.setTitle("Select Date");
            datePickerDialog.show();
        }
        if (v . getId() == R . id . timePicker) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            EditText txtTime = (EditText) findViewById(R . id . time) ;
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
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

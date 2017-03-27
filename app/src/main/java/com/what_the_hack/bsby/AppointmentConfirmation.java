package com.what_the_hack.bsby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AppointmentConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_confirmation);
        Intent intent = this . getIntent();
        if(intent != null && intent.hasExtra("id")) {
            TextView appointId = (TextView) findViewById(R . id . appoint_id) ;
            String appoint_id = "Appointment ID - " + intent . getStringExtra("id") ;
            appointId . setText(appoint_id) ;
            TextView hosName = (TextView) findViewById(R . id . hos_name) ;
            String hos_name = "Hospital Name - " + intent . getStringExtra("hos_name") ;
            hosName . setText(hos_name) ;
            TextView docName = (TextView) findViewById(R . id . doc_name) ;
            String doc_name = "Doctor Name - " + intent . getStringExtra("doc_name") ;
            docName . setText(doc_name) ;
            TextView patName = (TextView) findViewById(R . id . pat_name) ;
            String pat_name = "Patient Name - " + intent . getStringExtra("pat_name") ;
            patName . setText(pat_name) ;
            TextView disease = (TextView) findViewById(R . id . disease) ;
            String disease_str = "Disease - " + intent . getStringExtra("disease") ;
            disease . setText(disease_str) ;
            TextView roomNo = (TextView) findViewById(R . id . room_no) ;
            String room_no = "Room No. - " + intent . getStringExtra("room_no") ;
            roomNo . setText(room_no) ;
        }
    }
}

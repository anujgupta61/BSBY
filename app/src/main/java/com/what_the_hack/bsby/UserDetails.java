package com.what_the_hack.bsby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SaveSharedPreference.getLog(getApplicationContext()) . length() != 0) {
            setContentView(R.layout.activity_user_details);
            TextView name = (TextView) findViewById(R.id.name) ;
            String t_name = "NAME - " + SaveSharedPreference . getName(getApplicationContext()) ;
            name . setText(t_name) ;
            /*
            TextView bms_id = (TextView) findViewById(R.id.bmsh_id) ;
            String t_bms_id = "BHAMASHAH ID - " + SaveSharedPreference . getBmshId(getApplicationContext()) ;
            Toast.makeText(this, t_bms_id , Toast.LENGTH_SHORT).show();
            bms_id . setText(t_bms_id) ;
            */
            TextView aadhar_no = (TextView) findViewById(R.id.aadhar_no) ;
            String t_aadhar_no = "AADHAR ID - " + SaveSharedPreference . getAadharNo(getApplicationContext()) ;
            aadhar_no . setText(t_aadhar_no) ;
            TextView dob = (TextView) findViewById(R.id.dob) ;
            String t_dob = "DOB - " + SaveSharedPreference . getDOB(getApplicationContext()) ;
            dob . setText(t_dob) ;
            TextView eco_gp = (TextView) findViewById(R.id.eco_group) ;
            String t_eco_gp = "ECONOMIC_GROUP - " + SaveSharedPreference . getEcoGrp(getApplicationContext()) ;
            eco_gp . setText(t_eco_gp) ;
            Button btn = (Button) findViewById(R. id. search_hos) ;
            btn . setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserDetails.this , HosSearchActivity.class) ;
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //finish();
                }
            } );
        } else {
            Intent intent = new Intent(UserDetails.this , LoginActivity.class) ;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}

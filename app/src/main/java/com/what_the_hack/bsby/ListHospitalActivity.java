package com.what_the_hack.bsby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListHospitalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_hospital);
        Intent intent = this . getIntent() ;
        if(intent != null) {
            final ArrayList<String> hosp_names = intent . getStringArrayListExtra("hosp_names") ;
            final ArrayList<String> openingTimes = intent . getStringArrayListExtra("openingTimes") ;
            final ArrayList<String> closingTimes = intent . getStringArrayListExtra("closingTimes") ;
            final ArrayList<String> descriptions = intent . getStringArrayListExtra("descriptions") ;
            final ArrayList<String> contactNos = intent . getStringArrayListExtra("contactNos") ;
            final ArrayList<String> emails = intent . getStringArrayListExtra("emails") ;
            final ArrayList<String> addresses = intent . getStringArrayListExtra("addresses") ;
            final ArrayList<String> lats = intent . getStringArrayListExtra("lats") ;
            final ArrayList<String> lngs = intent . getStringArrayListExtra("lngs") ;
            final ArrayList<String> distances = intent . getStringArrayListExtra("distances") ;
            CustomListAdapter adapter = new CustomListAdapter(ListHospitalActivity.this , hosp_names , addresses , distances , openingTimes , closingTimes) ;
            ListView listview = (ListView) findViewById(R.id.listview) ;
            listview . setOnItemClickListener(new AdapterView.OnItemClickListener () {
                @Override
                public void onItemClick(AdapterView<?> adapterView , View view , int pos , long l) {
                    Intent intent1 = new Intent(ListHospitalActivity.this , HospitalDetails.class) ;
                    intent1 . putExtra("hosp_names" , hosp_names . get(pos)) ;
                    intent1 . putExtra("openingTimes" , openingTimes . get(pos)) ;
                    intent1 . putExtra("closingTimes" , closingTimes . get(pos)) ;
                    intent1 . putExtra("descriptions" , descriptions . get(pos)) ;
                    intent1 . putExtra("contactNos" , contactNos . get(pos)) ;
                    intent1 . putExtra("emails" , emails . get(pos)) ;
                    intent1 . putExtra("addresses" , addresses . get(pos)) ;
                    intent1 . putExtra("lats" , lats . get(pos)) ;
                    intent1 . putExtra("lngs" , lngs . get(pos)) ;
                    intent1 . putExtra("distances" , distances . get(pos)) ;
                    //intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    //finish();
                }
            } ) ;
            try {
                listview.setAdapter(adapter);
            } catch(Exception ex) {
                ex . printStackTrace();
            }
        }
    }
}

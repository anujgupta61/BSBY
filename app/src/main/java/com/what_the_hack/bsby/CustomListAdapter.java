package com.what_the_hack.bsby;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private Activity context ;
    private ArrayList<String> hosp_names = new ArrayList<>() ;
    private ArrayList<String> addresses = new ArrayList<>() ;
    private ArrayList<String> distances = new ArrayList<>() ;
    private ArrayList<String> openingTimes = new ArrayList<>() ;
    private ArrayList<String> closingTimes = new ArrayList<>() ;

    public CustomListAdapter(Activity context, ArrayList<String> hosp_names , ArrayList<String> addresses , ArrayList<String> distances , ArrayList<String> openingTimes , ArrayList<String> closingTimes) {
        super(context, R.layout.list_item , hosp_names);
        this . context = context;
        this . hosp_names = hosp_names ;
        this . addresses = addresses ;
        this . distances = distances ;
        this . openingTimes = openingTimes ;
        this . closingTimes = closingTimes ;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);
        TextView hspName = (TextView) rowView.findViewById(R.id.hsp_name);
        TextView addr = (TextView) rowView.findViewById(R.id.hsp_addr);
        TextView dist = (TextView) rowView.findViewById(R.id.dist);
        TextView times = (TextView) rowView . findViewById(R.id.time) ;
        hspName . setText(hosp_names . get(position));
        addr . setText(addresses . get(position));
        double tdist = Double . parseDouble(distances . get(position)) ;
        double roundOff = Math.round(tdist * 100.0) / 100.0;
        String temp_dist = roundOff + " KM" ;
        dist . setText(temp_dist);
        times . setText(openingTimes . get(position) + " - " + closingTimes . get(position));
        return rowView;
    }
}
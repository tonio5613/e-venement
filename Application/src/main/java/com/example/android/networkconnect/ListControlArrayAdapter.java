package com.example.android.networkconnect;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by adonniou on 27/05/15.
 */
public class ListControlArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<ControlTic> ListControl;
   private int taille;
    private static final String TAG = "EDroide";


    public ListControlArrayAdapter(Context context, ArrayList ListControl) {
        super(context, -1, ListControl);
        this.context = context;
        this.ListControl=ListControl;
        taille=ListControl.size();

//        Collections.reverse(ListControl);
    }

    @Override
    public void insert(String object, int index) {
        super.insert(object, index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listcontrole, parent, false);

        Log.i(TAG,"position: "+position);

        TextView controltic_timestamp = (TextView) rowView.findViewById(R.id.controltic_timestamp);
        //TextView controltic_message = (TextView) rowView.findViewById(R.id.controltic_message);
        TextView controltic_erreur = (TextView) rowView.findViewById(R.id.controltic_erreurs);
        TextView controltic_ticket = (TextView) rowView.findViewById(R.id.controltic_ticket);
        TextView controltic_gauge = (TextView) rowView.findViewById(R.id.controltic_gauge);
        TextView controltic_manifestation = (TextView) rowView.findViewById(R.id.controltic_manifestation);

        if(ListControl.get(position).getDETAILS_CONTROL_ERRORS()!="")
        {
            controltic_timestamp.setTextColor(Color.RED);
            controltic_erreur.setTextColor(Color.RED);
            controltic_ticket.setTextColor(Color.RED);
            controltic_gauge.setTextColor(Color.RED);
            controltic_manifestation.setTextColor(Color.RED);
        }

        controltic_timestamp.setText(ListControl.get(position).getTIMESTAMP()+" "+ListControl.get(position).getMESSAGE());
        //controltic_message.setText(ListControl.get(position).getMESSAGE());
        controltic_erreur.setText(ListControl.get(position).getDETAILS_CONTROL_ERRORS());
        String infotic=ListControl.get(position).getTICKETS_VALUE_TXT()+" "+ListControl.get(position).getTICKETS_PRICE()+" Billet: #"+ListControl.get(position).getTICKETS_ID()+" ("+ListControl.get(position).getTICKETS_USERS()+")";
        controltic_ticket.setText(infotic);
        controltic_gauge.setText(ListControl.get(position).getTICKETS_GAUGE());
        controltic_manifestation.setText(ListControl.get(position).getTICKETS_MANIFESTATION());

        return rowView;
    }
}
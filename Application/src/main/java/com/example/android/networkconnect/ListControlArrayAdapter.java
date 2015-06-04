package com.example.android.networkconnect;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by adonniou on 27/05/15.
 * class
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
        ImageView controltic_image = (ImageView) rowView.findViewById(R.id.controltic_picture);
        RelativeLayout relativeLayout=(RelativeLayout) rowView.findViewById(R.id.controltic_group);

        controltic_timestamp.setTypeface(null, Typeface.BOLD);
        controltic_erreur.setTypeface(null, Typeface.BOLD);
        controltic_ticket.setTypeface(null, Typeface.BOLD);
        controltic_gauge.setTypeface(null, Typeface.BOLD);
        controltic_manifestation.setTypeface(null, Typeface.BOLD);

        if(ListControl.get(position).getDETAILS_CONTROL_ERRORS()!="")
        {
            controltic_image.setImageResource(R.drawable.ic_cancel_black_48dp);
            relativeLayout.setBackgroundColor(Color.parseColor("#e57373"));

        }

        else {
            relativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
            controltic_image.setImageResource(R.drawable.ic_check_circle_black_48dp);

        }
            controltic_timestamp.setText(ListControl.get(position).getTIMESTAMP() + " " + ListControl.get(position).getMESSAGE());
            //controltic_message.setText(ListControl.get(position).getMESSAGE());
            controltic_erreur.setText(ListControl.get(position).getDETAILS_CONTROL_ERRORS());
            String infotic = ListControl.get(position).getTICKETS_VALUE_TXT() + " " + ListControl.get(position).getTICKETS_PRICE() + " Billet: #" + ListControl.get(position).getTICKETS_ID() + " (" + ListControl.get(position).getTICKETS_USERS() + ")";
            controltic_ticket.setText(infotic);
            controltic_gauge.setText(ListControl.get(position).getTICKETS_GAUGE());
            controltic_manifestation.setText(ListControl.get(position).getTICKETS_MANIFESTATION());

        return rowView;
    }
}
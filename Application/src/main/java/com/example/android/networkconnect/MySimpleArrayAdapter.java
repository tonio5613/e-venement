package com.example.android.networkconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adonniou on 27/05/15.
 */
public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
   // private final String[] values;
    private final int l;
    private final ArrayList<ControlTic> ListControl;

//    private final ControlTic controlTic;

    public MySimpleArrayAdapter(Context context, ArrayList ListControl) {
        super(context, -1, ListControl);
        this.context = context;
        this.l = ListControl.size();
        this.ListControl=ListControl;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listcontrole, parent, false);
        TextView timestamp = (TextView) rowView.findViewById(R.id.controltic_timestamp);
        TextView message = (TextView) rowView.findViewById(R.id.controltic_message);
        TextView erreur = (TextView) rowView.findViewById(R.id.controltic_erreurs);


        timestamp.setText(ListControl.get(position).getTIMESTAMP());
        message.setText(ListControl.get(position).getMESSAGE());
        // change the icon for Windows and iPhone


        return rowView;
    }
}
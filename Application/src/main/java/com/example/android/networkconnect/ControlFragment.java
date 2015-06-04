/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.networkconnect;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import static java.util.Collections.*;

/**
 * Simple fragment containing only a TextView. Used by TextPagerAdapter to create
 * tutorial-style pages for apps.
 */
public class ControlFragment extends Fragment {

    // Contains the text that will be displayed by this Fragment
    String mText;
    // Contains a resource ID for the text that will be displayed by this fragment.
    int mTextId = -1;

    // Keys which will be used to store/retrieve text passed in via setArguments.
    public static final String TEXT_KEY = "text";
    public static final String TEXT_ID_KEY = "text_id";
    public static final String JSON_CHECKPOINT="json_checkpoint";


    public String [] listcheckpoint;
    public String checkpoint="Merci de sélectionner un point de controle";
    public String num_checkpoint;



    private static JSONObject json;

        //List des contoles
    private final ArrayList<ControlTic> ListArrayControl= new ArrayList<ControlTic>();
    private final ArrayList<ControlTic> ListArrayControlOrdre= new ArrayList<ControlTic>(ListArrayControl.size());

    private static final String TAG = "EDroide";

    public String apidev6="https://dev3.libre-informatique.fr/tck.php/ticket/control/action";

    public String scan_controle1="";

    View createdView;

    private TextView mTextView;

    private String message="";

    Spinner spinner_checkpoint;

    ListControlArrayAdapter list_adapter;

    EditText ET_scan_controle;

    ListView list_controle;


    public ControlFragment() {

    }

    private static JSONObject convertInputStreamToJson(InputStream inputStream) throws IOException{

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        JSONObject jsonobjet=null;

        try {
            jsonobjet=new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonobjet;

    }

    public static String getStringFromInputStream(InputStream stream) throws IOException
    {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

    private class ControlTaskHttps extends AsyncTask<String, Void, ControlTic> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ControlTic doInBackground(String...urls) {
            try {

                return https_control(urls[0]);
            } catch (IOException e) {
                //Log.i(TAG, "Erreur connection: "+e);
                ControlTic tic=null;
                return tic;
            }
        }

        @Override
        protected void onPostExecute(ControlTic result) {

            ListArrayControl.add(result);
            ListArrayControlOrdre.clear();

            //Log.i(TAG,"Taille tab: "+ListArrayControl.size());

            if(ListArrayControl.size()>1)
            {
                try {


                    for (int index=(ListArrayControl.size()-1);index>=0;index--)
                    {
                        //Log.i(TAG,"tabordre time "+index+":"+ListArrayControl.get(index).getTIMESTAMP());
                        ListArrayControlOrdre.add(ListArrayControl.get(index));

                    }

                    list_adapter = new ListControlArrayAdapter(getActivity(),ListArrayControlOrdre);
                } catch (Exception e) {
                    Log.i(TAG, "Erreur ListArray: "+e);
                }

            }
            else
            {
                list_adapter = new ListControlArrayAdapter(getActivity(),ListArrayControl);
            }

            list_controle.setAdapter(list_adapter);
            ET_scan_controle.setText("");

        }
    }

    private ControlTic https_control (String urlString) throws IOException {

        ControlTic mControlTic=new ControlTic();

        URL url = new URL(urlString);

        Log.i(TAG, "Protocol: "+url.getProtocol().toString());

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setReadTimeout(20000 /* milliseconds */);
        conn.setConnectTimeout(25000 /* milliseconds */);
        // conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setChunkedStreamingMode(0);

        conn.setRequestProperty("User-Agent", "e-venement-app/0.1");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //On cr�e la liste qui contiendra tous nos param�tres

        //Et on y rajoute nos param�tres
        nameValuePairs.add(new BasicNameValuePair("control[ticket_id]", scan_controle1));
        nameValuePairs.add(new BasicNameValuePair("control[checkpoint_id]", num_checkpoint));

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer2 = new BufferedWriter(
        new OutputStreamWriter(os, "UTF-8"));
        writer2.write(getQuery(nameValuePairs));
        writer2.flush();
        //writer2.close();
        //os.close();

        conn.connect();

        String headerName = null;

        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
        {
            Log.i (TAG,headerName+": "+conn.getHeaderField(i));
        }

        if (conn.getInputStream()!=null)
        {
            JSONObject jsonObject;

            try {
                jsonObject=  convertInputStreamToJson(conn.getInputStream());
                //
                Log.i(TAG, "JSON: "+jsonObject.toString());
                mControlTic.setJSONOBJET(jsonObject);
                //Log.i(TAG, "JSON: "+mControlTic.getSUCCESS());
            } catch (Exception e) {
                Log.i(TAG, "erreur json controletic: "+e);
            }

        }
        else
        {
            mControlTic=null;
            Log.i(TAG, "No InputStream: ");
        }

        return mControlTic;
    }


    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
            Bundle savedInstanceState) {

        // Before initializing the textView, check if any arguments were provided via setArguments.
        processArguments();

        createdView = inflater.inflate(R.layout.controle_layout, container, false);


        list_controle=(ListView) createdView.findViewById(R.id.listControle);

        ET_scan_controle =(EditText) createdView.findViewById(R.id.code_contole);


        spinner_checkpoint = (Spinner) createdView.findViewById(R.id.spinner_checkpoint);

        final ArrayAdapter<String> checkpoint_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                listcheckpoint);

        checkpoint_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_checkpoint.setAdapter(checkpoint_adapter);

        ET_scan_controle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_controle1 = ET_scan_controle.getText().toString();
                if (scan_controle1.length()!=0) {
                    try {
                        new ControlTaskHttps().execute(apidev6);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    ET_scan_controle.setText("");
                }
            }
        });

        spinner_checkpoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                try
                {
                    Log.i(TAG,"Selection: "+listcheckpoint[position]);
                    checkpoint=listcheckpoint[position];
                    num_checkpoint="";
                    Pattern pattern = Pattern.compile("(\\d+)");
                    Matcher m = pattern.matcher(checkpoint);

                    while(m.find()) {
                        Log.i(TAG, m.group());
                        num_checkpoint=num_checkpoint+m.group();
                    }
                    Log.i(TAG, "numero: "+num_checkpoint);


                }
                catch (Exception e)
                {
                    Log.i(TAG,"Erreur de selection"+e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        if(listcheckpoint.length<=1)
        {
            spinner_checkpoint.setVisibility(createdView.GONE);
        }


        // Create a new TextView and set its text to whatever was provided.

        if (message != null) {
            try {

                Log.i("SimpleTextFragment", message);
            } catch (Exception e) {
                Log.i(TAG, "Erreur affichage:"+e);
            }
        }
        return createdView;
    }



    public TextView getTextView() {
        return mTextView;
    }

    /**
     * Changes the text for this TextView, according to the resource ID provided.
     * @param stringId A resource ID representing the text content for this Fragment's TextView.
     */
    public void setText(int stringId) {
        getTextView().setText(getActivity().getString(stringId));
    }

    /**
     * Processes the arguments passed into this Fragment via setArguments method.
     * Currently the method only looks for text or a textID, nothing else.
     */
    public void processArguments() {
        // For most objects we'd handle the multiple possibilities for initialization variables
        // as multiple constructors.  For Fragments, however, it's customary to use
        // setArguments / getArguments.

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(JSON_CHECKPOINT))
            {
                try {
               // Log.i(TAG,args.getString(JSON_CHECKPOINT));
                JSONArray Js=null;

                //Js.getJSONArray(args.getString(JSON_CHECKPOINT));
                String data=args.getString(JSON_CHECKPOINT);
                //Log.i(TAG, "Arg_checkpoint: "+data);

                JSONObject checkpoints=new JSONObject(data);
                //Log.i(TAG, "JSON String: " + checkpoints.toString());

                JSONArray tab_checkpoints=new JSONArray();
                tab_checkpoints= checkpoints.names();

                    String tab[]=null;
                    int var=0;
                    Log.i(TAG, "Longueur tab: "+tab_checkpoints.length());
                    if(tab_checkpoints.length()>1) {

                        listcheckpoint = new String[tab_checkpoints.length() + 1];
                        listcheckpoint[0]= "Selection d'un point de controle";
                        var = 1;

                        for(int i=0;i<tab_checkpoints.length();i++)
                        {

                            //Log.i(TAG,"Tableau JSON: "+tab_checkpoints.get(i).toString());

                            //Log.i(TAG,"Valeur JSON: "+checkpoints.get(tab_checkpoints.get(i).toString()));
                            String entree=tab_checkpoints.get(i).toString()+" "+checkpoints.get(tab_checkpoints.get(i).toString());

                            listcheckpoint[i+var]=entree;
                        }

                    }

                    if(tab_checkpoints.length()<=1)
                    {
                        //spinner_checkpoint.setVisibility(View.GONE);
                       listcheckpoint = new String[tab_checkpoints.length()];
                       num_checkpoint=tab_checkpoints.get(0).toString();
                       Log.i(TAG, "Checkpoint: "+num_checkpoint);

                    }



                } catch (Exception e) {
                Log.i(TAG,"Erreur Tableau JSON: "+e.toString());
                e.printStackTrace();
            }
            }
            if (args.containsKey(TEXT_KEY)) {
                mText = args.getString(TEXT_KEY);
                Log.d("Constructor", "Added Text.");
            } else if (args.containsKey(TEXT_ID_KEY)) {
                mTextId = args.getInt(TEXT_ID_KEY);
                mText = getString(mTextId);
            }
        }
    }


}
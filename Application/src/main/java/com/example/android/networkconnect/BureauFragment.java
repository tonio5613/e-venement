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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Simple fragment containing only a TextView. Used by TextPagerAdapter to create
 * tutorial-style pages for apps.
 */
public class BureauFragment extends Fragment {

    // Contains the text that will be displayed by this Fragment
    String mText;
    Drawable mDrawable;
    // Contains a resource ID for the text that will be displayed by this fragment.
    int mTextId = -1;
    int mDrawableId=-1;
    // Keys which will be used to store/retrieve text passed in via setArguments.
    public static final String TEXT_KEY = "text";
    public static final String TEXT_ID_KEY = "text_id";
    public static final String ERRORS_LOG = "errors_log";

    public static final Drawable DRAWABLE_KEY=null;
    public static final Drawable DRAWABLE_ID_KEY=null;
    public ControlFragment mControlFragment;
    View layout;
    private static final String TAG = "EDroide";
    private JSONObject jsonLog;
    private String log_verif="";
    public String URL="";
    private String tls="";

    private static String JSON_CHECKPOINT="json_checkpoint";

    public BureauFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Before initializing the textView, check if any arguments were provided via setArguments.

        processArguments();

        layout = inflater.inflate(R.layout.bureaulayout, container, false);

        final Button scan_button = (Button) layout.findViewById(R.id.lauchscan);

        scan_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL=url();
                    if(URL!="") {
                        new CheckpointTaskHttps().execute(URL);
                    }
                    else
                    {
                        //LoginDialog mLogin = new LoginDialog();
                        //mLogin.show(getActivity().getSupportFragmentManager(), "LoginDialog");

                    }
                } catch (Exception e) {
                    Show_Connection_error_Dialog();
                    Log.i(TAG, "Erreur CheckpointTaskHttps: "+e);
                }
            }
        });

        if (mText != null) {

            Log.i("SimpleTextFragment", mText);
        }
        return layout;
    }

    private String url ()
    {
        String url="";

        try {

            JSONObject object = Read_log(getActivity());

            Log.i(TAG, "Json: "+object.toString());

            if(object.getString("log_verif")!="")
            {

                Log.i(TAG,"Log verif: "+log_verif);

                // https://dev3.libre-informatique.fr/tck.php/ticket/checkpointAjax

                url=object.getString("hote");
                url=url+"/tck.php/ticket/checkpointAjax";
                tls=object.getString("tls");

            }
            else
            {
                url="faux";
            }


        } catch (Exception e) {
            Log.i(TAG, "Erreur lecture: "+e);
        }
        return url;
    }

    public void Show_Connection_error_Dialog()
    {
        final ArrayList seletedItems=new ArrayList();
        String messageText= getResources().getString(R.string.connection_error_txt);
        //final CharSequence[] items = {checkboxText};
        final boolean[] checker={true};
        final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.connection_error);
        builder.setMessage(messageText);
        builder.setNeutralButton(R.string.Continuer,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.Quitter,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        builder.show();
    }

    private class CheckpointTaskHttps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {

                return https_Checkpoint(urls[0]);

                // return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.i(TAG, "Erreur connection: "+e);

                return "";
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
             //Log.i(TAG, "PostResult: "+result);
            //affichage du resultat dans un toast

            try {
                JSONObject object= new JSONObject(result);

                Bundle arg = new Bundle();
                try {

                    arg.putString(JSON_CHECKPOINT, result);

                } catch (Exception e) {
                    Log.i(TAG, "Erreur Put arg : " + e);
                }
                final FragmentManager fm = getActivity().getSupportFragmentManager();
                final FragmentTransaction ft = fm.beginTransaction();
                mControlFragment = (ControlFragment) new ControlFragment();
                mControlFragment.setArguments(arg);

                ft.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

                ft.replace(R.id.intro_fragment, mControlFragment);

                ft.addToBackStack(null);

                ft.commit();
            } catch (JSONException e) {

                //Affichage message d'absence de manifestation
                final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());

                builder.setMessage(R.string.no_checkpoint);
                builder.setTitle(R.string.info_titre);
                builder.setNeutralButton(R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        }
    }

    private String https_Checkpoint (String urlString) throws  IOException {

        String result="";
        URL url = new URL(urlString);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setReadTimeout(6000 /* milliseconds */);
        conn.setConnectTimeout(5000 /* milliseconds */);
        // conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setChunkedStreamingMode(0);

        conn.setRequestProperty("User-Agent", "e-venement-app/0.1");

        conn.connect();


        if (conn.getInputStream()!=null)
        {

            try {
                result=  getStringFromInputStream(conn.getInputStream());
            } catch (Exception e) {
                Log.i(TAG, "erreur InputStream: " + e);
            }

        }

        return result;
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

    public JSONObject Read_log(Context context)
    {
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

        JSONObject json=null;

        try{
            fIn = context.openFileInput("settings.txt");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            json=new JSONObject(data);
            Log.i(TAG, "JSON: "+json.toString());
        }
        catch (Exception e) {
            //Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
        }
        return json;
    }

    public void processArguments() {

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(ERRORS_LOG)) {
                String errors = args.getString(ERRORS_LOG);
                DialogFragment mLoginDialog = new LoginDialog();
                mLoginDialog.show(getActivity().getSupportFragmentManager(), "LoginDialog");

            } else if (args.containsKey(TEXT_ID_KEY)) {
                mTextId = args.getInt(TEXT_ID_KEY);
                mText = getString(mTextId);
            }
        }
    }

}
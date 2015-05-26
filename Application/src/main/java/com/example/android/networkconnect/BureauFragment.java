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

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
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

    public static final Drawable DRAWABLE_KEY=null;
    public static final Drawable DRAWABLE_ID_KEY=null;
    private Layout ll;
    private FragmentActivity fa;
    public ControlFragment mControlFragment;
    View layout;
    // For situations where the app wants to modify text at Runtime, exposing the TextView.
    private TextView mTextView;
    private static final String TAG = "EDroide";

    public String apidev8="https://dev3.libre-informatique.fr/tck.php/ticket/checkpointAjax";

    private static String JSON_CHECKPOINT="json_checkpoint";

    public BureauFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Before initializing the textView, check if any arguments were provided via setArguments.

        processArguments();
fa=super.getActivity();

        layout = inflater.inflate(R.layout.bureaulayout, container, false);
        //final imageButton;
        //=(ImageButton)findViewById(R.id.lauchscan);


        final Button scan_button = (Button) layout.findViewById(R.id.lauchscan);
        //Log.i(TAG, "numéro du bouton: "+scan_button.getId());
        scan_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "Bonton control OK");
                try {
                    new CheckpointTaskHttps().execute(apidev8);
                } catch (Exception e) {
                    Log.i(TAG, "Erreur CheckpointTaskHttps: "+e);
                }
            }
        });

        if (mText != null) {
            mTextView.setText(mText);
            Log.i("SimpleTextFragment", mText);
        }
        //return inflater.inflate(R.layout.bureaulayout,container,false);
        //return mDrawableView;
        return layout;
    }


    private class CheckpointTaskHttps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {

                return https_Checkpoint(urls[0]);

                // return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.i(TAG, "Erreur connection: "+e);
                return null;
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
            Bundle arg=new Bundle();
            try {

                arg.putString(JSON_CHECKPOINT,result);

            } catch (Exception e) {
                Log.i(TAG, "Erreur Put arg : "+e);
            }


            final FragmentManager fm = getActivity().getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            mControlFragment=(ControlFragment) new ControlFragment();
            mControlFragment.setArguments(arg);

            ft.setCustomAnimations(android.R.anim.slide_in_left,
              android.R.anim.slide_out_right);

        ft.replace(R.id.intro_fragment, mControlFragment);

        ft.addToBackStack(null);

        ft.commit();

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


            //Log.i(TAG, "Checkpoint:"+json.toString());

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
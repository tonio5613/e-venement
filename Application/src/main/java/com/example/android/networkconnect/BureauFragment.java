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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    public String cookie;

    private User user ;

    private static String JSON_CHECKPOINT="json_checkpoint";

    public static final String LOG_ERREURS="log_erreur";

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

            if(object.getString("log_verif")!="faux")
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

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls) {
            if (urls[0].contains("https")) {
                try {

                    return login_control(urls[0]);
                } catch (IOException e) {
                    return getString(R.string.connection_error);
                }
            }

            else
            {
                try {

                    return login_control_http(urls[0]);
                } catch (IOException e) {
                    return getString(R.string.connection_error);
                }
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);

        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */

        @Override
        protected void onPostExecute(String result) {

            if (result.contains("Le mot de passe ou l'identifiant est invalide.")||result.contains(getString(R.string.connection_error))||result.contains("erreur connection")||result.contains("erreur unknownHostException"))
            {

                try {
                    jsonLog.put("log_verif","faux");
                    //  Save_log(getActivity(), jsonLog);
                } catch (Exception e) {
                    Log.i(TAG, "Erreur sauvegarde: "+e);
                    e.printStackTrace();
                }

                LoginDialog mlogindialog = new LoginDialog();
                //mlogindialog.setArguments(arg);
                mlogindialog.show(getActivity().getSupportFragmentManager(), "LoginDialog");
            }
            else
            {
                        String name=null;
                    try {
                        jsonLog.put("tls", "no_certif");
                        jsonLog.put("log_verif", "vrai");
                        name = jsonLog.getString("login");
                    } catch (JSONException e) {
                        Log.i(TAG,"Erreur json: "+e);
                    }


                Log.i(TAG, "Pas erreur?: "+result);
                Save_log(getActivity(),jsonLog);



                final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.info_titre);
                builder.setMessage("Bienvenue "+name);
                builder.setNeutralButton(R.string.Continuer,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }

        }
    }
    /**
     *Vérification de l'utilisateur, protocol http
     **/

    private String login_control_http (String urlString) throws  IOException {
        int count;
        String token="";
        URL url = new URL(urlString);

        Log.i(TAG, "Protocol http: "+url.getProtocol().toString());
        SSLHosts();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();


            conn.setReadTimeout(2000 /* milliseconds */);
            conn.setConnectTimeout(2500 /* milliseconds */);
            // conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(0);

            conn.setRequestProperty("User-Agent", "e-venement-app/0.1");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //On cr�e la liste qui contiendra tous nos param�tres

            //Et on y rajoute nos param�tres

            nameValuePairs.add(new BasicNameValuePair("signin[username]", user.getLOGIN()));
            nameValuePairs.add(new BasicNameValuePair("signin[password]", user.getPASS()));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer2.write(getQuery(nameValuePairs));
            writer2.flush();

            conn.connect();

            String headerName = null;
            long total = 0;
            for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
            {
                Log.i (TAG,headerName+": "+conn.getHeaderField(i));
            }

            int responseCode = conn.getResponseCode();

            if(responseCode == conn.HTTP_OK) {
                final String COOKIES_HEADER = "Set-Cookie";
                cookie = conn.getHeaderField(COOKIES_HEADER);
            }

            if (conn.getInputStream()!=null)
            {

                token=conn.getHeaderField(1);

                Log.i(TAG,getStringFromInputStream(conn.getInputStream()));
                try
                {
                    String erreur="";
                    Pattern pattern=null;
                    pattern = Pattern.compile("Le mot de passe ou l'identifiant est invalide.");
                    Matcher m=null;
                    m = pattern.matcher(readIt(conn.getInputStream(),2500));

                    while(m.find()) {
                        Log.i(TAG, m.group());
                        erreur=erreur+m.group();
                    }

                    if (erreur!="")
                    {
                        token=erreur;
                    }

                    Log.i(TAG, "Erreur connection: "+erreur);


                }
                catch (Exception e)
                {
                    Log.i(TAG,"Erreur de selection"+e);
                }
            }

        }
        catch (UnknownHostException unknownHostException) {
            Log.i(TAG, "erreur unknownHostException:"+unknownHostException);
            return "erreur unknownHostException";
        }

        catch (IOException e) {
            Log.i(TAG, "erreur id:"+e);
            return "erreur connection";
        }
        return token;
    }

    /**
     *Vérification de l'utilisateur, protocol https
     **/

    private String login_control (String urlString) throws  IOException {
        int count;
        String token="";
        URL url = new URL(urlString);

        Log.i(TAG, "Protocol https: "+url.getProtocol().toString());

        SSLHosts();

        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) url.openConnection();

            conn.setReadTimeout(2000 /* milliseconds */);
            conn.setConnectTimeout(2000 /* milliseconds */);
            // conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(0);

            conn.setRequestProperty("User-Agent", "e-venement-app/0.1");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //On cr�e la liste qui contiendra tous nos param�tres

            //Et on y rajoute nos param�tres

            try {
                nameValuePairs.add(new BasicNameValuePair("signin[username]", jsonLog.getString("login")));
                nameValuePairs.add(new BasicNameValuePair("signin[password]", jsonLog.getString("pass")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer2.write(getQuery(nameValuePairs));
            writer2.flush();
            //writer2.close();
            //os.close();

            conn.connect();

            String headerName = null;
            long total = 0;
            for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
            {
                Log.i (TAG,headerName+": "+conn.getHeaderField(i));
            }

            int responseCode = conn.getResponseCode();

            if(responseCode == conn.HTTP_OK) {
                final String COOKIES_HEADER = "Set-Cookie";
                cookie = conn.getHeaderField(COOKIES_HEADER);
            }

            if (conn.getInputStream()!=null)
            {
                token=conn.getHeaderField(1);

                try
                {
                    String erreur="";
                    //Pattern pattern = Pattern.compile(!<input\s+.*id="signin_username".*\sname="signin[username]"\s.*/>!);
                    Pattern pattern = Pattern.compile("Le mot de passe ou l'identifiant est invalide.");
                    Matcher m = pattern.matcher(getStringFromInputStream(conn.getInputStream()));

                    while(m.find()) {
                        Log.i(TAG, m.group());
                        erreur=erreur+m.group();
                    }
                    if (erreur!="")
                    {
                        token=erreur;
                    }
                }
                catch (Exception e)
                {
                    Log.i(TAG,"Erreur de selection"+e);
                }
            }

        }
        catch (UnknownHostException unknownHostException) {
            Log.i(TAG, "erreur unknownHostException:"+unknownHostException);
            return "erreur unknownHostException";
        }

        catch (IOException e) {
            Log.i(TAG, "erreur id:"+e);
            return "erreur connection";
        }
        return token;
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

    private static void SSLHosts() {

        X509TrustManager easyTrustManager=new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };


        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{easyTrustManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Erreur trustManager: "+e);
        }
    }

    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        char[] buffer = new char[0];
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private void Save_log (Context context,JSONObject sav_log)
    {
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        if(Read_log(context)!=null)
        {
            context.deleteFile("settings.txt");

        }
        else {
        }
            try {

                fOut = context.openFileOutput("settings.txt", Context.MODE_APPEND);

                osw = new OutputStreamWriter(fOut);

                osw.write(sav_log.toString());
                osw.flush();
                //popup surgissant pour le résultat
                Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    osw.close();
                    fOut.close();
                } catch (IOException e) {
                    Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
                }
            }

    }

    public void processArguments() {

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey("USER")){

                try {
                    jsonLog=null;
                    jsonLog= new JSONObject(args.getString("USER"));
                    Log.i(TAG,"Argument: "+jsonLog.toString());

                    if(jsonLog.getString("login")==""||jsonLog.getString("hote")==""||jsonLog.getString("pass")=="")
                    {
                        DialogFragment mLoginDialog = new LoginDialog();
                        mLoginDialog.show(getActivity().getSupportFragmentManager(), "LoginDialog");
                    }
                    else
                    {

                    }
                } catch (JSONException e) {
                    Log.i(TAG, "Erreur: json"+e);
                    DialogFragment mLoginDialog = new LoginDialog();
                    mLoginDialog.show(getActivity().getSupportFragmentManager(), "LoginDialog");
                }

                try {

                    new LoginAsyncTask().execute(jsonLog.getString("hote"));

                } catch (Exception e) {
                    Log.i(TAG, "Erreur: "+e);
                }
            }

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
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import android.hardware.usb.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Sample application demonstrating how to connect to the network and fetch raw
 * HTML. It uses AsyncTask to do the fetch on a background thread. To establish
 * the network connection, it uses HttpURLConnection.
 *
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */

public class MainActivity extends FragmentActivity                      {

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private static final String ACTION_USB_DEVICE_DETACHED =
            "com.android.example.USB_DEVICE_DETACHED";

    private LogFragment mLogFragment;
    public ControlFragment mcontrolFragment;
    private BureauFragment mbureauFragment;
    public boolean UseNoCertifTLS=true;
    private static String JSON_CHECKPOINT="json_checkpoint";

    private static final String TAG = "EDroide";

    public String cookie;

    public User user=new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            //initialisation du CookieManager
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(new CookieManager());


            this.setContentView(R.layout.mainlayout);
            //initialisation des écrans
            setupFragments();
            //Affichage de l'écran du bureau principal
            showFragment(mbureauFragment, null);

        //Vérification si connection internet active

        if (isConnected() != true) {

        } else {

            //Lecture des informations
            if (Read_log(this) == null) {
                showDialog();
            } else {
                JSONObject UserJson = Read_log(this);

                try {
                    user.setUser(UserJson.getString("login"), UserJson.getString("pass"), UserJson.getString("hote"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //
                    new ConnectTaskHttps().execute("https://dev3.libre-informatique.fr/");
                    //new ConnectTaskHttps().execute("https://" + user.getHOTE() + ".libre-informatique.fr/");
                } catch (Exception e) {
                    Log.i(TAG, "Erreur de connection: " + e);
                    e.printStackTrace();
                }
            }

        }
    }
   /**
    *Connection internet présente
    **/
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /**
     *Fonctions d'affichage des écrans et du menu
     *
     **/


    /**
    *Affichage de la boite de dialogue d'identification de l'utilisateur
    **/

    private void showDialog()
    {
        //initialisation de la boite de dialog login
        DialogFragment mLoginDialog = new LoginDialog();
        mLoginDialog.show(getSupportFragmentManager(), "LoginDialog");
    }

    private void SSLshowDialog()
    {
        final ArrayList seletedItems=new ArrayList();
        String checkboxText= getResources().getString(R.string.ssl_check);
        final CharSequence[] items = {checkboxText};
        final AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle(R.string.ssl_titre);
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                            UseNoCertifTLS=true;

                        } else if (seletedItems.contains(indexSelected)) {
                            UseNoCertifTLS=false;
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                });
        builder.setNeutralButton(R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.Annuler,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    /*
     *Initialisation des fragments
     **/
    public void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();

        this.mbureauFragment = (BureauFragment) new BureauFragment();
        if (this.mbureauFragment == null) {
            this.mbureauFragment = new BureauFragment();
        }

        this.mcontrolFragment = (ControlFragment) new ControlFragment();
        if (this.mcontrolFragment == null) {
            this.mcontrolFragment = new ControlFragment();
        }
    }

    /**
     *Changement de l'affichage des fragments
     **/

    public void showFragment(Fragment newfragment, Bundle arg) {
        if (newfragment == null)
            return;

        final FragmentManager fm = getSupportFragmentManager();

        final FragmentTransaction ft = fm.beginTransaction();

        if(newfragment==mcontrolFragment) {
            if (arg != null) {
                if (arg.getString(JSON_CHECKPOINT) != null) {
            newfragment.setArguments(arg);
                }
            }
                else {
            newfragment=mbureauFragment;
                }
        }

        ft.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        ft.replace(R.id.intro_fragment, newfragment);

        ft.addToBackStack(null);

        ft.commit();

    }

    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            showFragment(mbureauFragment, null);
        } else {
            super.onBackPressed();
        }
    }

   /**
    *Création de la barre de menu
    **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

   /**
    *
    **/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.newlog:
                //rentrer un nouveau mot de passe
                showDialog();
                return true;

            case R.id.clear_action:
                //Quitter le programme
                this.finish();
                return true;
            case R.id.menu:
                //Afficher l'écran de controle de billet
                showFragment(mbureauFragment,null);
                return true;
            case R.id.ssl:
                SSLshowDialog();
                return true;
        }
        return false;
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

        }
        catch (Exception e) {
            Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
        }
        return json;
    }


    private class ConnectTaskHttps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (UseNoCertifTLS!=false) {
                try {

                    return https_test(urls[0]);

                    // return loadFromNetwork(urls[0]);
                } catch (IOException e) {
                    Log.i(TAG, "Erreur connection: " + e);
                    return getString(R.string.connection_error);
                }
            }
            else
            {
                try {

                    return http_test(urls[0]);

                    // return loadFromNetwork(urls[0]);
                } catch (IOException e) {
                    Log.i(TAG, "Erreur connection: " + e);
                    return getString(R.string.connection_error);
                }
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
           // Log.i(TAG, result);
            //affichage du resultat dans un toast

            //try {
          //      json= new JSONObject(result);
            //   Log.i(TAG, "Json: "+json.length());
            //} catch (JSONException e) {
              //  Log.i(TAG, "Erreur Json : "+e);
            //}


        }
    }

    private String http_test (String urlString) throws  IOException {

        String token="";
        URL url = new URL(urlString);

        trustAllHosts();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(20000 /* milliseconds */);
        conn.setConnectTimeout(25000 /* milliseconds */);
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

        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
        {
            Log.i (TAG,headerName+": "+conn.getHeaderField(i));
        }

        int responseCode = conn.getResponseCode();

        if(responseCode == conn.HTTP_OK) {
            final String COOKIES_HEADER = "Set-Cookie";
            cookie = conn.getHeaderField(COOKIES_HEADER); // this is managed automagically by Android and it does not require to be setted in every request
        }

        if (conn.getInputStream()!=null)
        {
            Log.i(TAG,readIt(conn.getInputStream(),15000));
            token=readIt(conn.getInputStream(),15000);
        }
        return token;
    }

    private String https_test (String urlString) throws  IOException {



        String token="";
        URL url = new URL(urlString);

       if (url.getProtocol().toLowerCase().equals("https")) {}

        if (url.getProtocol().toLowerCase().equals("http")) {}

        trustAllHosts();

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
       nameValuePairs.add(new BasicNameValuePair("signin[username]", user.getLOGIN()));
       nameValuePairs.add(new BasicNameValuePair("signin[password]", user.getPASS()));

       OutputStream os = conn.getOutputStream();
       BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
       writer2.write(getQuery(nameValuePairs));
       writer2.flush();

       conn.connect();

       String headerName = null;

       for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
       {
           Log.i (TAG,headerName+": "+conn.getHeaderField(i));
       }

       int responseCode = conn.getResponseCode();

       if(responseCode == conn.HTTP_OK) {
          final String COOKIES_HEADER = "Set-Cookie";
          cookie = conn.getHeaderField(COOKIES_HEADER); // this is managed automagically by Android and it does not require to be setted in every request
       }

       if (conn.getInputStream()!=null)
       {
           Log.i(TAG,readIt(conn.getInputStream(),15000));
           token=readIt(conn.getInputStream(),15000);
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

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream containing HTML from targeted site.
     * @param len    Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        Reader reader = null;
        //stream.available();
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private static void trustAllHosts() {

        X509TrustManager easyTrustManager = new X509TrustManager() {

            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
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
        }
    }

    /**
     * Creation d'un broadcastReceiver pour détecter la douchette en usb
     */

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

//détection d'un appareil usb qui se connecte
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
//affichage bouton vert de présence douchette
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
                 //détection de débranchement d'un appareil usb
                if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice deviceout = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (deviceout != null) {

                        //affichage bouton rouge indiquant qu'il n'y a pas de douchette
                        // call your method that cleans up and closes communication with the device

                    }
                }
            }
        }}

        ;

    /**
     * Create a chain of targets that will receive log data
     */
    public void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
       // Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        mLogFragment =
                (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(mLogFragment.getLogView());
    }



}
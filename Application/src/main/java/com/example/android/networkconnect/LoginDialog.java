package com.example.android.networkconnect;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by adonniou on 16/04/15.
 */
public class LoginDialog extends DialogFragment {

    private PreferenceManager mPreference;
    private JSONObject jsonLog;
    private User user;
    private static final String TAG = "EDroide";
    public String cookie;
    private ProgressBar progressBar;
    private int mProgressStatus = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View alertDialogView = inflater.inflate(R.layout.login_dialog, null);
        final EditText login = (EditText) alertDialogView.findViewById(R.id.username);
        final EditText hote = (EditText) alertDialogView.findViewById(R.id.hote);
        final EditText pass =(EditText) alertDialogView.findViewById(R.id.password);
        final CheckBox checkBox_save = (CheckBox) alertDialogView.findViewById(R.id.save_log);
        progressBar=(ProgressBar) alertDialogView.findViewById(R.id.progressbar);

        builder.setView(alertDialogView);
        user=new User();

        //
        if (Read_log(getActivity()) != null) {


            try {
                JSONObject js=Read_log(getActivity());

                login.setText(js.getString("login"),null);
                pass.setText(js.getString("pass"),null);
                hote.setText(js.getString("hote"), null);
                user.setUser(js.getString("login"),js.getString("pass"),js.getString("hote"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        builder.setTitle("Login");


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog.Builder builder1 = builder.setView(alertDialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            //récuperer le login + trim + minuscule
                            String log = login.getText().toString();
                            log = log.trim();
                            log = log.toLowerCase();


                            //récuperer le pass + trim + minuscule
                            String pas = pass.getText().toString();
                            pas = pas.trim();
                            pas = pas.toLowerCase();

                            //récuperer l'hote + trim + minuscule
                            String hot = hote.getText().toString();
                            hot = hot.trim();
                            hot = hot.toLowerCase();

                            hot="https://dev3.libre-informatique.fr/";
                            JSONObject JSONLogin = new JSONObject();

                            user.setUser(log, pas, hot);
                            user.savToJsonFile();


                                LoginAsyncTask loginAsyncTask = (LoginAsyncTask) new LoginAsyncTask();
                                //loginAsyncTask.execute("https://"+hot+".libre-informatique.fr/");
                            loginAsyncTask.execute(hot);


                            if (checkBox_save.isChecked()) {
                                jsonLog = new JSONObject();
                                jsonLog.put("login", log).toString();
                                jsonLog.put("pass", pas).toString();
                                jsonLog.put("hote", hot).toString();
                                Save_log(getActivity(), jsonLog);
                                //signin[remember]
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Erreur save login: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.Annuler, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Implementation of AsyncTask, to fetch the login data in the background away from
     * the UI thread.
     */
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

            //progressBar.setProgress();
            progressBar.setProgress(mProgressStatus);
            super.onProgressUpdate(values);

        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
           // Log.i(TAG, result);
            //affichage du resultat dans un toast
            Log.i(TAG , "Postexe: "+result);

            if (result.contains("unknownHostException"))
            {

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

        Log.i(TAG, "Protocol: "+url.getProtocol().toString());

        //if (url.getProtocol().toLowerCase().equals("https")) {
        SSLHosts();
        //trustAllHosts();

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
                cookie = conn.getHeaderField(COOKIES_HEADER); // this is managed automagically by Android and it does not require to be setted in every request
            }

            if (conn.getInputStream()!=null)
            {

                //while ((count = conn.getInputStream().read()) != -1) {
                //  mProgressStatus += count;
                //    progressBar.setProgress(mProgressStatus);
                //mProgressStatus(""+(int)((total*100)/conn.getInputStream().available()));

                //}

                Log.i(TAG,readIt(conn.getInputStream(),15000));
                token=conn.getHeaderField(1);
                Log.i(TAG,getStringFromInputStream(conn.getInputStream()));
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

        Log.i(TAG, "Protocol: "+url.getProtocol().toString());

        SSLHosts();

        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) url.openConnection();


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
            cookie = conn.getHeaderField(COOKIES_HEADER); // this is managed automagically by Android and it does not require to be setted in every request
        }

        if (conn.getInputStream()!=null)
        {

            Log.i(TAG,readIt(conn.getInputStream(),15000));
            token=conn.getHeaderField(1);
            Log.i(TAG,getStringFromInputStream(conn.getInputStream()));
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
            //affiche le contenu de mon fichier dans un popup surgissant
            Toast.makeText(context, "data: "+data,Toast.LENGTH_SHORT).show();
            json=new JSONObject(data);
        }
        catch (Exception e) {
            Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
        }
            /*finally {
               try {
                      isr.close();
                      fIn.close();
                      } catch (IOException e) {
                        Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
                      }
            } */
        return json;
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
            //Log.i(TAG,"TrustManager: "+sc.getProtocol());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Erreur trustManager: "+e);
        }
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
            //Log.i(TAG,"TrustManager: "+sc.getProtocol());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Erreur trustManager: "+e);
        }
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


}

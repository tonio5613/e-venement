package com.example.android.networkconnect;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by adonniou on 16/04/15.
 */
public class LoginDialog extends DialogFragment {


    private JSONObject jsonLog=null;
    private User user;
    private static final String TAG = "EDroide";
    public String cookie;
    private ProgressBar progressBar;
    private int mProgressStatus = 0;
    public static final String LOG_ERREURS="log_erreur";

    private EditText login;
    private EditText hote;
    private EditText pass;
    private CheckBox checkBox_save;
    private String tls="";

    String log="";
    String pas="";
    String hot="";

    private String errors_log="Le mot de passe ou l'identifiant est invalide.";
    private boolean Log_ok=false;


    static LoginDialog newInstance() {
        LoginDialog f = new LoginDialog();
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //vérification des variables d'entrées
        processArguments();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View alertDialogView = inflater.inflate(R.layout.login_dialog, null);
        login = (EditText) alertDialogView.findViewById(R.id.username);
        hote = (EditText) alertDialogView.findViewById(R.id.hote);
        pass = (EditText) alertDialogView.findViewById(R.id.password);
        checkBox_save = (CheckBox) alertDialogView.findViewById(R.id.save_log);

        builder.setView(alertDialogView);
        user = new User();

        if (Read_log(getActivity()) != null) {

            try {
                JSONObject js = Read_log(getActivity());

                login.setText(js.getString("login"), null);
                pass.setText(js.getString("pass"), null);
                hote.setText(js.getString("hote"), null);
                tls = js.getString("tls");
                user.setUser(js.getString("login"), js.getString("pass"), js.getString("hote"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        builder.setTitle("Login");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(alertDialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    //récuperer le login + trim + minuscule
                                    log = login.getText().toString();
                                    log = log.trim();
                                    log = log.toLowerCase();


                                    //récuperer le pass + trim + minuscule
                                    pas = pass.getText().toString();
                                    pas = pas.trim();
                                    pas = pas.toLowerCase();

                                    //récuperer l'hote + trim + minuscule
                                    hot = hote.getText().toString();
                                    hot = hot.trim();
                                    hot = hot.toLowerCase();

                                    //hot="https://dev3.libre-informatique.fr/";

                                    jsonLog = new JSONObject();

                                    jsonLog.put("hote", hot).toString();
                                    jsonLog.put("login", log).toString();
                                    jsonLog.put("pass", pas).toString();

                                    Bundle arg = new Bundle();
                                    arg.putString("USER", jsonLog.toString());

                                    BureauFragment bureauFragment = new BureauFragment();
                                    bureauFragment.setArguments(arg);

                                    FragmentManager fm = getActivity().getSupportFragmentManager();

                                    FragmentTransaction ft = fm.beginTransaction();

                                    ft.setCustomAnimations(android.R.anim.slide_in_left,
                                            android.R.anim.slide_out_right);

                                    ft.replace(R.id.intro_fragment, bureauFragment);

                                    ft.addToBackStack(null);

                                    ft.commit();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                )
                            .
                    setNegativeButton(R.string.Quitter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                               getActivity().finish();
                                }
                            }

                    );
                    // Create the AlertDialog object and return it
                    return builder.create();
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

    public void processArguments() {
        // For most objects we'd handle the multiple possibilities for initialization variables
        // as multiple constructors.  For Fragments, however, it's customary to use
        // setArguments / getArguments.

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(LOG_ERREURS))
            {
                try {
                    login.setText(R.string.log_error);

                    JSONArray Js=null;

                    String data=args.getString(LOG_ERREURS);

                    JSONObject checkpoints=new JSONObject(data);

                    JSONArray tab_checkpoints=new JSONArray();
                    tab_checkpoints= checkpoints.names();

                    String tab[]=null;
                    int var=0;
                    Log.i(TAG, "Longueur tab: "+tab_checkpoints.length());
                    if(tab_checkpoints.length()>1) {

                        var = 1;

                        for(int i=0;i<tab_checkpoints.length();i++)
                        {

                            String entree=tab_checkpoints.get(i).toString()+" "+checkpoints.get(tab_checkpoints.get(i).toString());

                        }

                    }

                    if(tab_checkpoints.length()<=1)
                    {
                        //spinner_checkpoint.setVisibility(View.GONE);
  //                      listcheckpoint = new String[tab_checkpoints.length()];
    //                    num_checkpoint=tab_checkpoints.get(0).toString();
      //                  Log.i(TAG, "Checkpoint: "+num_checkpoint);

                    }



                } catch (Exception e) {
                    Log.i(TAG,"Erreur Tableau JSON: "+e.toString());
                    e.printStackTrace();
                }
            }
           else {

            }
        }
    }

}

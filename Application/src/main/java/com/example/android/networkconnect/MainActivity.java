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
import android.app.Dialog;
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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

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
import java.net.CookieHandler;
import java.net.CookieManager;
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

import android.hardware.usb.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

    public User user=new User();
    public JSONObject UserJson=null;

    public LoginDialog mLoginDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //initialisation du CookieManager
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(new CookieManager());

            this.setContentView(R.layout.mainlayout);


            //Initialisation de LoginDialog

            mLoginDialog = new LoginDialog();

            //Vérification si connection internet active

        if (isConnected() != true) {
            //Affichage erreur de connection
            Show_Connection_error_Dialog(null);
        } else {
            //Lecture des informations en local
            if (Read_log(this) == null) {
                //Affichage de la fenêtre d'identification
                mLoginDialog.show(getSupportFragmentManager(), "LoginDialog");
            }

            //Utilisation du fichier utilisateur
             else {

                UserJson = Read_log(this);

                Bundle arg = new Bundle();
                arg.putString("USER", UserJson.toString());

                BureauFragment bureauFragment = new BureauFragment();
                bureauFragment.setArguments(arg);

                FragmentManager fm = this.getSupportFragmentManager();

                FragmentTransaction ft = fm.beginTransaction();

                ft.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

                ft.replace(R.id.intro_fragment, bureauFragment);

                ft.addToBackStack(null);
                //affichage du fragment Bureau
                ft.commit();

            }
        }
    }

   /**
    *Vérification connection internet présente
    **/

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else {
            return false;
        }
    }

    /**
     *Fonctions d'affichages des écrans et du menu
     *
     **/


    /**
     *Fonction d'affichage d'erreurs
     **/
    public void Show_Connection_error_Dialog(String erreur)
    {
        String messageText= getResources().getString(R.string.connection_error_txt);

        if(erreur!=null)
        {
            messageText=erreur;
        }
        final ArrayList seletedItems=new ArrayList();
        final boolean[] checker={true};
        final AlertDialog.Builder builder =new AlertDialog.Builder(this);
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
            finish();
            }
        });
        builder.show();
    }

    /**
     *Fonction d'affichage choix SSL
     **/
    private void SSLshowDialog()
    {
        final ArrayList seletedItems=new ArrayList();
        String checkboxText= getResources().getString(R.string.ssl_check);
        final CharSequence[] items = {checkboxText};
        final boolean[] checker={true};
        final AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle(R.string.ssl_titre);

        builder.setMultiChoiceItems(items, checker,
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

    /**
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

    /**
     *Gestion du bouton de retour
     **/

    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();

        if (manager.getBackStackEntryCount() > 1) {
            showFragment(mbureauFragment, null);
        } else {
            this.finish();
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
    *Gestion des actions de la barre de menu
    **/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.newlog:
                //rentrer un nouveau mot de passe
                mLoginDialog.show(getSupportFragmentManager(), "LoginDialog");
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
                //Affichage sélection SSL
                SSLshowDialog();
                return true;
        }
        return false;
    }

    /**
     *Lecture du fichier des utilisateurs
     **/

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
            Log.i(TAG, "Error setting: "+e);
            Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
        }
        return json;
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

                    }
                }
            }
        }}
        ;
}
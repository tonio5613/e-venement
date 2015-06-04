package com.example.android.networkconnect;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Created by adonniou on 18/05/15.
 */
public class User {

    private String LOGIN;
    private String PASS;
    private String HOTE;
    private JSONObject JSONLOG_SAV;

  public String TAG="EDroide_log-erreur";

    private JSONObject Read_log(Context context)
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
          //  Toast.makeText(context, "data: "+data,Toast.LENGTH_SHORT).show();
            json=new JSONObject(data);
        }
        catch (Exception e) {
           // Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
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

    private void Save_log (Context context,JSONObject sav_log)
    {
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        if(Read_log(context)!=null)
        {
          //  Toast.makeText(context, "Compte existant: ",Toast.LENGTH_SHORT).show();

        }
        else {
            try {
                fOut = context.openFileOutput("settings.txt", Context.MODE_APPEND);
                osw = new OutputStreamWriter(fOut);

                osw.write(sav_log.toString());
                osw.flush();

            } catch (Exception e) {
            } finally {
                try {
                    osw.close();
                    fOut.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public  void savToJsonFile()
    {
        try {
            JSONLOG_SAV = new JSONObject();
            JSONLOG_SAV.put("login",LOGIN);
            JSONLOG_SAV.put("pass", PASS);
            JSONLOG_SAV.put("hote", HOTE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Save_log(, JSONLOG_SAV);
    }

    public void setUser(String mLOGIN,String mPASS, String mHOTE)
    {
    this.LOGIN=mLOGIN;
    this.PASS=mPASS;
    this.HOTE=mHOTE;
    }

    public String getLOGIN() {
        return LOGIN;
    }

    public void setLOGIN(String LOGIN) {
        this.LOGIN = LOGIN;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public String getHOTE() {
        return HOTE;
    }

    public void setHOTE(String HOTE) {
        this.HOTE = HOTE;
    }




}

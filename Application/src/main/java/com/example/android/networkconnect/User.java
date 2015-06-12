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
    private String TLS;
    private JSONObject JSONLOG_SAV;

  public String TAG="EDroide_log-erreur";

    public  void savToJsonFile()
    {
        try {
            JSONLOG_SAV = new JSONObject();
            JSONLOG_SAV.put("login",LOGIN);
            JSONLOG_SAV.put("pass", PASS);
            JSONLOG_SAV.put("hote", HOTE);
            JSONLOG_SAV.put("tls", TLS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

package com.example.android.networkconnect;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adonniou on 29/04/15.
 */
public class ControlTic {

    private static final String TAG = "EDroide_class_JSON";

    JSONObject JSONOBJET;
    JSONArray JSONARRAY_TICKETS;
    String SUCCESS;
    String MESSAGE;
    String TIMESTAMP;
    String TICKETS_ID;
    String TICKETS_JAUGE;
    String TICKETS_MANIFESTATION;
    String TICKETS_MANIFESTATION_URL;
    String TICKETS_SEAT;
    String TICKETS_PRICE;
    String TICKETS_VALUE;
    String TICKETS_VALUE_TXT;
    String TICKETS_URL;
    String TICKETS_USERS;
    String TICKETS_CANCEL;
    JSONObject DETAILS;
    JSONObject CONTROL;
    String DETAILS_CONTROL_COMMENT;
    JSONArray ERRORS_ARRAY;
    String DETAILS_CONTROL_ERRORS;
    JSONArray CONTACTS;
    int CONTACTS_ID;
    String CONTACTS_CONTACT_NAME;
    String CONTACTS_CONTACT_COMMENT;
    String CONTACTS_CONTACT_URL;
    String CONTACTS_CONTACT_FLASH;

    public JSONObject getJSONOBJET() {
        return JSONOBJET;
    }

    @Override
    public String toString()
    {
        return MESSAGE;
    }

    public void setJSONOBJET(JSONObject JSONOBJET) {

        this.JSONOBJET = JSONOBJET;

        try {

            if (JSONOBJET.getString("success")!=null)
                this.SUCCESS=JSONOBJET.getString("success");

            if(JSONOBJET.getString("message")!=null)
                this.MESSAGE=JSONOBJET.getString("message");

            if(JSONOBJET.getString("timestamp")!=null)
            this.TIMESTAMP=JSONOBJET.getString("timestamp");

            if(JSONOBJET.getJSONArray("tickets")!=null) {
                this.JSONARRAY_TICKETS = JSONOBJET.getJSONArray("tickets");

                if(JSONARRAY_TICKETS.getJSONObject(0).getString("id")!=null)
                this.TICKETS_ID = JSONARRAY_TICKETS.getJSONObject(0).getString("id");

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("gauge")!=null)
                        this.TICKETS_JAUGE = JSONARRAY_TICKETS.getJSONObject(0).getString("gauge");
                } catch (JSONException e) {
                    this.TICKETS_JAUGE ="";
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("manifestation")!=null)
                      this.TICKETS_MANIFESTATION=JSONARRAY_TICKETS.getJSONObject(0).getString("manifestation");
                } catch (JSONException e) {
                    this.TICKETS_MANIFESTATION="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("manifestation_url")!=null)
                        this.TICKETS_MANIFESTATION_URL=JSONARRAY_TICKETS.getJSONObject(0).getString("manifestation_url");
                } catch (JSONException e) {
                    this.TICKETS_MANIFESTATION_URL="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("seat")!=null)
                        this.TICKETS_SEAT=JSONARRAY_TICKETS.getJSONObject(0).getString("seat");
                } catch (JSONException e) {
                    this.TICKETS_SEAT="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("price")!=null)
                        this.TICKETS_PRICE=JSONARRAY_TICKETS.getJSONObject(0).getString("price");
                } catch (JSONException e) {
                    this.TICKETS_PRICE="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("value")!=null)
                        this.TICKETS_VALUE=JSONARRAY_TICKETS.getJSONObject(0).getString("value");
                } catch (JSONException e) {
                    this.TICKETS_VALUE="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("value_txt")!=null)
                        this.TICKETS_VALUE_TXT=JSONARRAY_TICKETS.getJSONObject(0).getString("value_txt");
                } catch (JSONException e) {
                    this.TICKETS_VALUE_TXT="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("url")!=null)
                       this.TICKETS_URL=JSONARRAY_TICKETS.getJSONObject(0).getString("url");
                } catch (JSONException e) {
                    this.TICKETS_URL="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getJSONArray("users").getString(0)!=null)
                      this.TICKETS_USERS=JSONARRAY_TICKETS.getJSONObject(0).getJSONArray("users").getString(0);
                } catch (JSONException e) {
                    this.TICKETS_USERS="";
                    e.printStackTrace();
                }

                try {
                    if(JSONARRAY_TICKETS.getJSONObject(0).getString("cancel")!=null)
                        this.TICKETS_CANCEL=JSONARRAY_TICKETS.getJSONObject(0).getString("cancel");
                } catch (JSONException e) {
                    this.TICKETS_CANCEL="";
                    e.printStackTrace();
                }
            }

            try {
                if(JSONOBJET.getJSONObject("details")!=null) {
                    this.DETAILS = JSONOBJET.getJSONObject("details");

                    try {
                        if (DETAILS.getJSONObject("control") != null) {
                            this.CONTROL=DETAILS.getJSONObject("control");

                            try {
                                if(CONTROL.getJSONArray("errors")!=null)
                                {
                                    this.ERRORS_ARRAY=CONTROL.getJSONArray("errors");

                                    try {
                                        if(ERRORS_ARRAY.getString(0)!=null) {

                                        this.DETAILS_CONTROL_ERRORS =ERRORS_ARRAY.getString(0);
                                        }
                                    } catch (JSONException e) {
                                        this.DETAILS_CONTROL_ERRORS="";
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                this.ERRORS_ARRAY=null;
                                e.printStackTrace();
                            }

                        }
                    } catch (JSONException e) {
                        this.CONTROL=null;
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.DETAILS =null;
            }

            this.DETAILS=JSONOBJET.getJSONObject("details");

            this.CONTROL=DETAILS.getJSONObject("control");

            this.DETAILS_CONTROL_COMMENT=CONTROL.getString("comment");

            try {
                if(DETAILS.getJSONArray("contacts")!=null);
                {
                    this.CONTACTS=DETAILS.getJSONArray("contacts");


                }
            } catch (JSONException e) {
                this.CONTACTS=null;
                e.printStackTrace();
            }



        } catch (JSONException e) {

            Log.i(TAG,e.toString());
        }

    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(String SUCCESS) {
        this.SUCCESS = SUCCESS;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getTICKETS_ID() {
        return TICKETS_ID;
    }

    public void setTICKETS_ID(String TICKETS_ID) {
        this.TICKETS_ID = TICKETS_ID;
    }

    public String getTICKETS_JAUGE() {
        return TICKETS_JAUGE;
    }

    public void setTICKETS_JAUGE(String TICKETS_GAUGE) {
        this.TICKETS_JAUGE = TICKETS_GAUGE;
    }

    public String getTICKETS_MANIFESTATION() {
        return TICKETS_MANIFESTATION;
    }

    public void setTICKETS_MANIFESTATION(String TICKETS_MANIFESTATION) {
        this.TICKETS_MANIFESTATION = TICKETS_MANIFESTATION;
    }

    public String getTICKETS_MANIFESTATION_URL() {
        return TICKETS_MANIFESTATION_URL;
    }

    public void setTICKETS_MANIFESTATION_URL(String TICKETS_MANIFESTATION_URL) {
        this.TICKETS_MANIFESTATION_URL = TICKETS_MANIFESTATION_URL;
    }

    public String getTICKETS_SEAT() {
        return TICKETS_SEAT;
    }

    public void setTICKETS_SEAT(String TICKETS_SEAT) {
        this.TICKETS_SEAT = TICKETS_SEAT;
    }

    public String getTICKETS_PRICE() {
        return TICKETS_PRICE;
    }

    public void setTICKETS_PRICE(String TICKETS_PRICE) {
        this.TICKETS_PRICE = TICKETS_PRICE;
    }

    public String getTICKETS_VALUE() {
        return TICKETS_VALUE;
    }

    public void setTICKETS_VALUE(String TICKETS_VALUE) {
        this.TICKETS_VALUE = TICKETS_VALUE;
    }

    public String getTICKETS_VALUE_TXT() {
        return TICKETS_VALUE_TXT;
    }

    public void setTICKETS_VALUE_TXT(String TICKETS_VALUE_TXT) {
        this.TICKETS_VALUE_TXT = TICKETS_VALUE_TXT;
    }

    public String getTICKETS_URL() {
        return TICKETS_URL;
    }

    public void setTICKETS_URL(String TICKETS_URL) {
        this.TICKETS_URL = TICKETS_URL;
    }

    public String getTICKETS_USERS() {
        return TICKETS_USERS;
    }

    public void setTICKETS_USERS(String TICKETS_USERS) {
        this.TICKETS_USERS = TICKETS_USERS;
    }

    public String getTICKETS_CANCEL() {
        return TICKETS_CANCEL;
    }

    public void setTICKETS_CANCEL(String TICKETS_CANCEL) {
        this.TICKETS_CANCEL = TICKETS_CANCEL;
    }

    public String getDETAILS_CONTROL_COMMENT() {
        return DETAILS_CONTROL_COMMENT;
    }

    public void setDETAILS_CONTROL_COMMENT(String DETAILS_CONTROL_COMMENT) {
        this.DETAILS_CONTROL_COMMENT = DETAILS_CONTROL_COMMENT;
    }

    public String getDETAILS_CONTROL_ERRORS() {
        return DETAILS_CONTROL_ERRORS;
    }

    public void setDETAILS_CONTROL_ERRORS(String DETAILS_CONTROL_ERRORS) {
        this.DETAILS_CONTROL_ERRORS = DETAILS_CONTROL_ERRORS;
    }

    public int getCONTACTS_ID() {
        return CONTACTS_ID;
    }

    public void setCONTACTS_ID(int CONTACTS_ID) {
        this.CONTACTS_ID = CONTACTS_ID;
    }

    public String getCONTACTS_CONTACT_NAME() {
        return CONTACTS_CONTACT_NAME;
    }

    public void setCONTACTS_CONTACT_NAME(String CONTACTS_CONTACT_NAME) {
        this.CONTACTS_CONTACT_NAME = CONTACTS_CONTACT_NAME;
    }

    public String getCONTACTS_CONTACT_COMMENT() {
        return CONTACTS_CONTACT_COMMENT;
    }

    public void setCONTACTS_CONTACT_COMMENT(String CONTACTS_CONTACT_COMMENT) {
        this.CONTACTS_CONTACT_COMMENT = CONTACTS_CONTACT_COMMENT;
    }

    public String getCONTACTS_CONTACT_URL() {
        return CONTACTS_CONTACT_URL;
    }

    public void setCONTACTS_CONTACT_URL(String CONTACTS_CONTACT_URL) {
        this.CONTACTS_CONTACT_URL = CONTACTS_CONTACT_URL;
    }

    public String getCONTACTS_CONTACT_FLASH() {
        return CONTACTS_CONTACT_FLASH;
    }

    public void setCONTACTS_CONTACT_FLASH(String CONTACTS_CONTACT_FLASH) {
        this.CONTACTS_CONTACT_FLASH = CONTACTS_CONTACT_FLASH;
    }

}

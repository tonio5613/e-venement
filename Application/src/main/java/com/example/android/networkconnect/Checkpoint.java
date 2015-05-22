package com.example.android.networkconnect;

/**
 * Created by adonniou on 19/05/15.
 */
public class Checkpoint{
    private String CPNUM;
    private String CPNOM;

    public Checkpoint(String numero,String nom)
    {
        this.CPNUM=numero;
        this.CPNOM=nom;
    }

    public String getCPNUM() {
        return CPNUM;
    }

    public void setCPNUM(String CPNUM) {
        this.CPNUM = CPNUM;
    }

    public String getCPNOM() {
        return CPNOM;
    }

    public void setCPNOM(String CPNOM) {
        this.CPNOM = CPNOM;
    }
    @Override
    public String toString()
    {
        String data=CPNUM+" "+CPNOM;
        return data;
    }
}

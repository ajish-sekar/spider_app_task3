package com.example.android.weather;

/**
 * Created by Ajish on 10-07-2017.
 */

public class Weather {
    private Long mtime;
    private String mname;
    private String mdesc;
    private double mtemp;
    private int mhumidity;
    private double mwindSpeed;
    private int mvisibility;
    private double mpressure;
    private String mimage;
    private byte[] mimageByte;
    private String mdate;

    public Weather(Long time, String name, String desc, double temp, int humidity, double windSpeed, int visibilty, double pressure, String image){
        mtime=time;
        mname=name;
        mdesc=desc;
        mtemp=temp;
        mhumidity=humidity;
        mwindSpeed=windSpeed;
        mvisibility=visibilty;
        mpressure=pressure;
        mimage=image;
    }

    public void setImageByte(byte[] imageByte){
        mimageByte = imageByte;
    }

    public byte[] getMimageByte() {
        return mimageByte;
    }

    public void setDate(String date) {
        mdate = date;
    }

    public String getMdate() {
        return mdate;
    }

    public String getMname() {
        return mname;
    }

    public int getMhumidity() {
        return mhumidity;
    }

    public double getMpressure() {
        return mpressure;
    }

    public double getMtemp() {
        return mtemp;
    }

    public int getMvisibility() {
        return mvisibility;
    }

    public double getMwindSpeed() {
        return mwindSpeed;
    }

    public String getMdesc() {
        return mdesc;
    }

    public String getMimage() {
        return mimage;
    }

    public Long getMtime() {
        return mtime;
    }
}

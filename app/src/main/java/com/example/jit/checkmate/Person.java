package com.example.jit.checkmate;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by jit on 24-03-2017.
 */

public class Person {

    String name,contactno;
    double lat,lng;
    String desc;
    double distance;
    Date date;

    Person(){

    }

    Person(String name, double lat, double lng, String desc,Date date){
        this.name=name;
        this.lat = lat;
        this.lng=lng;
        this.desc=desc;
        this.date = date;
    }

    public void setName(String n){
        this.name=n;
    }
    public void setLat(double d){
        this.lat=d;
    }
    public void setLng(double d){
        this.lng=d;
    }
    public void setDesc(String s){
        this.desc=s;
    }
    public void setDistance(double d){
        this.distance=(d/1000);
    }
    public void setContactno(String c){
        this.contactno = c;
    }
    public void setDate(Date d){
        this.date=d;
    }

    public String getName(){
        return name;
    }
    public double getLng(){
        return lng;
    }
    public double getLat(){
        return lat;
    }
    public String getDesc(){
        return desc;
    }
    public double getDistance(){return distance;}
    public String getContactno(){
        return contactno;
    }
    public Date getDate(){
        return date;
    }

}

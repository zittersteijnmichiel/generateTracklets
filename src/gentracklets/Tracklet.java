/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gentracklets;

import java.util.Comparator;

/**
 *
 * @author zittersteijn
 */
public class Tracklet {
    
    private double RA;
    private double RAdot;
    private double DEC;
    private double DECdot;
    private double sigRA;
    private double sigRAdot;
    private double sigDEC;
    private double sigDECdot;
    private double doy;
    private int fence;
    private String label;
    private int length;
    
    public Tracklet(double RAval, double RAdotVal, double DECval, double DECdotVal, double sigRAval, double sigRAdotVal, double sigDECval, double sigDECdotVal, double doyVal ,int fenceVal, String name){
        RA = RAval;
        RAdot = RAdotVal;
        DEC = DECval;        
        DECdot = DECdotVal;
        sigRA = sigRAval;
        sigRAdot = sigRAdotVal;
        sigDEC = sigDECval;
        sigDECdot = sigDECdotVal;
        doy = doyVal;
        fence = fenceVal;
        label = name;
    }
    
    public void printTracklet(){
        System.out.println(RA + "\t" + RAdot + "\t" + DEC + "\t" +  DECdot + "\t" +  sigRA + "\t" +  sigRAdot +  "\t" + 
                sigDEC + "\t" +  sigDECdot + "\t" +  doy +  "\t" + fence + "\t" +  label);
    }
    
    public void setRA(double RAval){RA = RAval;}
    public void setRAdot(double RAdotVal){RAdot = RAdotVal;}
    public void setDEC(double DECval){DEC = DECval;}
    public void setDECdot(double DECdotVal){DECdot = DECdotVal;}
    public void setSigRA(double sigRAval){sigRA = sigRAval;}
    public void setSigRAdot(double sigRAdotVal){sigRAdot = sigRAdotVal;}
    public void setSigDEC(double sigDECval){sigDEC = sigDECval;}
    public void setSigDECdot(double sigDECdotVal){sigDECdot = sigDECdotVal;}
    public void setMjd(double doyVal){doy = doyVal;}
    public void setFence(int fenceVal){fence = fenceVal;}
    public void setLength(int val){length = val;}
    
    public double getRA(){return RA;}
    public double getRAdot(){return RAdot;}
    public double getDEC(){return DEC;}
    public double getDECdot(){return DECdot;}
    public double getSigRA(){return sigRA;}
    public double getSigRAdot(){return sigRAdot;}
    public double getSigDEC(){return sigDEC;}
    public double getSigDECdot(){return sigDECdot;}
    public double getDOY(){return doy;}
    public int getFence(){return fence;}
    public String getLabel(){return label;}
    public int getLength(){return length;}
}

class TrackletDOYComparator implements Comparator<Tracklet> {
    @Override
    public int compare(Tracklet tracklet1, Tracklet tracklet2) {
        if(tracklet1.getDOY() - tracklet2.getDOY() < 0){
            int out = -1;
            return out;
        }else if(tracklet1.getDOY() - tracklet2.getDOY() == 0){
            int out = 0;
            return out;
        }else{
            int out = 1;
            return out;
        }
    }
}
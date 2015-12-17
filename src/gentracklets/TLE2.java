/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gentracklets;

/**
 *
 * @author zittersteijn
 */
public class TLE2 {
    
    // first line
    private String satCatNumberElset;
    private String internationalDesignator;
    private double epoch;
    private String firstDerivative;
    private String secondDerivative;
    private String Bstar;
    private int elemenentSetType;
    private int eleNumber;
        
    //second line
    private int satCatNumber;
    private double i;
    private double RAAN;
    private double e;
    private double w;
    private double M;
    private double n;
    private int revNumb;
    
    // general constructor
    public void TLE(String satCatNumberElsetin, String internationalDesignatorin, double epochin, String firstDerivativein, String secondDerivativein, 
            String Bstarin, int elemenentSetTypein, int eleNumberin, int satCatNumberin, double iin, double RAANin, double ein, double win, double Min, double nin, int revNumbin){
        // first line
        satCatNumberElset = satCatNumberElsetin;
        internationalDesignator = internationalDesignatorin;
        epoch = epochin;
        firstDerivative = firstDerivativein;
        secondDerivative = secondDerivativein;
        Bstar = Bstarin;
        elemenentSetType = elemenentSetTypein;
        eleNumber = eleNumberin;
        
        // second line
        satCatNumber = satCatNumberin;
        i = iin;
        RAAN = RAANin;
        e = ein;
        w = win;
        M = Min;
        n = nin;
        revNumb = revNumbin;
    }
    
    
    // setters for first line
    public void setSatCatNumberElset(String s){
        satCatNumberElset = s;
    }
    public void setInternationalDesignator(String s){
        internationalDesignator = s;
    }
    public void setEpoch(double s){
        epoch = s;
    }
    public void setFirstDerivative(String s){
        firstDerivative = s;
    }
    public void setSecondDerivative(String s){
        secondDerivative = s;
    }
    public void setBstar(String s){
        Bstar = s;
    }
    public void setElemenentSetType(int s){
        elemenentSetType = s;
    }
    public void setEleNumber(int s){
        eleNumber = s;
    }
    
    //getters for first line
    public String getSatCatNumberElset(){
        return satCatNumberElset;
    }
    public String getInternationalDesignator(){
        return internationalDesignator;
    }
    public double getEpoch(){
        return epoch;
    }
    public String getFirstDerivative(){
        return firstDerivative;
    }
    public String getSecondDerivative(){
        return secondDerivative;
    }
    public String getBstar(){
        return Bstar;
    }
    public int getElemenentSetType(){
        return elemenentSetType;
    }
    public int getEleNumber(){
        return eleNumber;
    }
    
    // setters for second line
    public void setSatCatNumber(int s){
        satCatNumber = s;
    }
    public void setInc(double s){
        i = s;
    }
    public void setRAAN(double s){
        RAAN = s;
    }
    public void setE(double s){
        e = s;
    }
    public void setW(double s){
        w = s;
    }
    public void setM(double s){
        M = s;
    }
    public void setN(double s){
        n = s;
    }
    public void setRevNumb(int s){
        revNumb = s;
    }
  
    // getters for second line
    public int getSatCatNumber(){
        return satCatNumber;
    }
    public double getInc(){
        return i;
    }
    public double getRAAN(){
        return RAAN;
    }
    public double getE(){
        return e;
    }
    public double getW(){
        return w;
    }
    public double getM(){
        return M;
    }
    public double getN(){
        return n;
    }
    public int getRevNumb(){
        return revNumb;
    }
}

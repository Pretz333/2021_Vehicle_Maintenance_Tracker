package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.util.Log;

import java.util.Date;

public class Vehicle {
    private static final String TAG = "VEHICLE_CLASS";

    private int id;
    private String name;
    private String make;
    private String model;
    private String year;
    private String color;
    private int mileage;
    private String VIN;
    private String licensePlate;
    private Date purchaseDate;
    private double value;

    //Constructors
    //Default, can probably delete later
    public Vehicle() {
    }

    //Current minimums to be functional. May need to add in make, model, year, color?
    public Vehicle(int id, String name) {
        this.id = id;
        this.name = name;
    }

    //Everything, for use when reading from the database
    public Vehicle(int id, String name, String make, String model, String year, String color, int mileage, String VIN, String licensePlate, Date purchaseDate, double value) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.mileage = mileage;
        setVIN(VIN); //Using the set so I can keep DRY
        setLicensePlate(licensePlate); //Using the set so I can keep DRY
        this.purchaseDate = purchaseDate;
        this.value = value;
    }

    private boolean isVINValid(String VIN, String year){
        VIN = VIN.toUpperCase(); //In case the user typed in lowercase letters

        //Vehicles after 1981 have a 17 character VIN
        if (Integer.parseInt(year) > 1981 && VIN.length() != 17) {
            return false;
        }

        //Characters Q, I, and O are not used due to their similarity to 1 and 0
        //VINs are only letters or digits, so we'll check that as well
        for(char c : VIN.toCharArray()){
            if(!Character.isLetterOrDigit(c) || c == 'Q' || c == 'O' || c == 'I'){
                return false;
            }
        }

        //TODO: add more tests
        return true;
    }

    private boolean isLPValid(String licensePlate){
        boolean valid = true;
        //TODO: test validity
        return valid;
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    //May want to delete? Only have the database use this with the constructors above?
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year; //TODO: ensure it is 4 digits?
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        if(isVINValid(VIN, this.year)){
            this.VIN = VIN;
        } else {
            Log.w(TAG, "Invalid VIN");
        }
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        if(isLPValid(licensePlate)){
            this.licensePlate = licensePlate;
        } else {
            Log.w(TAG, "Invalid License Plate");
        }
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Vehicle ID:").append(id)
                .append(", Name:").append(name);
        return builder.toString();
    }
}

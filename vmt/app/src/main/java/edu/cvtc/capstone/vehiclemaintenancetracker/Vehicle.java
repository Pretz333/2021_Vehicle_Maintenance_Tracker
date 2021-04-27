package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;

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
    //Using sets so I can keep DRY
    //Default, can probably delete later
    public Vehicle() {
    }

    //Current minimums to be functional. May need to add in make, model, year, color?
    public Vehicle(int id, String name) {
        setId(id);
        setName(name);
    }

    //Everything, for use when reading from the database
    public Vehicle(int id, String name, String make, String model, String year, String color, int mileage, String VIN, String licensePlate, Date purchaseDate, double value) {
        setId(id);
        setName(name);
        setMake(make);
        setModel(model);
        setYear(year);
        setColor(color);
        setMileage(mileage);
        setVIN(VIN);
        setLicensePlate(licensePlate);
        setPurchaseDate(purchaseDate);
        setValue(value);
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    //May want to delete? Only have the database use this with the constructors above?
    public void setId(int id) {
        //TODO: Check if record is in database or the call came from the database
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(VerifyUtil.isStringSafe(name)){
            this.name = name;
        } else {
            Log.w(TAG, "Vehicle name unsafe");
        }
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
        if(year.length() == 4 && Integer.parseInt(year) > 1900) {
            this.year = year;
        } else {
            Log.w(TAG, "Year was not 4 digits or was not a number");
        }
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
        VIN = VIN.toUpperCase(); //In case the user typed in lowercase letters
        if(VerifyUtil.isVINValid(VIN, this.year)){
            this.VIN = VIN;
        } else {
            Log.w(TAG, "Invalid VIN");
        }
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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

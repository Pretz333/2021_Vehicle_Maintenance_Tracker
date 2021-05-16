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

    // Constructors, using sets to keep DRY
    // Minimum
    public Vehicle(String name) {
        this.id = -1;
        setName(name);
    }

    // Everything but the id
    public Vehicle(String name, String make, String model, String year, String color, int mileage, String VIN, String licensePlate, Date purchaseDate, double value) {
        this.id = -1;
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

    // Everything, for use when reading from the database. Since it's in the database, it's already passed verification
    public Vehicle(int id, String name, String make, String model, String year, String color, int mileage, String VIN, String licensePlate, Date purchaseDate, double value) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.mileage = mileage;
        this.VIN = VIN;
        this.licensePlate = licensePlate;
        this.purchaseDate = purchaseDate;
        this.value = value;
    }

    // Everything but the date, for use when reading from the database and the long date value is null.
    // Since it's in the database, it's already passed verification
    public Vehicle(int id, String name, String make, String model, String year, String color, int mileage, String VIN, String licensePlate, double value) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.mileage = mileage;
        this.VIN = VIN;
        this.licensePlate = licensePlate;
        this.value = value;
    }

    // Getters and Setters
    // TODO: Capitalize first letters of makes, models, and colors
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (VerifyUtil.isStringSafe(name)) {
            this.name = name;
        } else {
            Log.w(TAG, "Vehicle name unsafe");
        }
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        if (VerifyUtil.isStringSafe(make)) {
            this.make = make;
        } else {
            Log.w(TAG, "Vehicle make unsafe");
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if (VerifyUtil.isStringSafe(model)) {
            this.model = model;
        } else {
            Log.w(TAG, "Vehicle model unsafe");
        }
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if (VerifyUtil.isYearValid(year)) {
            this.year = year;
        } else {
            Log.w(TAG, "Year was not 4 digits or was not a number");
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        if (VerifyUtil.isStringLettersOnly(color)) {
            this.color = color;
        } else {
            Log.w(TAG, "Color unsafe");
        }
    }

    public int getMileage() {
        return mileage;
    }

    // Type casting from the TextView is all we need for verification
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        if (VIN != null) {
            // VINs only use capitalized letters
            VIN = VIN.toUpperCase();
            if (VerifyUtil.isVINValid(VIN, this.year)) {
                this.VIN = VIN;
            } else {
                Log.w(TAG, "Invalid VIN");
            }
        }
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        if (VerifyUtil.isStringSafe(licensePlate)) {
            this.licensePlate = licensePlate;
        } else {
            Log.w(TAG, "License plate unsafe");
        }
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    // Type casting from the TextView is all we need for verification
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getValue() {
        return value;
    }

    // This is a calculated field, but even if the user types it in,
    // type casting from the TextView is all we need for verification
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Vehicle ID: " + id + ", Name: " + name;
    }
}

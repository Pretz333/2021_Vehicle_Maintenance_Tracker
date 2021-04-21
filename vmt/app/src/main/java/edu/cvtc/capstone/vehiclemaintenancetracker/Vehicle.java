package edu.cvtc.capstone.vehiclemaintenancetracker;

import java.util.Date;

public class Vehicle {
    private int id;
    private String name;
    private String make;
    private String model;
    private String year;
    private String color;
    private int mileage;
    private String VIN;
    private String LicensePlate;
    private Date purchaseDate;
    private double Value;

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
        this.VIN = VIN;
        LicensePlate = licensePlate;
        this.purchaseDate = purchaseDate;
        Value = value;
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
        this.year = year;
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
        this.VIN = VIN;
    }

    public String getLicensePlate() {
        return LicensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        //TODO: Make verification that the licensePlate is valid
        LicensePlate = licensePlate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(double value) {
        Value = value;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Vehicle ID:").append(id)
                .append(", Name:").append(name);
        return builder.toString();
    }
}

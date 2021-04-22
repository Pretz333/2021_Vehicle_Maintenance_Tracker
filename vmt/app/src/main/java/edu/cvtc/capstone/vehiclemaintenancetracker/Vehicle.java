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
        setYear(year); //Using some sets so I can keep DRY
        this.color = color;
        this.mileage = mileage;
        setVIN(VIN);
        setLicensePlate(licensePlate);
        this.purchaseDate = purchaseDate;
        this.value = value;
    }

    public static boolean isVINValid(String VIN, String year){
        //Vehicles pre-1981 don't follow a set standard that can be easily tested
        if (Integer.parseInt(year) < 1981) {
            //If using an API query, we can test vehicles pre-1981
            //For now, just assume it's good
            return true;
        } else if(VIN.length() == 17) { //after 1981, vehicles have a 17 character VIN
            //Vars needed for the test inside of the loop
            int runningTotal = 0;
            int[] multipliers = new int[]{8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};
            char[] VINArray = VIN.toCharArray();
            char c; //More efficient to re-assign it than re-declare it 17 times in the for loop

            for (int i = 0; i < VINArray.length; i++) {
                c = VINArray[i];

                //Characters Q, I, and O are not used due to their similarity to 1 or 0
                //VINs are only letters or digits, so we'll check that as well
                if (!Character.isLetterOrDigit(c) || c == 'Q' || c == 'O' || c == 'I') {
                    return false;
                }

                //Check digit test preparation
                //Convert character to its number, multiply the number by the place
                //multiplier, and lastly add the result to the runningTotal
                runningTotal += numberValues.get(c) * multipliers[i];
            }

            //Check digit test, replace 10 with the character x
            c = runningTotal % 11 == 10 ? 'x' : Character.forDigit(runningTotal % 11, 10);
            return VINArray[8] == c;
        }

        return false;
    }

    private static boolean isLPValid(String licensePlate){
        //TODO: test validity
        return true;
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

    //The HashMap for the VIN check digit test, at the class level so
    //it isn't destroyed and recreated any more than necessary
    private static final HashMap<Character, Integer> numberValues = new HashMap<Character, Integer>() {{
        put('A', 1);
        put('B', 2);
        put('C', 3);
        put('D', 4);
        put('E', 5);
        put('F', 6);
        put('G', 7);
        put('H', 8);
        put('J', 1);
        put('K', 2);
        put('L', 3);
        put('M', 4);
        put('N', 5);
        put('P', 7);
        put('R', 9);
        put('S', 2);
        put('T', 3);
        put('U', 4);
        put('V', 5);
        put('W', 6);
        put('X', 7);
        put('Y', 8);
        put('Z', 9);
        put('1', 1);
        put('2', 2);
        put('3', 3);
        put('4', 4);
        put('5', 5);
        put('6', 6);
        put('7', 7);
        put('8', 8);
        put('9', 9);
        put('0', 0);
    }};
}

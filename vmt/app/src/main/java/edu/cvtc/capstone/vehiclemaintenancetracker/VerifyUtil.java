package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public final class VerifyUtil {
    public static final String TAG = "VERIFYUTIL_CLASS";
    // TODO: Consider non-english characters

    public static void alertUser(Context context, String title, String message) {
        // This will crash if we use null or getApplicationContext() as the context
        // getApplicationContext().toString() looks something like "android.app.Application@5ccffd7"
        // whereas MainActivity.this (a context that will work) looks like edu.cvtc.capstone.vehiclemaintenancetracker.MainActivity@8588ee7
        // So, we filter out contexts with Application in them and null contexts.
        if (context == null || context.toString().contains("Application")) {
            Log.w(TAG, "Invalid context in alertUser");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    // Strings only containing capitalized or lowercase a-z return true
    public static boolean isStringLettersOnly(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        return str.matches("^[a-zA-Z]+$");
    }

    // Strings only containing capital or lowercase a-z and numbers 0-9 return true
    public static boolean isStringLettersOrDigitsOnly(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        return str.matches("^[-a-zA-Z0-9 ]*$"); // don't move the first -
    }

    // Strings containing most special characters, capital or lowercase a-z, and/or numbers 0-9 return true
    public static boolean isTextSafe(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        return str.matches("^[-a-zA-Z0-9 .\"'!,]*$"); // don't move the first -
    }

    // Strings only containing capital or lowercase a-z, numbers 0-9, a " ", or a "-" return true
    public static boolean isStringSafe(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        return str.matches("^[-a-zA-Z0-9 ]*$"); // don't move the first -
    }

    // For Years
    public static boolean isYearValid(String year) {
        if (year == null || year.isEmpty()) {
            return true;
        }

        return year.length() == 4 && Integer.parseInt(year) > 1800;
    }

    public static Date parseStringToDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        if (!str.matches("^[0-9/]*$")) { // they have invalid chars
            return null;
        }

        // Split on the "/"
        String[] chunks = str.split("/", 3);

        if (chunks.length != 3) {
            // they didn't have two "/", meaning they didn't submit something in the proper format
            return null;
        }

        try {
            //Check that the month section is between 1 and 12
            if (Integer.parseInt(chunks[0]) < 1 || Integer.parseInt(chunks[0]) > 12) {
                return null;
            }

            //Check that the day section is between 1 and 31
            if (Integer.parseInt(chunks[1]) < 1 || Integer.parseInt(chunks[1]) > 31) {
                return null;
            }

            //Check that the year section is greater than 1800
            if (Integer.parseInt(chunks[2]) > 1800) {
                return null;
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        if (str.length() != 10) { // They didn't follow MM/dd/yyyy exactly, so something like 1/4/99
            // Add a leading zero to the month if it's only 1 character
            if (chunks[0].length() == 1) {
                chunks[0] = "0" + chunks[0];
            }

            // Add a leading zero to the day if it's only 1 character
            if (chunks[1].length() == 1) {
                chunks[1] = "0" + chunks[1];
            }

            // Fix the year
            if (chunks[2].length() != 4) {
                if (chunks[2].length() == 1) { // assume it's 200X if they put in a X
                    chunks[2] = "200" + chunks[2];
                } else if (chunks[2].length() == 2) {
                    // assume it's 20XX if they put in a 0X or 1X
                    if (chunks[2].toCharArray()[0] == '0' || chunks[2].toCharArray()[0] == '1') {
                        chunks[2] = "20" + chunks[2];
                    } else { // assume it's 19XX if they put in a XX (ex: 1984 if 84)
                        chunks[2] = "19" + chunks[2];
                    }
                } else { // assume it's a typo, what kind of monster submits a "02/04/999" or "02/03/"
                    return null;
                }
            }

            // Reassemble
            str = chunks[0] + "/" + chunks[1] + "/" + chunks[2];
        }

        // Ensure the fixes fixed the problems
        if (str.length() != 10) {
            // then they forgot something, like a section of the date,
            // otherwise it would have been fixed by the above check
            return null;
        }

        try {
            return formatter.parse(str);
        } catch (ParseException ex) {
            return null;
        }
    }

    // For VINs
    public static boolean isVINValid(String VIN, String strYear) {
        if (VIN == null || VIN.isEmpty()) {
            return true;
        }

        // VINs are only letters or digits, so we'll check that as well
        if (!isStringLettersOrDigitsOnly(VIN)) {
            return false;
        }

        // Vehicles pre-1981 don't follow a set standard that can be easily tested
        int year;
        try {
            year = Integer.parseInt(strYear);
        } catch (NumberFormatException ex) {
            return false;
        }

        if (year > 1800 && year < 1981) {
            // If using an API query, we can test vehicles pre-1981
            // For now, we'll just assume it's good
            return true;
        } else if (VIN.length() == 17) { // after 1981, vehicles have a 17 character VIN
            // Vars needed for the test inside of the loop
            int runningTotal = 0;
            int[] multipliers = new int[]{8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};
            char[] VINArray = VIN.toCharArray();
            char c; // More efficient to re-assign it than re-declare it 17 times in the for loop

            // Year digit (10th digit) check
            // Start with getting the digit as a year
            int yearFromChar;
            try {
                yearFromChar = yearChar.get(VINArray[9]);
            } catch (NullPointerException ex) {
                // The year digit is an invalid char
                return false;
            }

            // Ensure we got a year
            if (yearFromChar < 1980 || yearFromChar > 2009) {
                // We failed to get a year from the HashMap
                return false;
            }

            // If the year passed in is > 2009, subtract 30 so it aligns with
            // the HashMap, since the pattern loops every 30 years.
            // For example, A can be for a VIN from 1980 or a VIN from 2010.
            while (year > 2009) {
                year -= 30;
            }

            // Check if the VIN's year digit is equal to the year passed in from the vehicle
            // Don't return true if it passes, as we want to run other tests as well
            if (year != yearFromChar) {
                return false;
            }

            // Check the rest of the VIN
            for (int i = 0; i < VINArray.length; i++) {
                c = VINArray[i];

                // Characters Q, I, and O are not used due to their similarity to 1 or 0
                if (c == 'Q' || c == 'O' || c == 'I') {
                    return false;
                }

                // Check digit test preparation
                // Convert character to its number, multiply the number by the place
                // multiplier, and lastly add the result to the runningTotal
                runningTotal += numberValues.get(c) * multipliers[i];
            }

            // Check digit test, replace 10 with the character x
            c = runningTotal % 11 == 10 ? 'x' : Character.forDigit(runningTotal % 11, 10);
            return VINArray[8] == c;
        }

        return false;
    }

    // The HashMap for the VIN check digit test, at the class level so
    // it isn't destroyed and recreated any more than necessary
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

    // The HashMap for the VIN year digit, also at the class level so
    // it isn't destroyed and recreated any more than necessary
    private static final HashMap<Character, Integer> yearChar = new HashMap<Character, Integer>() {{
        put('A', 1980);
        put('B', 1981);
        put('C', 1982);
        put('D', 1983);
        put('E', 1984);
        put('F', 1985);
        put('G', 1986);
        put('H', 1987);
        put('J', 1988);
        put('K', 1989);
        put('L', 1990);
        put('M', 1991);
        put('N', 1992);
        put('P', 1993);
        put('R', 1994);
        put('S', 1995);
        put('T', 1996);
        put('V', 1997);
        put('W', 1998);
        put('X', 1999);
        put('Y', 2000);
        put('1', 2001);
        put('2', 2002);
        put('3', 2003);
        put('4', 2004);
        put('5', 2005);
        put('6', 2006);
        put('7', 2007);
        put('8', 2008);
        put('9', 2009);
    }};
}

package edu.cvtc.capstone.vehiclemaintenancetracker;

import java.util.HashMap;

public final class VerifyUtil {
    //Strings containing only capital or lowercase a-z return true
    public static boolean isStringLettersOnly(String str){
        return str.matches("^[a-zA-Z]+$");
    }

    //Strings containing only numbers 0-9 return true
    public static boolean isStringANumber(String str){
        return str.matches("^[0-9]*$");
    }

    //Strings containing only capital or lowercase a-z and numbers 0-9 return true
    public static boolean isStringLettersOrDigitsOnly(String str){
        return str.matches("^[a-zA-Z0-9]+$");
    }

    //Strings containing only capital or lowercase a-z, numbers 0-9, a " ", or a "-" return true
    public static boolean isStringSafe(String str) {
        return str.matches("^[a-zA-Z0-9 -]*$");
    }

    //For VINs
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

package edu.cvtc.capstone.vehiclemaintenancetracker;

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
}

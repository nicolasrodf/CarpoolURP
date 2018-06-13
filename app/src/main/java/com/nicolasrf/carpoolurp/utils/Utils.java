package com.nicolasrf.carpoolurp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nicolas on 12/06/2018.
 */

public class Utils {

    //Date and Time

    public static int convertTimeToMs(int hours, int minutes) {
//        String[] tokens = timeData.split(":");
//        int hours = Integer.parseInt(tokens[0]);
//        int minutes = Integer.parseInt(tokens[1]);
        int duration = 3600 * hours + 60 * minutes;
        return duration;
    }

    public static String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String myTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return myTime;

    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String hireDate = sdf.format(date);
        return hireDate;
    }

    //Transformar costo y asientos a desde string a numerico.-

    public static int travelCostToNumeric(String travelCost){

        int numericCost = 0;

        switch(travelCost){

            case "S/ 2":
                numericCost = 2;
                break;
            case "S/ 3":
                numericCost = 3;
                break;
            case "S/ 5":
                numericCost = 5;
                break;
            case "S/ 7":
                numericCost = 7;
                break;
            case "S/ 10":
                numericCost = 10;
                break;
            case "S/ 15":
                numericCost = 15;
                break;
            default:
                break;

        }
        return numericCost;
    }

    public static int numberOfSeatsToNumeric(String numberOfSeats) {

        int numericCost = 0;

        switch (numberOfSeats) {

            case "1":
                numericCost = 1;
                break;
            case "2":
                numericCost = 2;
                break;
            case "3":
                numericCost = 3;
                break;
            case "4":
                numericCost = 4;
                break;
            default:
                break;

        }
        return numericCost;
    }
}

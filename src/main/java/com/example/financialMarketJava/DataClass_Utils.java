package com.example.financialMarketJava;

import java.util.ArrayList;
import java.util.Calendar;

public class DataClass_Utils {
	
	public static ArrayList<String> getTimeAndUnitFromString (String dataInString) {
		
		ArrayList<String> stringComponents = new ArrayList<String>();
		
		// The date in String is like "1d", "1mo", "2mo", ...
		// Isolate the time components (months, days, years, ...) and the quantity components (1, 2, 10, ...)
		
        // Create variables to store the different date parts
        StringBuilder numberPart = new StringBuilder();
        StringBuilder timePart = new StringBuilder();
        
        // Iterate the date list
        for (char c : dataInString.toCharArray()) {
            if (Character.isDigit(c)) {
                // If number, add it to the numerical part
                numberPart.append(c);
            } else {
                // else, append it to the time part
                timePart.append(c);
            }
        }
        
        // Convert the numeric part in numbers
        String unit = timePart.toString();
        
        stringComponents.add(unit);
        stringComponents.add(numberPart.toString());
        
        return stringComponents;
	}
	
	public static Calendar getDateFromString(String dataInString) {
		
		// Unpack data from the previous method
		ArrayList<String> dateComponents = getTimeAndUnitFromString(dataInString);
		
        int quantity = Integer.parseInt(dateComponents.get(1).toString());
        String unit = dateComponents.get(0);
        
        // Now, we got everything to translate the string into calendar
        // Create the Calendar Instance
        Calendar calendar = Calendar.getInstance();
        
        // Define Unity
        int calendarUnity = 0;
        if (unit == "y") {
        	calendarUnity = Calendar.YEAR;
        } 
        else if (unit == "mo") {
        	calendarUnity = Calendar.MONTH; 
        }
        else if (unit == "d") {
        	calendarUnity = Calendar.DAY_OF_YEAR; 
        }

        // get the final Calendar Object
        calendar.add(calendarUnity, -quantity);
        
        return calendar;
	}
	
}

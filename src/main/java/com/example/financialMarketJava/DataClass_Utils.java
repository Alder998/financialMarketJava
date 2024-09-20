package com.example.financialMarketJava;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
	
	public static OffsetDateTime extractOffsetDateTimeFromString (String dateInString) {
				
        OffsetDateTime date = OffsetDateTime.now(ZoneOffset.of("+02:00"));
		// Split the string into spaces
        String[] words = dateInString.split(" ");

        if (words.length >= 4) {
            // Take the first three words that are the date paramters (mo, day, year)
            int day = Integer.parseInt(words[1].replace(",", ""));
            int year = Integer.parseInt(words[2]);
            int month = getMonthFromMonthName(words[0]);
            date = date.withMonth(month).withYear(year).withDayOfMonth(day);
        }
		return date;
	}
	
	public static int getMonthFromMonthName(String monthString) {
		int monthNumber = 0;
		
		// stupid method to get the month number, but required
		if (monthString.equals("Jan")) {
			monthNumber = 1;
		}
		else if (monthString.equals("Feb")) {
			monthNumber = 2;
		}
		else if (monthString.equals("Mar")) {
			monthNumber = 3;
		}
		else if (monthString.equals("Apr")) {
			monthNumber = 4;
		}
		else if (monthString.equals("May")) {
			monthNumber = 5;
		}
		else if (monthString.equals("Jun")) {
			monthNumber = 6;
		}
		else if (monthString.equals("Jul")) {
			monthNumber = 7;
		}
		else if (monthString.equals("Aug")) {
			monthNumber = 8;
		}
		else if (monthString.equals("Sep")) {
			monthNumber = 9;
		}
		else if (monthString.equals("Oct")) {
			monthNumber = 10;
		}
		else if (monthString.equals("Nov")) {
			monthNumber = 11;
		}
		else if (monthString.equals("Dec")) {
			monthNumber = 12;
		}
		else {
            throw new IllegalArgumentException("No month found for the name" + monthString);
		}
		return monthNumber;
	}
	
}

package com.example.financialMarketJava;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;

import Objects.HistoricalTimeSeries;

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
	
	public static String getUnixTimeStampFromString (String intervalInString) {
		
        OffsetDateTime date = OffsetDateTime.now(ZoneOffset.of("+02:00"));
		if (intervalInString.equals("now")) {
	        long unixTimestamp = date.toEpochSecond();
	        String currentTimeStamp = Long.toString(unixTimestamp);
	        
	        return currentTimeStamp;
		}
		else {
			// First, we need to get the date components, and we got a function for it
			ArrayList<String> components =  getTimeAndUnitFromString(intervalInString);
	        int quantity = Integer.parseInt(components.get(1).toString());
	        String unit = components.get(0);
	
			// Secondly, it is needed to get in OffsetDateTime the date back at the interval that has been selected
	        if (unit.equals("y")) {
	        	date = date.minusYears(quantity);
	        }
	        else if (unit.equals("mo")) {
	        	date = date.minusMonths(quantity);
	        }
	        else if (unit.equals("d")) {
	        	date = date.minusDays(quantity);
	        }
	        
	        // finally, convert the date to Unix Time stamp
	        long unixTimestamp = date.toEpochSecond();
	        String finalTimeStamp = Long.toString(unixTimestamp);
	        
	        return finalTimeStamp;
		}
	}
	
	public static ArrayList<HistoricalTimeSeries> updateHistoryForStocksOperations (ArrayList<HistoricalTimeSeries> history, String byWhat) {
		
		 ArrayList<HistoricalTimeSeries> finalHistory = new ArrayList<HistoricalTimeSeries>();
		 
	    if (byWhat.equals("Splits")) {

	        // "Clean" the History Array
	        finalHistory = history.stream().filter((x) -> x.getStockSplit().equals("NaN"))
			  													.collect(Collectors.toCollection(ArrayList::new));
	        
	        // Some works now is needed to update the extra operations (Stock Splits and dividends)
	        ArrayList<HistoricalTimeSeries> stockSplitsObjects = history.stream().filter((x) -> !x.getStockSplit().equals("NaN"))
	        											  		.collect(Collectors.toCollection(ArrayList::new));
	        for (HistoricalTimeSeries obj : stockSplitsObjects) {
	        	
	        	// update again the final History Object
	        	finalHistory = finalHistory.stream().filter((x) -> x.getDate().getDayOfYear() != obj.getDate().getDayOfYear() ||
																   x.getDate().getYear() != obj.getDate().getYear())
																   .collect(Collectors.toCollection(ArrayList::new));
	        	
	           	// Isolate the dates in which the stocks splits has happened
	            ArrayList<HistoricalTimeSeries> stockSplitsDates = history.stream().filter((x) -> x.getStockSplit().equals("NaN") &&
	            													x.getDate().getDayOfYear() == obj.getDate().getDayOfYear() && 
	            													x.getDate().getYear() == obj.getDate().getYear())
				  												   .collect(Collectors.toCollection(ArrayList::new));
	            for (HistoricalTimeSeries obj1 : stockSplitsDates) {
	            	obj1.setStockSplit(obj.getStockSplit());
	            	
	            	finalHistory.add(obj1);
	            }
	        }
	        
	        // Sort the Stock List
	        finalHistory.sort((s1, s2) -> s1.getDate().compareTo(s2.getDate()));
	        
	    }
        
        else if (byWhat.equals("Dividends")) {
            // "Clean" the History Array
            finalHistory = history.stream().filter((x) -> x.getDividend() == 0)
    		  										.collect(Collectors.toCollection(ArrayList::new));
            
            // Some works now is needed to update the extra operations (Stock Splits and dividends)
            ArrayList<HistoricalTimeSeries> stockSplitsObjects = history.stream().filter((x) -> x.getDividend() != 0)
            											  		.collect(Collectors.toCollection(ArrayList::new));
            for (HistoricalTimeSeries obj : stockSplitsObjects) {
            	
	        	// update again the final History Object
	        	finalHistory = finalHistory.stream().filter((x) -> x.getDate().getDayOfYear() != obj.getDate().getDayOfYear() || 
																   x.getDate().getYear() != obj.getDate().getYear())
																   .collect(Collectors.toCollection(ArrayList::new));
            	
               	// Isolate the dates in which the stocks splits has happened
                ArrayList<HistoricalTimeSeries> stockSplitsDates = history.stream().filter((x) -> x.getDividend() == 0 &&
                													x.getDate().getDayOfYear() == obj.getDate().getDayOfYear() && 
                													x.getDate().getYear() == obj.getDate().getYear())
    			  												   .collect(Collectors.toCollection(ArrayList::new));
                for (HistoricalTimeSeries obj1 : stockSplitsDates) {
                	obj1.setDividend(obj.getDividend());
                	
                	finalHistory.add(obj1);
                }
            }
            // Sort the Stock List
            finalHistory.sort((s1, s2) -> s1.getDate().compareTo(s2.getDate())); 
        }
        return finalHistory;
	}
	
	// method to Expand Matrix
	public static float[][] expandMatrix(float[][] originalMatrix, int newSize) {
	    int oldSize = originalMatrix.length;

	    // New Matrix creation with new sizes
	    float[][] expandedMatrix = new float[newSize][newSize];

	    // Copy the existing values in the new Matrix
	    for (int i = 0; i < oldSize; i++) {
	        for (int j = 0; j < oldSize; j++) {
	            expandedMatrix[i][j] = originalMatrix[i][j];
	        }
	    }
	    return expandedMatrix;
	}
	
}

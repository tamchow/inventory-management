package Miscellaneous;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * a class containing a few utility methods and a few codes commonly used by parts of the program
 */
public class Codes {
    //codes
    public static final String MAIN_FILE = "main.cfg", PARSER_FILE = "parser.cfg", FILENAME = "Database.imdb", LOG_FILE = "run.log", EXPORT_FILE = "Database_Exported.csv", EMPTY = "Empty";
    public static final String DATE_COLUMN = "DATE_COLUMN", TIME_COLUMN = "TIME_COLUMN", DATE_TIME_COLUMN = "DATE_TIME_COLUMN", TEXT_COLUMN = "TEXT_COLUMN", NUMERIC_COLUMN = "NUMERIC_COLUMN";
    public static final String SEPARATOR = "~";
    //Parser features
    public static final String[] PARSER_OPTIONS = {"CHECK_EXPIRY_DATES", "CHECK_UPDATES", "PRODUCT_STATISTICS", "PROJECTED_USAGE", "ENUMERATE"};

    /**
     * method to generate the proper number of spaces required to align the output
     */
    public static String properSpacing(int longest, String currentString) {
        StringBuilder s = new StringBuilder();
        for (int i = currentString.length(); i <= longest + 2; i++)//add 2 more spaces just for good measure
            s.append(" ");
        return s.toString();
    }

    /**
     * a method to remove empty lines from an ArrayList&lt;String&gt;
     */
    public static void fixInput(ArrayList<String> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals(""))
                data.remove(i);
        }
    }

    /**
     * returns a string representation of the current time
     */
    public static String currentTime() {
        DifferentialCalendar dc = new DifferentialCalendar();
        return dc.get(Calendar.DATE) + "." + (dc.get(Calendar.MONTH) + 1) + "." + dc.get(Calendar.YEAR) + " " + dc.get(Calendar.HOUR_OF_DAY) + ":" + dc.get(Calendar.MINUTE) + ":" + dc.get(Calendar.SECOND);
    }

    /**
     * returns a string representation of the current date
     */
    public static String currentDate() {
        DifferentialCalendar dc = new DifferentialCalendar();
        return dc.get(Calendar.DATE) + "." + (dc.get(Calendar.MONTH) + 1) + "." + dc.get(Calendar.YEAR);
    }
}
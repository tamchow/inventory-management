package UI;

import Data.Database;
import Miscellaneous.Codes;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Notifications {
    private PrintWriter pw;//we log using this
    private String time;//this is the current time
    private static boolean printed = false;//flag to tell us if we have printed the time or not

    /**
     * default constructor
     */
    private Notifications() throws IOException {
        pw = new PrintWriter(new FileWriter(Codes.LOG_FILE, true));
        time = Codes.currentTime();
    }

    /**
     * internal method to log a message to a file
     */
    private void log(String toLog) {
        if (!printed) {//we haven't printed the timestamp
            pw.println("\nRun on:" + time);//now we know when this was logged
            printed = true;//we don't want to print the timestamp again
        }
        pw.println(toLog);//writing to the file
        pw.flush();//finalize any pending output
    }

    /**
     * method to show a general message
     */
    public static void showNotification(String msg) throws IOException {
        Notifications n = new Notifications();//object created
        System.out.println("Message:" + msg);//message printed
        n.log("Message:" + msg);//message logged
    }

    /**
     * method to show a message for a specific product
     */
    public static void showNotification(String[] product, String msg, Database db, int index) throws IOException {
        Notifications n = new Notifications();//object created
        System.out.println("Information for product registered at index " + index + " in the database.");
        n.log("Information for product registered at index " + index + " in the database.");
        StringBuilder headers = new StringBuilder();
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < db.getHeaders().length && i < product.length; i++) {
            headers.append(db.getHeaders()[i]).append(Codes.properSpacing(db.lengthOfLongestElement(), db.getHeaders()[i]));
            data.append(product[i]).append(Codes.properSpacing(db.lengthOfLongestElement(), product[i]));
        }
        System.out.println(headers + "\n" + data);//product info printed
        n.log(headers.toString());//headers logged
        n.log(data.toString());//product info logged
        System.out.println("Message for product registered at index " + index + " in the database.\n" + msg);//message printed
        n.log(msg);//message logged
    }
}
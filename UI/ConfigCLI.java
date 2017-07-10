package UI;

import Data.Database;
import Miscellaneous.Codes;
import Miscellaneous.StringTokens;

import java.io.*;
import java.util.ArrayList;

/**
 * class which handles the configuration of the system
 */
public class ConfigCLI {
    private String username, password, status, auto_entry;//main configuration files' data
    private ArrayList<String> opt;//stores various data
    private BufferedReader br;//console input source

    /**
     * our default construcor
     */
    private ConfigCLI() {
        username = "";
        password = "";
        status = "";
        auto_entry = "";
        opt = new ArrayList<>(10);
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * a method to create object and call methods
     */
    public static void init() throws IOException {
        ConfigCLI cc = new ConfigCLI();
        cc.configureAdmin();
        cc.configureDatabase();
        cc.configureParser();
    }

    /**
     * method to configure the user account and some general options
     */
    private void configureAdmin() throws IOException {
        System.out.println("Configuring user account...");
        System.out.println("Enter an administrator username:");
        username = br.readLine();//input
        System.out.println("Enter an administrator password for " + username + ":");
        password = String.valueOf(br.readLine().hashCode());//input
        System.out.println("Enter the code denoting a product or batch of items which have been accepted into the inventory:");
        status = br.readLine();//input
        System.out.println("Enter the Automatic Entry Date field header, enter nothing to disable this feature:");
        auto_entry = br.readLine();//input
        if (!auto_entry.equals(""))
            System.out.println("The Automatic Entry Date field is not immediately included,please include it during the database configuration.");
        PrintWriter pw = new PrintWriter(new FileWriter(Codes.MAIN_FILE));//write the data here
        pw.println("USERNAME:" + username);
        pw.println("PASSWORD:" + password);
        pw.println("ACCEPTED_STATUS_CODE:" + status);
        pw.print("AUTOMATIC_ENTRY_DATE_HEADER:" + auto_entry);
        pw.flush();
        pw.close();
    }

    /**
     * method to configure the database
     */
    private void configureDatabase() throws IOException {
        System.out.println("Configuring Database...");
        int rows, fields;
        System.out.println("Enter number of fields in database:");
        fields = Integer.parseInt(br.readLine());//input
        for (int i = 1; i <= fields; i++) {
            System.out.println("Enter header for field " + i + ":");
            opt.add(br.readLine());//input
        }
        System.out.println("Enter initial number of products:");
        rows = Integer.parseInt(br.readLine());//input
        Database db = new Database(rows, fields);
        db.setHeaders(opt.toArray(new String[opt.size()]));//set the field headers
        db.changeNullElements();//No NullPointerExceptions please!
        opt.clear();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Codes.FILENAME));//serialize the database here
        oos.writeObject(db);//output
        oos.flush();
        oos.close();
    }

    /**
     * method to configure the parser
     */
    private void configureParser() throws IOException {
        System.out.println("Configuring Autonomic Functions...");
        PrintWriter pw = new PrintWriter(new FileWriter(Codes.PARSER_FILE));
        System.out.println("Parser options are:\n1:Check Expiry Dates\n2:Check for Information updates\n3:Generate Product Statistics");
        System.out.println("4:Calculate Projected Usage\n5:Enumerate Accepted Items");
        System.out.println("Enter the option numbers you want enabled (comma-separated list):");
        StringTokens st = new StringTokens(br.readLine(), ",");//input processed
        while (st.hasMoreTokens()) {
            switch (Integer.parseInt(st.nextToken())) {
                case 1:
                    System.out.println("Configuring Expiry Date Function...");
                    System.out.println("Enter Field Name which contains Estimated Time of Delivery:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Expiry Date('.' or '/' as delimiters for dates):");
                    opt.add(br.readLine());//input
                    pw.println(Codes.PARSER_OPTIONS[0] + ":" + opt.get(0) + "," + opt.get(1));//output
                    pw.flush();
                    opt.clear();
                    break;
                case 2:
                    System.out.println("Configuring Information Update Function...");
                    System.out.println("Enter Field Name which contains Information Update Warning Time:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Last Update Date('.' or '/' as delimiters for dates):");
                    opt.add(br.readLine());//input
                    pw.println(Codes.PARSER_OPTIONS[1] + ":" + opt.get(0) + "," + opt.get(1));//output
                    pw.flush();
                    opt.clear();
                    break;
                case 3:
                    System.out.println("Configuring Product Statistics Function...");
                    System.out.println("Enter Field Name which contains Product Rate:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Batch Cost Price:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Number of sold items:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which will contain Profit/Loss of a product:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which will contain Profit/Loss percentage of a product:");
                    opt.add(br.readLine());//input
                    pw.println(Codes.PARSER_OPTIONS[2] + ":" + opt.get(0) + "," + opt.get(1) + "," + opt.get(2) + "," + opt.get(3) + "," + opt.get(4));//output
                    pw.flush();
                    opt.clear();
                    break;
                case 4:
                    System.out.println("Configuring Projected Usage Function...");
                    System.out.println("Enter Field Name which contains Number of used items:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Total number of items:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Supply Period (Estimated Time of Delivery):");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Overage factor for a product:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Last Entry Modification Date('.' or '/' as delimiters for dates):");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which will contain Projected Usage:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which will contain Number of items required:");
                    opt.add(br.readLine());//input
                    pw.println(Codes.PARSER_OPTIONS[3] + ":" + opt.get(0) + "," + opt.get(1) + "," + opt.get(2) + "," + opt.get(3) + "," + opt.get(4) + "," + opt.get(5) + "," + opt.get(6));//output
                    pw.flush();
                    opt.clear();
                    break;
                case 5:
                    System.out.println("Configuring Enumeration Function...");
                    System.out.println("Enter Field Name which contains Product Name:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Product ID:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Number of used items:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Total number of items:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which contains Product Status:");
                    opt.add(br.readLine());//input
                    System.out.println("Enter Field Name which will contain Number of items remaining:");
                    opt.add(br.readLine());//input
                    pw.println(Codes.PARSER_OPTIONS[4] + ":" + opt.get(0) + "," + opt.get(1) + "," + opt.get(2) + "," + opt.get(3) + "," + opt.get(4) + "," + opt.get(5));//output
                    pw.flush();
                    opt.clear();
                    break;
            }
        }
    }
}
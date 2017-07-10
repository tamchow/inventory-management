import Autonomic.Parser;
import Data.Database;
import Miscellaneous.*;

import java.util.ArrayList;

import UI.*;

import java.io.*;

/**
 * The main class which loads the other classes at runtime
 */
public class InventoryManagementLoader {
    private Database db;//stores the data
    private String username, status, auto_entry;//stores configuration data
    private int password;//password is stored hashed for security
    private static BufferedReader br;//we take console input from here
    private final File[] FILES = {(new File(Codes.FILENAME)), (new File(Codes.MAIN_FILE)), (new File(Codes.PARSER_FILE))};//these are the required files
    private ArrayList<String> ar;//stores file data
    private boolean admin;

    /**
     * sole constructor
     */
    private InventoryManagementLoader() throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));//initializing our input source
        if (!checkFiles())//checking that all required files are present
            ConfigCLI.init();//all files are not present,start up the configuration wizard

        else {//initialize everything with defaults
            username = "";
            password = 0;
            status = "";
            auto_entry = "";
            admin = false;
            ar = new ArrayList<>(5);
            loadMain();//call the method which loads the main file
            authorize();//call the method which handles username and password verification
        }
    }

    /**
     * internal method to check if all required files are intact
     */
    private boolean checkFiles() {
        for (File FILE : FILES) {
            if (!FILE.exists())
                return false;//a file does not exist
        }
        return true;//all files exist
    }

    /**
     * internal method to load configuration file
     */
    private void loadMain() throws IOException {
        BufferedReader br1 = new BufferedReader(new FileReader(FILES[1]));
        String s = br1.readLine();
        while (s != null) {
            ar.add(s);
            s = br1.readLine();
        }
        Codes.fixInput(ar);//remove any blank lines from the input
        username = ar.get(0).substring(ar.get(0).indexOf(':') + 1, ar.get(0).length());//read username
        password = Integer.parseInt(ar.get(1).substring(ar.get(1).indexOf(':') + 1, ar.get(1).length()));//read password hash
        status = ar.get(2).substring(ar.get(2).indexOf(':') + 1, ar.get(2).length());//read accepted status code
        auto_entry = ar.get(3).substring(ar.get(3).indexOf(':') + 1, ar.get(3).length());//read Automatic Edit time field header
        ar.clear();//clean up for the next method
    }

    /**
     * internal method to handle user authentication\nUnlimited attempts! If the user does not remember the password, he/she can look it up in main.cfg
     */
    private void authorize() throws Exception {
        System.out.println("Authentication menu:");
        outer:
        while (true) {
            System.out.println("Do you want to authenticate yourself as administrator or proceed as a common user?\nEnter Y for yes,N for No.");
            char c = br.readLine().charAt(0);
            switch (c) {
                case 'y':
                case 'Y':
                    System.out.println("Enter username:");
                    String u = br.readLine();
                    if (!u.equals(username)) {
                        System.out.println("Wrong username!");
                        continue;
                    }
                    System.out.println("Enter password for " + u + ":");
                    String p = br.readLine();
                    if (p.hashCode() != password) {
                        System.out.println("Wrong password for " + u + "!");
                        continue;
                    }
                    if (u.equals(username) && (p.hashCode() == password)) {//user is authenticated
                        admin = true;//set administrator
                        break outer;//break the loop
                    }
                case 'n':
                case 'N':
                    admin = false;
                    break outer;
                default:
            }
        }
        init();//call the method to load the database management system
    }

    /**
     * internal method to load the other classes as requires by the user
     */
    private void init() throws Exception {
        loadDB();//deserialize the database
        outer:
        while (true) {
            //show options
            System.out.println("Main menu:");
            System.out.println("1:Start Parsing\n2:Start Browsing\n3:Start Configuration System\n4:Export Current Database\n5:Import a database\n6:Exit");
            switch (Integer.parseInt(br.readLine())) {
                case 1://start the parser
                    BufferedReader br2 = new BufferedReader(new FileReader(FILES[2]));
                    String s = br2.readLine();
                    while (s != null) {
                        ar.add(s);//read the options in the parser configuration file
                        s = br2.readLine();
                    }
                    Codes.fixInput(ar);//same as before
                    Parser p = new Parser(db, ar.toArray(new String[ar.size()]), status);//initializing the database parser
                    p.parse();//handing control to the parser
                    break;
                case 2://start the editor
                    Editor e = new Editor(db, auto_entry);//initializing the editor
                    e.browse();
                    break;//the editor is capable of returning to the main menu
                case 3://configure everything again?
                    ConfigCLI.init();//configuration system is self-initialising, just hand over control to it
                    break outer;//that's all, after configuration, the program needs to be restarted anyway
                case 4://export the current database to a Database_Exported.csv file,which opens in MS Excel
                    new ExportImport().exporter(db, auto_entry);//initializing the export system and handing over control to it!
                    break;//we can come back to the main menu
                case 5://import an existing exported database(using this program on a different computer?)
                    new ExportImport().importer(db, auto_entry);//now it's the import system
                    break;//same as case 4
                case 6://No!!! Don't leave me behind!(Can't do anything about it anyway, can I?)
                    System.out.println("Exiting...");
                    System.exit(0);//Bye-Bye
                default://Stop making mistakes in input!
                    //I'm very considerate, I give everybody infinite chances
            }
        }
    }

    /**
     * internal method to load the database
     */
    private void loadDB() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILES[0]));//Recovering a persistently-stored database
        db = (Database) ois.readObject();//Easy, ain't it!(P.S. Don't forget the typecast here, it's absolutely necessary
    }

    /**
     * main method. @param java.lang.String[]args the command-line options(I don't use them. Do you?)
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Inventory Management Database loaded on " + Codes.currentTime());
        new InventoryManagementLoader();//don't even need to name the object, because we don't use it anyway
    }
}
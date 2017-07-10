package UI;

import Data.Database;
import Miscellaneous.Codes;
import Miscellaneous.Pairs;
import Miscellaneous.StringTokens;

import java.io.*;
import java.util.ArrayList;

/**
 * the class which handles exporting and importing of databases
 */
public class ExportImport {
    private Database db;//the database to be exported or imported to
    private ArrayList<String> arr;//stores file data
    private String autoEntry;//same as that in Editor, we need this as we use the editor from here

    /**
     * method which initialises the exporting system
     */
    public void exporter(Database db, String autoEntry) throws IOException {
        this.db = db;
        this.autoEntry = autoEntry;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter the directory to which the current database will be exported:");
            String s = br.readLine();//input
            File f = new File(s);
            if (f.exists() && f.isDirectory()) {//the directory path is valid
                handleExport(s);//do the actual export
                break;
            }
        }
    }

    /**
     * method which initialises the importing system
     */
    public void importer(Database db, String autoEntry) throws IOException {
        this.db = db;
        this.autoEntry = autoEntry;
        this.arr = new ArrayList<>(db.rows());//get ready to read the file
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter the directory and the timestamp of the .csv file from which the database will be imported:");
            String s = br.readLine();
            File f = new File(s);
            if (f.exists()) {//the file path is valid
                handleImport(s);//do the actual import
                break;
            }
        }
    }

    /**
     * method which handles the actual export
     */
    private void handleExport(String dirPath) throws IOException {
        File f = new File(dirPath + Codes.EXPORT_FILE);
        //create a new file
        if (!f.createNewFile()) {
            System.out.println("File \"" + f + "\" already exists");
        }
        PrintWriter pw = new PrintWriter(new FileWriter(f));//we write here
        pw.println("# Exported on:" + Codes.currentTime());//putting the timestamp
        for (int i = 0; i < db.rows(); i++) {
            for (int j = 0; j < db.columns(); j++) {
                if (j == db.columns() - 1)
                    pw.println(db.get(i)[j]);//last for a column, don't print the comma
                else
                    pw.print(db.get(i)[j] + ",");//print the data followed by a comma
            }
        }
        pw.flush();
        System.out.println("Database successfully exported.");//YAY!!!
        System.out.println("Please do not edit the exported file's #<timestamp> line,\notherwise importing it will not work correctly.");//OHHH!
    }

    /**
     * method which handles the actual import
     */
    private void handleImport(String dirPath) throws IOException {
        File f = new File(dirPath + Codes.EXPORT_FILE);
        if (!f.exists()) {
            System.out.println(dirPath + Codes.EXPORT_FILE + " does not exist.");
            return;
        }
        BufferedReader br1 = new BufferedReader(new FileReader(f));//we read from here
        String line = br1.readLine();
        while (line != null) {//read the input
            arr.add(line);
            line = br1.readLine();
        }
        Codes.fixInput(arr);
        arr.remove(0);//removing timestamp comment line(# Exported on: <date> <time>)
        System.out.println("Please check the current data in the database.");//show the user the database as he might need to look up the field headers
        Editor e = new Editor(db, autoEntry);
        e.browse();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokens st = new StringTokens(arr.get(0), ",");
        ArrayList<String> headers = new ArrayList<>(st.splitCounter());
        Pairs<String, String> impHeaders = new Pairs<>(st.splitCounter());
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            headers.add(s);//read the headers
        }
        for (int i = 0; i < headers.size(); i++) {//correlate the imported headers to the current headers
            System.out.println("What does the header " + headers.get(i) + " correspond to in the current database? Enter \"Empty\" if there is no such header:\n");
            String in = br.readLine();
            if (!in.equalsIgnoreCase(Codes.EMPTY)) {//imported header does not exist in the current database
                impHeaders.add(in, headers.get(i));
                headers.remove(i);//remove the imported header
                i--;
            } else//imported header exists in the current database
                impHeaders.add(headers.get(i), headers.get(i));
        }
        ArrayList<String> newHeaders = new ArrayList<>((db.getHeaders().length + headers.size()));//to store the new headers
        int k = 0;
        for (int i = 0; i < (db.getHeaders().length + headers.size()); i++) {
            if (i < db.getHeaders().length)
                newHeaders.add(db.getHeaders()[i]);//add the headers of the current database
            else if (headers.size() > 0) {
                newHeaders.add(headers.get(k));//add the imported headers
                k++;
            }
        }
        String[] newData = new String[newHeaders.size()];
        db.resize(db.rows(), newHeaders.size());//resize the database to contain the new headers
        db.setHeaders(newHeaders.toArray(new String[newHeaders.size()]));//set the new headers
        Editor edit = new Editor(db, autoEntry);//show the user the new database
        edit.browse();
        int idx = db.size();
        for (int i = 1; i < arr.size(); i++) {
            System.out.println("Importing entry " + i + " of " + (arr.size() - 1) + " entries. Please wait...");
            StringTokens st2 = new StringTokens(arr.get(i), ",");
            ArrayList<String> entry = new ArrayList<>();
            boolean added = false;
            while (st2.hasMoreTokens())//read an entry
                entry.add(st2.nextToken());
            for (int j = 0; j < entry.size(); j++) {
                int field = db.getHeaderIndex(newHeaders.get(j));//get the database header corresponding to the imported header
                if (!added) {//adding a new entry
                    newData[field] = entry.get(j);
                    db.add(newData);
                    db.changeNullElements();
                    added = true;
                    idx = db.size();
                } else {//modifying an existing entry
                    newData = db.get(idx);
                    newData[field] = entry.get(j);
                    db.set(idx, newData);
                    db.changeNullElements();//No NullPointerExceptions please!
                }
            }
        }
        Editor edit2 = new Editor(this.db, autoEntry);//show the imported database to the user
        edit2.browse();
        System.out.println("Database successfully imported.");//show the message
    }
}
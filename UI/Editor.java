package UI;

import Data.Database;
import Miscellaneous.Codes;
import Miscellaneous.StringTokens;

import java.io.*;
import java.util.ArrayList;

/**
 * the most important class which encapsulates the editor for our database
 */
public class Editor {
    private Database db;//the database to edit
    private int index;//the current editing index in the database
    private BufferedReader br;//our input source
    private String[] newData;//stores the array which is added to the database when a new entry is made
    private String auto_entry;//stores the automatic last modification date entry field header

    /**
     * constructor to set the database to edit and the auto_entry field header
     */
    public Editor(Database db, String auto_entry) {
        this.db = db;
        this.index = 1;//we skip the field headers
        this.auto_entry = auto_entry;
        newData = new String[db.columns()];//initialise the newData here(Always be prepared!)
        for (int i = 0; i < newData.length; i++) {
            newData[i] = Codes.EMPTY;//No NullPointerExceptions, please!
        }
    }

    /**
     * the method responsible for showing the editor options
     */
    public void browse() throws IOException {
        this.br = new BufferedReader(new InputStreamReader(System.in));//this avoids the odd problem of input getting skipped
        System.out.println("These are the registered products:");
        for (int i = 0; i < db.getHeaders().length; i++)
            System.out.print(db.getHeaders()[i] + Codes.properSpacing(db.lengthOfLongestElement(), db.getHeaders()[i]));//formatted output
        System.out.println();
        for (int i = 1; i < db.rows(); i++) {
            System.out.println("Information of product:" + i);
            for (int j = 0; j < db.columns(); j++)
                System.out.print(db.get(i)[j] + Codes.properSpacing(db.lengthOfLongestElement(), db.get(i)[j]));//same as above
            System.out.println();
        }
        while (true) {//show the editor menu
            System.out.println("Editor menu:");
            System.out.println("1:Search\n2:Edit\n3:Create a new field\n4:Remove a field\n5:Make a new entry\n6:Remove an entry\n7:Commit Changes to Database\n8:Return to whatever sent you here\n9:Exit");
            System.out.println("Enter your choice:");
            int c = Integer.parseInt(br.readLine());
            switch (c) {
                case 1:
                    search();//search engine(IMDb.com-anyone?)
                    break;
                case 2://editor called for
                    edit();
                    break;
                case 3://add a new field
                    fieldsManage(true);
                    break;
                case 4://remove a field
                    fieldsManage(false);
                    break;
                case 5://new entry
                    if ((!auto_entry.equals("")))//check: has somebody not enabled such a useful option? Bad choice, whoever did this...
                        newData[db.getHeaderIndex(auto_entry)] = Codes.currentDate();//hey, I can do something myself too!
                    db.add(newData);
                    browse();
                    break;
                case 6://remove an existing entry
                    removeEntry();
                    break;
                case 7://finalizing the changes to the database called for
                    done();
                    break;
                case 8://return to the caller
                    return;
                case 9://exit
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default://give the inexperienced user another chance at using this menu
            }
        }
    }

    /**
     * the method which handles editing of the database
     */
    private void edit() throws IOException {
        System.out.println("Enter the index of the product whose data is to be edited.");
        index = Integer.parseInt(br.readLine());//input
        System.out.println("Entry Data for product at index " + index);
        for (int i = 0; i < db.getHeaders().length; i++)
            System.out.print(db.getHeaders()[i] + Codes.properSpacing(db.lengthOfLongestElement(), db.getHeaders()[i]));//beautiful output!
        System.out.println();//headers and data not on the same line
        for (int i = 0; i < db.get(index).length; i++)
            System.out.print(db.get(index)[i] + Codes.properSpacing(db.lengthOfLongestElement(), db.get(index)[i]));//pretty output!
        System.out.println("\nEnter the field whose data for product " + index + " you want to change:");
        String header = br.readLine();//input
        int i = db.getHeaderIndex(header);//the index of what to edit
        String[] change = db.get(index);//the entry to edit
        System.out.println("Enter the new value for the field " + header + " of product at index " + index + " which was previously " + db.get(index)[i] + ":");
        change[i] = br.readLine();//input
        if (!auto_entry.equals(""))
            change[db.getHeaderIndex(auto_entry)] = Codes.currentDate();//hey, I can do something myself too!
        db.set(index, change);
        db.changeNullElements();//no NullPointerExceptions,Please!
        browse();//what have I done?
    }

    /**
     * serializes the edited database
     */
    private void done() throws IOException {
        System.out.println("Changes commited to database.");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Codes.FILENAME));//serialize
        oos.writeObject(db);                                                                  //the
        oos.flush();                                                                          //database.
        oos.close();//don't need to keep this stream open any more
    }

    /**
     * the method which allows searching fields
     */
    private void search() throws IOException {
        System.out.println("Enter the field under which you want to search:");
        String header = br.readLine();//input
        System.out.println("Enter your query under field " + header + ":");
        String query = br.readLine();//input
        ArrayList<String> res = db.search(header, query);//do we need to do anything here at all?
        if (res.size() == 0)//no results
            System.out.println("No results. If this is undesirable, please check your input.");
        else {
            System.out.println("Search results:");
            for (int i = 0; i < res.size(); i++) {
                StringTokens st = new StringTokens(res.get(i), Codes.SEPARATOR);//split the search results into proper entry data
                ArrayList<String> subs = new ArrayList<>(db.columns());
                while (st.hasMoreTokens())
                    subs.add(st.nextToken());//get the individual data
                res.set(i, "");
                for (String sub : subs)
                    res.set(i, res.get(i) + sub + Codes.properSpacing(db.lengthOfLongestElement(), sub));//make the output look good
            }
            for (int i = 0; i < db.getHeaders().length; i++)
                System.out.print(db.getHeaders()[i] + Codes.properSpacing(db.lengthOfLongestElement(), db.getHeaders()[i]));//why leave out the headers?
            System.out.println();//can't put headers and data on the same line
            for (int i = 0; i < res.size(); i++) {//print out the results
                System.out.println("Result " + (i + 1) + ":");
                System.out.println(res.get(i));
            }
        }
    }

    /**
     * method which allows the removal of entries
     */
    private void removeEntry() throws IOException {
        System.out.println("Enter the index of the product whose information is to be removed from the database:");
        int idx = Integer.parseInt(br.readLine());//get the index of the product to be removed
        db.remove(idx);//nice database, isn't it?
        browse();//show off!!!
    }

    /**
     * method which allows the addition of new fields and deletion of existing fields.
     */
    private void fieldsManage(boolean add) throws IOException {
        if (add) {
            System.out.println("Enter the header for the new field:");
            String header = br.readLine();//get the new field's header
            ArrayList<String> newHeaders = new ArrayList<>((db.getHeaders().length + 1));
            for (int i = 0; i < (db.getHeaders().length + 1); i++) {//merge the old & new headers
                if (i < db.getHeaders().length)
                    newHeaders.add(db.getHeaders()[i]);
                else
                    newHeaders.add(header);
            }
            this.db.resize(db.rows(), newHeaders.size());//resize the database such that it can hold the new headers
            this.db.setHeaders(newHeaders.toArray(new String[newHeaders.size()]));//set the new headers
            browse();//show off again!!!
        } else {
            System.out.println("Enter the header of the field to remove:");
            String header = br.readLine();
            db.removeHeader(header);
            browse();//show off again!!!
        }
    }
}              
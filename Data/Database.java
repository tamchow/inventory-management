package Data;

import Miscellaneous.Codes;

import java.util.ArrayList;

/**
 * the class which implements a multi-field database
 */
@SuppressWarnings("unused")
public class Database implements java.io.Serializable {
    private final static long serialVersionUID = 1L;
    private String[][] data;//our data store
    private String name;
    private ArrayList<String> links;
    private int present, size;//addition index & size

    /**
     * sole constructor
     */
    public Database(int rows, int columns) {
        data = new String[rows + 1][columns];//column headers automatically included
        present = 1;
        size = 0;
        links = new ArrayList<>();
        name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getLinked() {
        return links;
    }

    public void setLinked(int idx, String linkName) {
        links.set(idx, linkName);
    }

    public void addLink(String linkName) {
        links.add(linkName);
    }

    public void clearLinked() {
        links.clear();
    }

    public void removeLink(String linkName) {
        links.remove(linkName);
    }

    /**
     * method to find the length of the longest element,which is required for output formatting.
     */
    public int lengthOfLongestElement() {
        int l = 0;
        for (String[] aData : data) {
            for (String anAData : aData) {
                if (anAData.length() >= l)
                    l = anAData.length();
            }
        }
        return l;
    }

    /**
     * method to retrieve the first row(field headers)
     */
    public String[] getHeaders() {
        return data[0];
    }

    /**
     * method to find the index of a header in the first row
     */
    public int getHeaderIndex(String header) {
        int i;
        for (i = 0; i < data[0].length; i++) {
            if (data[0][i].equalsIgnoreCase(header))
                break;//get header index
        }
        return i;
    }

    /**
     * method to retrieve the requested row
     */
    public String[] get(int index) {
        if (index < 0 || index > data.length) {
            System.out.println("Invalid index!");
            return null;
        } else if (index == 0)
            return getHeaders();
        else
            return data[index];
    }

    /**
     * method to set the requested row's data
     */
    public void set(int index, String[] toSet) {
        if (index < 0 || index > data.length)
            System.out.println("Invalid index!");
        else if (index == 0)
            setHeaders(toSet);
        else
            copy(toSet, data[index]);
    }

    /**
     * method to search the database for a query under a certain header
     */
    public ArrayList<String> search(String header, String query) {
        ArrayList<String> results = new ArrayList<>(rows() - 1);//not including field headers
        int i = getHeaderIndex(header);
        for (int j = 1; j < data.length; j++) {//search for query in given header index
            if (data[j][i].equalsIgnoreCase(query)) {
                String[] res = get(j);
                StringBuilder res2 = new StringBuilder();
                for (String re : res) res2.append(re).append(Codes.SEPARATOR);//concatenate the search results
                results.add(res2.toString());
            }
        }
        return results;
    }

    /**
     * method remove a field and its data from the database.
     */
    @SuppressWarnings("unchecked")
    public void removeHeader(String header) {
        ArrayList<String>[] columns = (ArrayList<String>[]) new ArrayList[data.length];
        for (int i = 0; i < columns.length; i++)
            columns[i] = new ArrayList<>(columns() - 1);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (j != getHeaderIndex(header)) {
                    columns[i].add(get(i)[j]);//other headers, add their data
                }
            }
        }
        resize(data.length, columns() - 1);//resize the database such that one column is excluded
        setHeaders(columns[0].toArray(new String[columns[0].size()]));//set the headers
        for (int i = 1; i < rows(); i++)
            set(i, columns[i].toArray(new String[columns[0].size()]));//set the data
    }

    /**
     * method to change any null elements in the database to Codes.EMPTY to avoid any NullPointerException(s)
     */
    public void changeNullElements() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == null)
                    data[i][j] = Codes.EMPTY;
            }
        }
    }

    /**
     * method to set the first row(field headers)
     */
    public void setHeaders(String[] headers) {
        copy(headers, data[0]);
        if (size() == 0)
            present = 1;
    }

    /**
     * method to retrieve the number of rows(field headers inclusive)
     */
    public int rows() {
        return data.length;
    }

    /**
     * method to set the first row(field headers)
     */
    public int columns() {
        return data[0].length;
    }

    /**
     * method to find the number of occupied rows(Codes.EMPTY containing rows are included but null rows are not)
     */
    public int size() {
        for (String[] aData : data) {
            if (!isNullRow(aData))
                size++;
        }
        return (size == rows() - 1) ? size - 1 : rows() - 1;//column headers not included
    }

    /**
     * method to resize the data store
     */
    public void resize(int rows, int columns) {
        String[][] tmp = new String[data.length][data[0].length];
        ddaCopy(data, tmp);
        data = new String[rows][columns];
        ddaCopy(tmp, data);
        changeNullElements();
    }

    /**
     * a method to clear the database
     */
    public void clear() {
        for (int i = 1; i < data.length; i++)
            remove(i);
    }

    /**
     * method to copy DDAs
     */
    private void ddaCopy(String[][] from, String[][] to) {
        for (int i = 0; i < from.length && i < to.length; i++) {
            for (int j = 0; j < from[i].length && j < to[i].length; j++)
                to[i][j] = from[i][j];
        }
    }

    /**
     * method to copy SDAs
     */
    private void copy(String[] from, String[] to) {
        for (int i = 0; i < from.length && i < to.length; i++)
            to[i] = from[i];
    }

    /**
     * method to remove an entry
     */
    public void remove(int index) {
        if (index < 0 || index > data.length)
            System.out.println("Invalid index!");
        else if (index == 0)
            System.out.println("Cannot Directly Remove Column Headers!");
        else {
            for (int i = 0; i < data[index].length; i++)
                data[index][i] = null;
        }
        removeNullRows();
        size();
    }

    /**
     * method to check if a row contains only null elements
     */
    private boolean isNullRow(String[] check) {
        int c = 0;
        for (String aCheck : check) {
            if (aCheck == null)
                c++;
        }
        return c == check.length;
    }

    /**
     * method to remove null element containing rows
     */
    private void removeNullRows() {
        int nullRows = 0;
        for (String[] aData : data) {
            if (isNullRow(aData))
                nullRows++;
        }
        String[][] tmp = new String[data.length - nullRows][data[0].length];
        int j = 0;
        for (int i = 0; i < data.length && j < tmp.length; i++) {
            if (!isNullRow(data[i])) {
                copy(data[i], tmp[j]);
                j++;
            }
        }
        resize(data.length - nullRows, data[0].length);
        ddaCopy(tmp, data);
        changeNullElements();
    }

    /**
     * method to add a new entry(row of data)
     */
    public void add(String[] toAdd) {
        present = size() + 1;
        resize(data.length + 1, data[0].length);
        copy(toAdd, data[present]);
        size();
    }
}
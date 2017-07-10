package Autonomic;

import Data.Database;
import Miscellaneous.Codes;
import Miscellaneous.DifferentialCalendar;
import Miscellaneous.StringTokens;
import UI.Notifications;
import UI.Search;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * the class which handles the autonomic parsing functions of the database
 */
public class Parser {
    private int index, total, enumeratedIndices;//stores various indices and numbers
    private Database db;//the database to parse
    private String[] optionList;//the array storing the options
    private String status;//the status field header
    private DifferentialCalendar current;//the current date
    private ArrayList<String> evaluated;//this contains the enumerated products

    /**
     * our constructor
     */
    public Parser(Database db, String[] optionList, String status) {
        if (db != null) {
            this.db = db;
            this.optionList = optionList;
            current = new DifferentialCalendar();
            evaluated = new ArrayList<>(db.rows() - 1);
            for (int i = 0; i < db.rows() - 1; i++) {
                evaluated.add("");//to avoid NullPointerException(s)
            }
            index = 1;//as we skip the field headers
            total = 0;
            enumeratedIndices = 0;
            this.status = status;
        } else
            System.out.println("No database to parse");
    }

    /**
     * the method controlling the parsing process
     */
    public void parse() throws java.io.IOException {
        outer:
        for (int i = 1; i < db.rows(); i++) {
            Notifications.showNotification("\fParsing entry " + i + " of " + (db.rows() - 1) + ", parsing " + (((((double) i) / ((double) (db.rows() - 1))) * 100)) + "% complete...");
            checkAsPerOptions(db.get(i));
            index = i;
            while (true) {
                System.out.println("Press <Enter> to continue...");
                char c = (char) System.in.read();
                if (c == '\f' || c == '\r' || c == '\n') {
                    continue outer;
                }
            }
        }
        Notifications.showNotification("\fThere are " + total + " items in the inventory from " + (db.rows() - 1) + " registered products.");
    }

    /**
     * method implementing the enumeration function
     */
    private void enumerate(String[] product, String name, String id, long used, long total, String state) throws java.io.IOException {
        if ((!evaluated.contains(name)) && state.equalsIgnoreCase(status)) {
            Notifications.showNotification(product, "There are " + (total - used) + " left of this product.", db, index);
            evaluated.set(enumeratedIndices, name + id);
            this.total += (total - used);
            this.enumeratedIndices++;
        }
    }

    /**
     * method implementing the expiry date checker function
     */
    private void checkExpiry(String[] product, int etod, DifferentialCalendar date) throws java.io.IOException {
        DifferentialCalendar b = current.clone();
        b.roll(Calendar.DAY_OF_MONTH, etod);
        if (date.before(b) && date.dateDifference(b) <= etod)
            Notifications.showNotification(product, "There are " + current.dateDifference(date) + " days left for expiry.\nPlease dispose or resupply.", db, index);
        else if (date.after(b))
            Notifications.showNotification(product, "The product will expire in " + current.dateDifference(date) + " days.\nPlease dispose or resupply.", db, index);
        else if (current.equals(date) || date.dateDifference(current) == 0)
            Notifications.showNotification(product, "The product has expired today.\nPlease dispose or resupply.", db, index);
        else if (current.after(date))
            Notifications.showNotification(product, "The product has expired " + current.dateDifference(date) + " days ago.\nPlease dispose or resupply.", db, index);
    }

    /**
     * method implementing the information update checker function
     */
    private void checkUpdates(String[] product, int updateDifference, DifferentialCalendar lastUpdate) throws java.io.IOException {
        if (current.after(lastUpdate) && current.dateDifference(lastUpdate) >= updateDifference)
            Notifications.showNotification(product, current.dateDifference(lastUpdate) + " days have passed since last update.\nPlease update product information.", db, index);
        else
            Notifications.showNotification(product, "No information updates for this product are required.", db, index);
    }

    /**
     * method implementing the projected usage calculation function
     */
    private void projectedUsage(String[] product, long used, long total, int etod, double overageFactor, DifferentialCalendar entryDate) throws java.io.IOException {
        double days = current.dateDifference(entryDate);
        if (days == 0 || etod == 0) {
            Notifications.showNotification(product, "Please wait one day before projected usage can be calculated.", db, index);
        } else if ((days / etod) <= etod) {
            long projectedUsage = (long) (((used / (days / etod)) * overageFactor));
            long required = Math.abs(projectedUsage - (total - used));
            Notifications.showNotification(product, "This product has a projected usage of " + projectedUsage + " units in the next supply period (" + etod + " days).\nTherefore " + required + " items of this product are required.", db, index);
        }
    }

    /**
     * method implementing the product statistics calculator function
     */
    private void productStats(String[] product, double rate, double costPrice, long sold) throws java.io.IOException {
        double salePrice = sold * rate;
        if (Math.round(salePrice) < Math.round(costPrice)) {
            double loss = costPrice - salePrice, lossPercentage = ((costPrice - salePrice) / costPrice) * 100;
            Notifications.showNotification(product, "This product has lost " + loss + ",or " + lossPercentage + "% on average since last information update.", db, index);
        } else if (Math.round(salePrice) == Math.round(costPrice)) {
            Notifications.showNotification(product, "This product has achieved breakeven on average since last information update.", db, index);
        } else if (Math.round(salePrice) > Math.round(costPrice)) {
            double profit = salePrice - costPrice, profitPercentage = ((salePrice - costPrice) / costPrice) * 100;
            Notifications.showNotification(product, "This product has gained " + profit + ",or " + profitPercentage + "% on average since last information update.", db, index);
        }
    }

    /**
     * method to check if a string has empty information
     */
    private boolean isEmpty(String[] data, int[] idx) {
        if (idx.length <= data.length) {
            for (int anIdx : idx) {
                if (data[anIdx].equals("") || data[anIdx].equals(Codes.EMPTY))
                    return true;
            }
        }
        return false;
    }

    /**
     * method which loads the data required to check an entry as per given options
     */
    private void checkAsPerOptions(String[] data) throws java.io.IOException {
        ArrayList<String> dat = new ArrayList<>(10);
        for (String anOptionList : optionList) {
            switch (anOptionList.substring(0, anOptionList.indexOf(':'))) {
                case "CHECK_EXPIRY_DATES":
                    dat.clear();
                    StringTokens st = new StringTokens(anOptionList.substring(anOptionList.indexOf(':') + 1, anOptionList.length()), ",");
                    while (st.hasMoreTokens())
                        dat.add(st.nextToken());
                    int[] d = {db.getHeaderIndex(dat.get(0)), db.getHeaderIndex(dat.get(1))};
                    if (!isEmpty(data, d))
                        checkExpiry(data, Integer.parseInt(data[d[0]]), Search.loadDate(data[d[1]]));
                    break;
                case "CHECK_UPDATES":
                    dat.clear();
                    StringTokens st2 = new StringTokens(anOptionList.substring(anOptionList.indexOf(':') + 1, anOptionList.length()), ",");
                    while (st2.hasMoreTokens())
                        dat.add(st2.nextToken());
                    int[] d2 = {db.getHeaderIndex(dat.get(0)), db.getHeaderIndex(dat.get(1))};
                    if (!isEmpty(data, d2))
                        checkUpdates(data, Integer.parseInt(data[d2[0]]), Search.loadDate(data[d2[1]]));
                    break;
                case "PROJECTED_USAGE":
                    dat.clear();
                    StringTokens st3 = new StringTokens(anOptionList.substring(anOptionList.indexOf(':') + 1, anOptionList.length()), ",");
                    while (st3.hasMoreTokens())
                        dat.add(st3.nextToken());
                    int[] d3 = {db.getHeaderIndex(dat.get(0)), db.getHeaderIndex(dat.get(1)), db.getHeaderIndex(dat.get(2)), db.getHeaderIndex(dat.get(3)), db.getHeaderIndex(dat.get(4))};
                    if (!isEmpty(data, d3))
                        projectedUsage(data, Long.parseLong(data[d3[0]]), Long.parseLong(data[d3[1]]), Integer.parseInt(data[d3[2]]), Double.parseDouble(data[d3[3]]), Search.loadDate(data[d3[4]]));
                    break;
                case "PRODUCT_STATISTICS":
                    dat.clear();
                    StringTokens st4 = new StringTokens(anOptionList.substring(anOptionList.indexOf(':') + 1, anOptionList.length()), ",");
                    while (st4.hasMoreTokens())
                        dat.add(st4.nextToken());
                    int[] d4 = {db.getHeaderIndex(dat.get(0)), db.getHeaderIndex(dat.get(1)), db.getHeaderIndex(dat.get(2))};
                    if (!isEmpty(data, d4))
                        productStats(data, Double.parseDouble(data[d4[0]]), Double.parseDouble(data[d4[1]]), Long.parseLong(data[d4[2]]));
                    break;
                case "ENUMERATE":
                    dat.clear();
                    StringTokens st5 = new StringTokens(anOptionList.substring(anOptionList.indexOf(':') + 1, anOptionList.length()), ",");
                    while (st5.hasMoreTokens())
                        dat.add(st5.nextToken());
                    int[] d5 = {db.getHeaderIndex(dat.get(0)), db.getHeaderIndex(dat.get(1)), db.getHeaderIndex(dat.get(2)), db.getHeaderIndex(dat.get(3)), db.getHeaderIndex(dat.get(4))};
                    if (!isEmpty(data, d5))
                        enumerate(data, data[d5[0]], data[d5[1]], Long.parseLong(data[d5[2]]), Long.parseLong(data[d5[3]]), data[d5[4]]);
                    break;
                default:
            }
        }
    }
}	
package UI;

import Data.Database;
import Miscellaneous.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("unused")
public class Search {
    private Database db;
    private Pairs<String, String> columnTypes;
    private ArrayList<String> res;

    public Search(Database db, Pairs<String, String> columnTypes) {
        this.db = db;
        this.columnTypes = columnTypes;
        res = new ArrayList<>(db.rows() - 1);
    }

    private boolean isDateColumn(String header) {
        return columnTypes.getValue(header).equals(Codes.DATE_COLUMN);
    }

    private boolean isTimeColumn(String header) {
        return columnTypes.getValue(header).equals(Codes.TIME_COLUMN);
    }

    private boolean isDateTimeColumn(String header) {
        return columnTypes.getValue(header).equals(Codes.DATE_TIME_COLUMN);
    }

    private boolean isTextColumn(String header) {
        return columnTypes.getValue(header).equals(Codes.TEXT_COLUMN);
    }

    private boolean isNumericColumn(String header) {
        return columnTypes.getValue(header).equals(Codes.NUMERIC_COLUMN);
    }

    public List<String> searchText(String header, String query) {
        res.clear();
        for (int i = 1; i < db.rows(); i++) {
            for (int j = 0; j < db.columns(); j++) {
                if (j == db.getHeaderIndex(header) && isTextColumn(db.getHeaders()[j])) {
                    if (db.get(i)[j].matches(query)) {
                        addResult(i);
                    }
                }
            }
        }
        return res;
    }

    public List<String> searchNumeric(String header, String query) {
        res.clear();
        for (int i = 1; i < db.rows(); i++) {
            for (int j = 0; j < db.columns(); j++) {
                if (j == db.getHeaderIndex(header) && isNumericColumn(db.getHeaders()[j])) {
                    if (evaluateNumeric(query, db.get(i)[j])) {
                        addResult(i);
                    }
                }
            }
        }
        return res;
    }

    private boolean evaluateNumeric(String a, String b) {
        StringTokens st = new StringTokens(a, " ");
        double comp = Double.parseDouble(b);
        boolean current = false;
        outer:
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            switch (s) {
                case "and":
                    current &= evalN(st.nextToken(), comp);
                    continue outer;
                case "or":
                    current |= evalN(st.nextToken(), comp);
            }
        }
        return current;
    }

    public List<String> searchDate(String header, String query) {
        res.clear();
        for (int i = 1; i < db.rows(); i++) {
            for (int j = 0; j < db.columns(); j++) {
                if (j == db.getHeaderIndex(header) && isDateColumn(db.getHeaders()[j])) {
                    if (evaluateDateTime(query, db.get(i)[j], "DATE")) {
                        addResult(i);
                    }
                }
            }
        }
        return res;
    }

    public List<String> searchDateTime(String header, String query) {
        res.clear();
        for (int i = 1; i < db.rows(); i++) {
            for (int j = 0; j < db.columns(); j++) {
                if (j == db.getHeaderIndex(header) && isDateTimeColumn(db.getHeaders()[j])) {
                    if (evaluateDateTime(query, db.get(i)[j], "DATETIME")) {
                        addResult(i);
                    }
                }
            }
        }
        return res;
    }

    public List<String> searchTime(String header, String query) {
        res.clear();
        for (int i = 1; i < db.rows(); i++) {
            for (int j = 0; j < db.columns(); j++) {
                if (j == db.getHeaderIndex(header) && isTimeColumn(db.getHeaders()[j])) {
                    if (evaluateDateTime(query, db.get(i)[j], "TIME")) {
                        addResult(i);
                    }
                }
            }
        }
        return res;
    }

    private boolean evaluateDateTime(String a, String b, String type) {
        StringTokens st = new StringTokens(a, " ");
        boolean current = false;
        outer:
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            switch (s) {
                case "and":
                    current &= evalDateTime(st.nextToken(), b, type);
                    continue outer;
                case "or":
                    current |= evalDateTime(st.nextToken(), b, type);
            }
        }
        return current;
    }

    private boolean evalN(String q, double d) {
        StringTokens st = new StringTokens(q, Codes.SEPARATOR);
        double comp = 0.0;
        boolean current = false;
        outer:
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            switch (s) {
                case "value":
                    comp = Double.parseDouble(st.nextToken());
                    continue outer;
                case ">":
                    current = (comp > d);
                    continue outer;
                case ">=":
                    current = (comp >= d);
                    continue outer;
                case "<":
                    current = (comp < d);
                    continue outer;
                case "<=":
                    current = (comp < d);
                    continue outer;
                case "=":
                    current = (comp == d);
            }
        }
        return current;
    }

    private boolean evalDateTime(String q, String d, String type) {
        StringTokens st = new StringTokens(q, Codes.SEPARATOR);
        DifferentialCalendar a, comp;
        boolean current = false;
        switch (type) {
            case "DATE":
                a = loadDate(d);
                comp = new DifferentialCalendar();
                outer:
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    switch (s) {
                        case "date":
                            comp = loadDate(st.nextToken());
                            continue outer;
                        case "after":
                            current = a.after(comp);
                            continue outer;
                        case "equals":
                            current = a.equalsDate(comp);
                            continue outer;
                        case "before":
                            current = a.before(comp);
                    }
                }
                break;
            case "TIME":
                a = loadTime(d);
                comp = new DifferentialCalendar();
                outer:
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    switch (s) {
                        case "time":
                            comp = loadTime(st.nextToken());
                            continue outer;
                        case "after":
                            current = a.after(comp);
                            continue outer;
                        case "equals":
                            current = a.equalsTime(comp);
                            continue outer;
                        case "before":
                            current = a.before(comp);
                    }
                }
                break;
            case "DATETIME":
                a = loadDateTime(d);
                comp = new DifferentialCalendar();
                outer:
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    switch (s) {
                        case "datetime":
                            comp = loadDateTime(st.nextToken());
                            continue outer;
                        case "after":
                            current = a.after(comp);
                            continue outer;
                        case "equals":
                            current = a.equalsDateTime(comp);
                            continue outer;
                        case "before":
                            current = a.before(comp);
                    }
                }
        }
        return current;
    }

    private void addResult(int idx) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < db.get(idx).length; i++) {
            s.append(db.get(idx)[i]).append("~");
        }
        res.add(s.toString());
    }

    @SuppressWarnings("WeakerAccess")
    public static DifferentialCalendar loadDate(String date) {
        StringTokens s = new StringTokens(date, "/.");
        ArrayList<String> dates = new ArrayList<>();
        DifferentialCalendar c = new DifferentialCalendar();
        int day = 0, month = 0, year = 0;
        while (s.hasMoreTokens())
            dates.add(s.nextToken());
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0)
                day = Integer.parseInt(dates.get(i));
            else if (i == 1)
                month = Integer.parseInt(dates.get(i));
            else if (i == 2)
                year = Integer.parseInt(dates.get(i));
        }
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c;
    }

    @SuppressWarnings("WeakerAccess")
    public static DifferentialCalendar loadTime(String time) {
        StringTokens s = new StringTokens(time, "/.");
        ArrayList<String> dates = new ArrayList<>();
        DifferentialCalendar c = new DifferentialCalendar();
        int hour = 0, minute = 0, second = 0;
        while (s.hasMoreTokens())
            dates.add(s.nextToken());
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0)
                hour = Integer.parseInt(dates.get(i));
            else if (i == 1)
                minute = Integer.parseInt(dates.get(i));
            else if (i == 2)
                second = Integer.parseInt(dates.get(i));
        }
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c;
    }

    private DifferentialCalendar loadDateTime(String dateTime) {
        StringTokens s = new StringTokens(dateTime, "/. :");
        ArrayList<String> dates = new ArrayList<>();
        DifferentialCalendar c = new DifferentialCalendar();
        int day = 0, month = 0, year = 0, hour = 0, minute = 0, second = 0;
        while (s.hasMoreTokens())
            dates.add(s.nextToken());
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0)
                day = Integer.parseInt(dates.get(i));
            else if (i == 1)
                month = Integer.parseInt(dates.get(i));
            else if (i == 2)
                year = Integer.parseInt(dates.get(i));
            else if (i == 3)
                hour = Integer.parseInt(dates.get(i));
            else if (i == 4)
                minute = Integer.parseInt(dates.get(i));
            else if (i == 5)
                second = Integer.parseInt(dates.get(i));
        }
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c;
    }
}
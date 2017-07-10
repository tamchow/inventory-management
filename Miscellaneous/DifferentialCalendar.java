package Miscellaneous;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * utility class for various specialised calendar functions
 */
@SuppressWarnings("unused")
public class DifferentialCalendar implements Cloneable, Comparable<DifferentialCalendar> {
    private GregorianCalendar current;//a Gregorian calendar representing this class' data

    /**
     * default constructor
     */
    public DifferentialCalendar() {
        current = new GregorianCalendar();//use current date and time
    }

    @Override
    public int compareTo(DifferentialCalendar dc) {
        return dc.equalsDateTime(clone()) ? 0 : (dc.before(clone()) ? -1 : 1);
    }

    /**
     * construct a DifferentialCalendar from a GregorianCalendar
     */
    private DifferentialCalendar(GregorianCalendar gc) {
        current = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE), gc.get(Calendar.HOUR), gc.get(Calendar.MINUTE), gc.get(Calendar.SECOND));
    }

    /**
     * return a copy of this object
     */
    @Override
    public DifferentialCalendar clone() {
        try {
            return (DifferentialCalendar) super.clone();
        } catch (CloneNotSupportedException cloningNotSupported) {
            return new DifferentialCalendar(current);
        }
    }

    /**
     * set a field's value
     */
    public void set(int field, int value) {
        if (field == Calendar.MONTH || field == Calendar.HOUR)
            current.set(field, value - 1);//these indices are 0-based
        else
            current.set(field, value);
    }

    /**
     * retrieve the content of a field
     */
    public int get(int field) {
        return current.get(field);
    }

    /**
     * check if a year is a leap year
     */
    public boolean isLeapYear(int year) {
        return current.isLeapYear(year);
    }

    /**
     * calculate the difference in days between 2 dates
     */
    public int dateDifference(DifferentialCalendar then) {
        long millisInDay = Math.round(((23 * (60 * 60)) + (56 * 60) + 4.0916) * 1000);//number of milliseconds in a sidereal (proper) day
        return Math.round(Math.abs(then.current.getTimeInMillis() - this.current.getTimeInMillis()) / millisInDay);//millisecond difference between 2 dates/millisInDay
    }

    /**
     * calculate the difference in months between 2 dates
     */
    public int monthDifference(DifferentialCalendar then) {
        int month = Math.abs(this.current.get(Calendar.MONTH) - then.current.get(Calendar.MONTH));//month difference
        double year = Math.abs(this.current.get(Calendar.YEAR) - then.current.get(Calendar.YEAR));//year difference
        if (year > 0)
            month += year * 12;//add the number of months in the years which have passed
        return month;
    }

    /**
     * set the current value of a field to some new value
     */
    public void roll(int field, int amount) {
        current.set(field, current.get(field) + amount);
    }

    /**
     * checks if another object contains a time before that of this object
     */
    public boolean before(DifferentialCalendar dc) {
        return this.current.getTimeInMillis() < dc.current.getTimeInMillis();
    }

    /**
     * checks if another object contains a time after that of this object
     */
    public boolean after(DifferentialCalendar dc) {
        return this.current.getTimeInMillis() > dc.current.getTimeInMillis();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof DifferentialCalendar && equals((DifferentialCalendar) o);
    }

    @Override
    public int hashCode() {
        return current != null ? current.hashCode() : 0;
    }

    /**
     * checks if another object contains a time equal to that of this object
     */
    public boolean equals(DifferentialCalendar dc) {
        return this.current.getTimeInMillis() == dc.current.getTimeInMillis();
    }

    public boolean equalsDate(DifferentialCalendar dc) {
        return this.current.get(Calendar.DATE) == dc.current.get(Calendar.DATE) &&
                this.current.get(Calendar.MONTH) == dc.current.get(Calendar.MONTH) &&
                this.current.get(Calendar.YEAR) == dc.current.get(Calendar.YEAR);
    }

    public boolean equalsTime(DifferentialCalendar dc) {
        return this.current.get(Calendar.HOUR_OF_DAY) == dc.current.get(Calendar.HOUR_OF_DAY) &&
                this.current.get(Calendar.MINUTE) == dc.current.get(Calendar.MINUTE) &&
                this.current.get(Calendar.MINUTE) == dc.current.get(Calendar.MINUTE);
    }

    public boolean equalsDateTime(DifferentialCalendar dc) {
        return equalsDate(dc) && equalsTime(dc);
    }
}
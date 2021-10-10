package it.developing.ico2k2.luckyplayer.database.date;

import java.util.Calendar;

import it.developing.ico2k2.luckyplayer.database.Optimized;

public class AbsoluteDate1970To2225 extends Date
{
    public static final short REFERENCE_YEAR = 1970;
    public final byte year;

    public AbsoluteDate1970To2225(byte day, byte month,short yearFrom1970To2225,byte hour, byte minute, byte second) {
        super(day,month,hour,minute,second);
        this.year = actualYearToMemYear(yearFrom1970To2225);
    }

    public AbsoluteDate1970To2225(byte day, byte month,short yearFrom1970To2225,byte hour, byte minute) {
        this(day, month, yearFrom1970To2225, hour, minute, (byte) 0);
    }

    public AbsoluteDate1970To2225(Calendar calendar)
    {
        super(calendar);
        this.year = actualYearToMemYear((short)calendar.get(Calendar.YEAR));
    }

    public AbsoluteDate1970To2225(java.util.Date date) {
        this(dateToCalendar(date));
    }

    public AbsoluteDate1970To2225() {
        this(now());
    }

    //This system makes it possible to save a year from 1970 to 2225 inside a byte value
    public static byte actualYearToMemYear(short actualYear)
    {
        return Optimized.byte256((short)(actualYear - REFERENCE_YEAR));
    }

    public static short memYearToActualYear(byte memYear)
    {
        return (short)(Optimized.shortFromByte256(memYear) + REFERENCE_YEAR);
    }

    @Override
    public short getYear() {
        return memYearToActualYear(year);
    }

    public byte getMemYear() {
        return year;
    }
}

package it.developing.ico2k2.luckyplayer.database.date;

import android.content.Context;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import it.developing.ico2k2.luckyplayer.R;

public class Date extends GeneralDate
{
    public static final short REFERENCE_YEAR = 1970;

    private final byte day;
    private final byte month;
    private final byte year;
    private final byte hour;
    private final byte minute;
    private final byte second;

    public Date(byte day, byte month, short year, byte hour, byte minute, byte second) {
        this.day = day;
        this.month = month;
        this.year = actualYearToMemYear(year);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Date(byte day, byte month, short year, byte hour, byte minute) {
        this(day, month, year, hour, minute, (byte) 0);
    }

    public Date(byte day, byte month, byte year, byte hour, byte minute, byte second) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Date(byte day, byte month, byte year, byte hour, byte minute) {
        this(day, month, year, hour, minute, (byte) 0);
    }

    public Date(Calendar calendar) {
        this.day = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        this.month = (byte) calendar.get(Calendar.MONTH);
        this.year = actualYearToMemYear((short)calendar.get(Calendar.YEAR));
        this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = (byte) calendar.get(Calendar.MINUTE);
        this.second = (byte) calendar.get(Calendar.SECOND);
    }

    public Date(java.util.Date date) {
        this(dateToCalendar(date));
    }

    public Date() {
        this(now());
    }

    public static byte actualYearToMemYear(short actualYear)
    {
        return (byte) (actualYear - REFERENCE_YEAR + Byte.MIN_VALUE);
    }

    public static short memYearToActualYear(byte memYear)
    {
        return (short) (memYear + REFERENCE_YEAR - Byte.MIN_VALUE);
    }

    public byte getDay() {
        return day;
    }

    public byte getMonth() {
        return month;
    }

    public short getYear() {
        return memYearToActualYear(year);
    }

    public byte getHour() {
        return hour;
    }

    public byte getMinute() {
        return minute;
    }

    public byte getSecond() {
        return second;
    }

    @Override
    public @NotNull
    String toString() {
        return getDay() + "/" + (getMonth() + 1) + "/" + getYear() + " " + getHour() + ":" + getMinute();
    }

    @Override
    public Date getAbsoluteDate() {
        return this;
    }

    @Override
    public RelativeDate getRelativeDate(Date date)
    {
        long ms = getMsFrom1970() - date.getMsFrom1970();
        RelativeDate.Type type;
        ms /= 1000L;
        if(ms > 60L)
        {
            ms /= 60L;
            if(ms > 60L)
            {
                ms /= 60L;
                if(ms > 24L)
                {
                    ms /= 24L;
                    if(ms > 7L)
                    {
                        ms /= 7L;
                        type = RelativeDate.Type.WEEK;
                    }
                    else
                        type = RelativeDate.Type.DAY;
                }
                else
                    type = RelativeDate.Type.HOUR;
            }
            else
                type = RelativeDate.Type.MINUTE;
        }
        else
            type = RelativeDate.Type.SECOND;
        return new RelativeDate(date,(short)ms,type);
    }

    @Override
    public Calendar getCalendar() {
        Calendar c = now();
        c.set(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
        return c;
    }

    @Override
    public java.util.Date getDate() {
        return getCalendar().getTime();
    }

    @Override
    @Nullable
    public String getString(Context context) {
        return context.getString(R.string.post_date, getDay(), getMonth(), getYear(), getHour(), getMinute());
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o != null)
        {
            if(o instanceof Date)
            {
                result = getMsFrom1970() == ((Date)o).getMsFrom1970();
            }
        }
        return result;
    }
}
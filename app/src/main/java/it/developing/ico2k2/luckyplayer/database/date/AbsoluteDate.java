package it.developing.ico2k2.luckyplayer.database.date;

import java.util.Calendar;

public class AbsoluteDate extends Date
{
    public final short year;

    public AbsoluteDate(byte day, byte month,short year,byte hour, byte minute, byte second) {
        super(day,month,hour,minute,second);
        this.year = year;
    }

    public AbsoluteDate(byte day, byte month,short year,byte hour, byte minute) {
        this(day, month, year, hour, minute, (byte) 0);
    }

    public AbsoluteDate(Calendar calendar)
    {
        super(calendar);
        this.year = (short)calendar.get(Calendar.YEAR);
    }

    public AbsoluteDate(java.util.Date date) {
        this(dateToCalendar(date));
    }

    public AbsoluteDate() {
        this(now());
    }

    @Override
    public short getYear() {
        return year;
    }
}

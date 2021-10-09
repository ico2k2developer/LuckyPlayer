package it.developing.ico2k2.luckyplayer.database.date;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.Calendar;

public abstract class GeneralDate {

    public static Calendar now() {
        return Calendar.getInstance();
    }

    public static Calendar dateToCalendar(java.util.Date date) {
        Calendar c = now();
        c.setTime(date);
        return c;
    }

    public ComparisonResult compare(GeneralDate date) {
        int result = getCalendar().compareTo(date.getCalendar());
        return result == 0 ? ComparisonResult.SAME : result > 0
                ? ComparisonResult.AFTER : ComparisonResult.BEFORE;
    }

    public Calendar getCalendar()
    {
        Calendar c = now();
        c.setTimeInMillis(getMsFrom1970());
        return c;
    }

    public abstract java.util.Date getDate();

    public abstract Date getAbsoluteDate();

    public abstract RelativeDate getRelativeDate(Date date);

    public RelativeDate getRelativeDateFromNow()
    {
        return getRelativeDate(new Date());
    }

    public long getMsFrom1970() {
        return getDate().getTime();
    }

    @Nullable
    public abstract String getString(Context context);

    public enum ComparisonResult {
        BEFORE,
        AFTER,
        SAME,
    }

}
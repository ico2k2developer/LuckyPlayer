package it.developing.ico2k2.luckyplayer.database.date;

import android.content.Context;

import androidx.annotation.Nullable;

import it.developing.ico2k2.luckyplayer.R;

public class RelativeDate extends GeneralDate
{
    private final Date reference;
    private final short count;
    private final Type type;

    public RelativeDate(Date reference, short count, Type type, boolean future) {
        this.reference = reference;
        if(count > 0)
            this.count = future ? count : (short) (count * -1);
        else
            this.count = future ? (short) (count * -1) : count;
        this.type = type;
    }

    public RelativeDate(Date reference, short count, Type type) {
        this(reference,count,type,false);
    }

    public RelativeDate(short count, Type type,boolean future) {
        this(new AbsoluteDate(), count, type,future);
    }

    public RelativeDate(short count, Type type) {
        this(new AbsoluteDate(), count, type);
    }

    public boolean isFuture()
    {
        return count > 0;
    }

    public boolean isPast()
    {
        return count < 0;
    }

    public Date getAbsoluteDate()
    {
        return new AbsoluteDate(getCalendar());
    }

    @Override
    public RelativeDate getRelativeDate(Date date)
    {
        return getAbsoluteDate().getRelativeDate(date);
    }

    @Override
    public long getMsFrom1970()
    {
        long ms = count;
        switch (type)
        {
            case WEEK:
            {
                ms *= 7L;
            }
            case DAY:
            {
                ms *= 24L;
            }
            case HOUR:
            {
                ms *= 60L;
            }
            case MINUTE:
            {
                ms *= 60L;
            }
            case SECOND:
            {
                ms *= 1000L;
                break;
            }
        }
        return reference.getMsFrom1970() + ms;
    }

    @Override
    public java.util.Date getDate()
    {
        return new java.util.Date(getMsFrom1970());
    }

    @Override
    @Nullable
    public String getString(Context context)
    {
        String result = null;
        int res;
        if(isFuture())
            res = R.string.post_date_relative_future;
        else if(isPast())
            res = R.string.post_date_relative_past;
        else
            res = 0;
        short c = count;
        if(c < 0)
            c *= -1;
        if(res != 0)
        {
            switch(type)
            {
                case WEEK:
                {
                    result = context.getString(res,c,
                            context.getString(c > 1 ? R.string.weeks : R.string.week));
                    break;
                }
                case DAY:
                {
                    result = context.getString(res,c,
                            context.getString(c > 1 ? R.string.days : R.string.day));
                    break;
                }
                case HOUR:
                {
                    result = context.getString(res,c,
                            context.getString(c > 1 ? R.string.hours : R.string.hour));
                    break;
                }
                case MINUTE:
                {
                    result = context.getString(res,c,
                            context.getString(c > 1 ? R.string.minutes : R.string.minute));
                    break;
                }
                case SECOND:
                {
                    result = context.getString(res,c,
                            context.getString(c > 1 ? R.string.seconds : R.string.second));
                    break;
                }
            }
        }
        return result;
    }

    public enum Type {
        WEEK,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o != null)
        {
            if(o instanceof RelativeDate)
            {
                result = getMsFrom1970() == ((RelativeDate)o).getMsFrom1970();
            }
        }
        return result;
    }
}
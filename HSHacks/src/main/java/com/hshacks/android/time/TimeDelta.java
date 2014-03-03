package com.hshacks.android.time;

import android.content.Context;

import com.hshacks.android.R;

import java.util.Date;

/**
 * Created by damian on 1/12/14.
 */
public class TimeDelta {
    public final long days, hours, minutes, seconds;

    public TimeDelta(Date one, Date two) {
        long timeAgo = one.getTime() - two.getTime();
        days = timeAgo/86400000;
        timeAgo %= 86400000;
        hours = timeAgo/3600000;
        timeAgo %= 3600000;
        minutes = timeAgo/60000;
        timeAgo %= 60000;
        seconds = timeAgo/1000;
    }

    public static String formatTimeAgo(Context context, Date date) {
        long timeAgo = (new Date()).getTime() - date.getTime();
        StringBuilder builder = new StringBuilder();
        long timeUnits = 0;

        if(timeAgo < 60000) {
            //Seconds
            timeUnits = (timeAgo / 1000);
            builder.append(timeUnits).append(context.getString(R.string.second));
        } else if(timeAgo < 3600000) {
            //Minutes
            timeUnits = (timeAgo / 60000);
            builder.append(timeUnits).append(context.getString(R.string.minute));
        } else if(timeAgo < 86400000) {
            //Hours
            timeUnits = (timeAgo / 3600000);
            builder.append(timeUnits).append(context.getString(R.string.hour));
        } else {
            //Days
            timeUnits = (timeAgo / 86400000);
            builder.append(timeUnits).append(context.getString(R.string.day));
        }

        if(timeUnits != 1) {
            builder.append(context.getString(R.string.s_for_plural));
        }
        builder.append(context.getString(R.string.ago));

        return builder.toString();
    }

    public static TimeDelta timeFromNowUntil(Date date) {
        return new TimeDelta(date, new Date());
    }

    public static double progressBetween(Date start, Date end) {
        Date current = new Date();
        return (double)(current.getTime() - start.getTime())/(double)(end.getTime() - start.getTime());
    }

    public static Date offsetDateFromUTCToEST(Date date) {
        long dateTime = date.getTime();
        dateTime += 5 * 60 * 60 * 1000;
        Date offsetDate = new Date();
        offsetDate.setTime(dateTime);
        return offsetDate;
    }
}

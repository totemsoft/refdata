package au.net.apollosoft.refdata.commons.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
public abstract class DateUtils {

    /**
     * Return the start of the day for the specified date.
     *
     * @param date a date
     * @return start of the day of the day for the specified date
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date parse(String str, DateFormat df) {
        return parse(str, df, true);
    }

    public static Date parse(String str, DateFormat df, boolean throwError) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            return df.parse(str);
        } catch (ParseException e) {
            if (throwError) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return null;
        }
    }

}
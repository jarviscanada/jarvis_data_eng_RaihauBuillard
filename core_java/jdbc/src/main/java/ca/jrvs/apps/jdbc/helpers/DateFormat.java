package ca.jrvs.apps.jdbc.helpers;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateFormat {

    public static String formatDate(Date date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        return sdfDate.format(date);
    }

    public static String formatTimestamp(Timestamp timestamp){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
        Date date = new Date(timestamp.getTime());
        return sdfDate.format(date);
    }
}

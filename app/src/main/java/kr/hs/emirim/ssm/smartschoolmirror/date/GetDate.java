package kr.hs.emirim.ssm.smartschoolmirror.date;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by doori on 2017-10-17.
 */

public class GetDate {
    private static int year;
    private static int month;
    private static int date;
    private static int hour;
    private static int minute;



    public static int getYear() {
        String format_insert_year = new String("YYYY");
        SimpleDateFormat idf = new SimpleDateFormat(format_insert_year, Locale.KOREA);
        year= Integer.parseInt(idf.format(new Date()));
        return year;
    }

    public static int getMonth() {
        String format_insert_month = new String("MM");
        SimpleDateFormat idf = new SimpleDateFormat(format_insert_month, Locale.KOREA);
        month= Integer.parseInt(idf.format(new Date()));
        return month;
    }


    public static int getDate() {
        String format_insert_date = new String("dd");
        SimpleDateFormat idf = new SimpleDateFormat(format_insert_date, Locale.KOREA);
        date= Integer.parseInt(idf.format(new Date()));
        return date;
    }
    public static int getHour() {
        String format_insert_hour = new String("HH");
        SimpleDateFormat idf = new SimpleDateFormat(format_insert_hour, Locale.KOREA);
        Log.e("시간",idf.format(new Date()));
        hour= Integer.parseInt(idf.format(new Date()));
        Log.e("시간", String.valueOf(hour));

        return hour;
    }
    public static int getMinute() {
        String format_insert_hour = new String("mm");
        SimpleDateFormat idf = new SimpleDateFormat(format_insert_hour, Locale.KOREA);
        minute= Integer.parseInt(idf.format(new Date()));
        return minute;
    }

    public static void updateDate(TextView date_text, TextView time_text) {
        String format_time = new String("HH : mm");
        String format_date = new String("MM월  dd일  E요일");

        SimpleDateFormat tf = new SimpleDateFormat(format_time, Locale.KOREA);
        date_text.setText(tf.format(new Date()));

        SimpleDateFormat df = new SimpleDateFormat(format_date, Locale.KOREA);
        time_text.setText(df.format(new Date()));



    }
}

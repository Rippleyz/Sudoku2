package sudoku.cobyapps.com.sudoku.Utilities;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDatePicker {
    public static String getCurrentDate(Context context) {
        Locale current = context.getResources().getConfiguration().locale;
        Date date = Calendar.getInstance().getTime();
        if (current.getCountry().equals(Locale.US.toString())) {
            DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy, hh:mm:ss");
            return dateFormat.format(date);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy, hh:mm:ss");
            return dateFormat.format(date);
        }
    }
}

package com.lyriad.e_commerce.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public final class Parser {

    public Parser() {

    }

    public static String formatCalendar (Calendar calendar) {

        String date;

        date = String.format(Constants.LOCALE,
                "%s %d, %d",
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Constants.LOCALE),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR));

        return date;
    }

    public static byte[] getBytesFromBitmap (Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmapFromBytes(byte[] image) {

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}

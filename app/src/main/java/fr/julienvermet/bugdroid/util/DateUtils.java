/*
* Copyright (C) 2013 Julien Vermet
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fr.julienvermet.bugdroid.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String ATOM_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static SimpleDateFormat sAtomDateFormat = new SimpleDateFormat(ATOM_PATTERN, Locale.ENGLISH);
    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    public static Calendar getCalendarFromAtomDate(String atomDate) {
        try {
            Date date = sAtomDateFormat.parse(atomDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAtomDate(String pubDate) {
        Calendar cal = getCalendarFromAtomDate(pubDate);
        return convertCalendarToString(cal);
    }

    public static String getAtomDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return convertCalendarToString(cal);
    }

    private static String convertCalendarToString(Calendar cal) {
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        String timeWithZeros = String.format("%02dh%02d", hours, minutes);

        long timeInMillis = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        String sDate = "";
        if ((now - timeInMillis) <= DAY_IN_MILLIS) {
            sDate = "Today";
        } else if (((now - timeInMillis) <= DAY_IN_MILLIS * 2)) {
            sDate = "Yesterday";
        } else {
            Date date = new Date(timeInMillis);
            sDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
        }
        return sDate + " at " + timeWithZeros;
    }
}
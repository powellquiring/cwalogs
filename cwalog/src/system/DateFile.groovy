package system

import java.util.regex.Matcher;

import cwalog.Constants;

class DateFile {
    // cwa-20120404.log
    static Date fileToDate(File file) {
        Matcher parts = (file.name =~ /\D*(\d{8})\D*/)
        if ((parts.size() != 1) || parts[0].size() != 2) return null
        String numbers =  parts[0][1]
        Date date = Date.parse("yyyyMMdd", numbers)
        return date
    }

    static boolean tuesday(File file) {
        Date date = fileToDate(file)
        if (date == null) return false
        if (date.day == 2) return true
        return false
    }


    static boolean tuesday2014(File file) {
        Date date = fileToDate(file)
        if (date == null) return false
        if (date.year != (2014 - 1900)) return false
        if (date.month <= 3) return false
        if (date.month >= 6) return false
        if (date.day == 2) {
             return true
        }
        return false
    }

    static Closure rangeIncluding(String first, String last) {
        return {File file -> ((file.name >= first) && (file.name <= last))}
    }
    static Closure rangeAfterIncluding(String first) {
        return {File file -> ((file.name >= first) || (file.name == Constants.todaysLogFile.name))}
    }
}

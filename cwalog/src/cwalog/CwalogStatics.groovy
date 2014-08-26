package cwalog;

import java.io.File;
import cwalog.Constants;

public class CwalogStatics {
    // the cwa.log file is growing all the time, so it's contents are changing
    static boolean dynamicFile(File fileName) {
        return (fileName.name == Constants.todaysLogFileName)
    }

}

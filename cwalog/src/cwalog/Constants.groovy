package cwalog

class Constants {
    static final URL logUrl = new URL('http://rchas412-nat.rchland.ibm.com:82/logs/')
    static final URL eccLogUrl = new URL('http://rchas412-nat.rchland.ibm.com:82/ecclogs/')

    // raw log files in the /logs url
    static final String validLogName = /(^cwa-\d*\.log)|(cwa.log)/
    static final File logParentDirectory = new File(/C:\powell\groovy\cwalogs\programlogs/)
    static final File logDir = new File(/E:\powell.groovy\groovy\cwalogs\logs/)
//    static final File logDir = new File(/C:\powell\groovy\cwalogs\logs/)
    static final String todaysLogFileName = 'cwa.log'
    static final File todaysLogFile = new File(logDir, todaysLogFileName)
    static final File logSkimDir = new File(/C:\powell\groovy\cwalogs\logs_skim/)
    static final File dailyReportDir = new File(/C:\powell\groovy\cwalogs\dailyreport/)
    
    // activity logs in the /logs/activity url
    static final URL activityUrl = new URL('http://rchas412-nat.rchland.ibm.com:82/logs/activity')
    static final String validActivityName = /(^activity-\d*\.txt)|(activity.txt)/
    static final File activityDir = new File(/C:\powell\groovy\cwalogs\activity/)
    static final String todaysActivityFileName = 'activity.txt'
    static final File todaysActivityFile = new File(logDir, todaysLogFileName)

}

package cwalog

import javax.management.InstanceOfQueryExp;

import cwalog.Constants;
import system.DateFile;
import system.FileStatic;
import system.UriHelper;


class Fetch {

    boolean overwriteLogHtmlFile = true    // default true, refetch the log html file
    boolean overwriteTodaysLogFile = true   // default true, refetch today's log file

    public static final Fetch instance = new Fetch()
    private Fetch(){} // use the instance
    
    

    private List<String>allFilesList
    public main() {
        fetch{File file -> true}
        
    }
    // return the list of all files that are available in the log directory
    public List<File>getAllFiles() {
        if (!allFilesList) {
            allFilesList = []
            UriHelper.getSingleton().initAuthenticator();
            File logHtml = new File(Constants.logDir, /logs.html/)
            if (overwriteLogHtmlFile || !logHtml.exists()) {
                println "+${logHtml.toString()}"
                logHtml.newOutputStream() << Constants.logUrl.openStream()
            }
            def parser = new org.cyberneko.html.parsers.SAXParser()
            Node root =   new XmlParser( parser ).parse( logHtml )
            root.BODY.PRE.A.each{
                String logName = it.'@href'
                if(logName ==~ Constants.validLogName) {
                    allFilesList << new File(logName)
                }
            }
        }
        return allFilesList 
    }
    
    public void fetch(Closure filter) {
        // fetch the log files.
        if (overwriteTodaysLogFile && Constants.todaysLogFile.exists()) {
            Constants.todaysLogFile.delete()
        }
        allFiles.each{File file ->
            if (filter(file)) {
                fetchFile(file)
            }
        }
    }
    
    public void fetchFile(File fileName) {
        URL url = new URL(Constants.logUrl, fileName.name)
        File logFile = new File(Constants.logDir, fileName.name)
        if (CwalogStatics.dynamicFile(logFile)) logFile.delete()
        if (logFile.exists()) {
            println "fetch - $fileName $logFile <- $url"
        } else {
            println "fetch + $fileName $logFile <- $url"
            FileStatic.copyUrlToFile(new URL(Constants.logUrl.toString()  + "/" + logFile.name), logFile)
        }
    }
    
	public static main(String[] args) {
		UriHelper.getSingleton().initAuthenticator();
//		instance.fetch{File file -> return DateFile.tuesday(file)}
        instance.fetch(DateFile.&tuesday)
	}
}
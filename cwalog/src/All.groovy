import system.DateFile;
import system.TeeOutputStream
import system.StandardOutToFile
import cwalog.Constants
import cwalog.CwalogStatics;

StandardOutToFile out =  StandardOutToFile.create("log", ".txt", Constants.logParentDirectory)
//Closure interestingFiles = DateFile.&tuesday2014
//Closure interestingFiles = 
// Closure interestingFiles = DateFile.rangeAfterIncluding("cwa-20140820.log")

// process(DateFile.rangeIncluding("cwa-20140527.log", "cwa-20140603.log")) // caching started on 6/1 2014
// process(DateFile.&tuesday2014)
// process{File file -> file.name == "cwa-20140610.log"}
 process(DateFile.rangeAfterIncluding("cwa-20140819.log")) // start with 8/19
out.closeAndGvim();

void process(Closure interestingFiles) {
    cwalog.Fetch.instance.allFiles.findAll(interestingFiles).each{File file ->
        File reportFile = new File(Constants.dailyReportDir, file.name)
        if (CwalogStatics.dynamicFile(reportFile)) reportFile.delete()
        if (reportFile.exists()) {
            println "report - ${reportFile.name} ${reportFile}"
        } else {
            println "report + ${reportFile.name} ${reportFile}"
            StandardOutToFile outForADay =  StandardOutToFile.createWithTemp(reportFile)
            cwalog.PrintGroups.instance.printLog(file, cwalog.Skim.instance, cwalog.Fetch.instance)
            outForADay.close()
        }
    }
}

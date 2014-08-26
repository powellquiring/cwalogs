package cwalog

import java.util.regex.Pattern;

import system.FileStatic;
import cwalog.Constants;
import cwalog.Line;
import groovy.io.FileType;
import groovy.lang.Closure;


class Skim {
    static public final Skim instance = new Skim()
    private Skim(){

    }
    // skim the log files that match the filter.  This assumes that the files have been Fetched previously
    public void main(Closure filterFile) {
        Constants.logSkimDir.mkdirs()
        
        // skim the log files removing the lines that are not parsed
        File logs = Constants.logDir
        TreeSet files = []
        logs.eachFileMatch(FileType.FILES, ~Constants.validLogName) {File file -> files << file}

        files.each{File file ->
            if (filterFile(file)) {
                skim(file)
            }
        }
    }
    
    // turn a File that is just a simple name to a full skim File
    private File fullSkimFile(File fileName) {
        File skimLogs = Constants.logSkimDir
        return new File(skimLogs, fileName.name)
    }
    
    private boolean mustReskimFile(File file) {
        return CwalogStatics.dynamicFile(file)
    }

    // fetch the file if needed, then skim the file.  Using this call
    // allows the log files to be deleted
    public File fetchAndSkim(File fileName, Fetch fetch) {
        File file = fullSkimFile(fileName)
        if (mustReskimFile(file) || (!file.exists())) {
            fetch.fetchFile(fileName)
        }
        return skim(file)
    }
    

    // skim the log file if the skim file doesn't exist
    public File skim(File fileName) {
        File skimFile = fullSkimFile(fileName)
        File logFile = new File(Constants.logDir, fileName.name)
        if (mustReskimFile(skimFile)) {
            skimFile.delete()
        }
        if (skimFile.exists()) {
            println "skim - ${skimFile.name} $skimFile <- $logFile"
            return skimFile
        }
        println "skim + ${skimFile.name} $skimFile <- $logFile"
        File tempFile = FileStatic.tempFile(skimFile)
        OutputStream skimFileStream = tempFile.newOutputStream()
        logFile.eachLine {String line ->
            Line l = Line.parse(line)
            if (l) skimFileStream << line << "\n"
        }
        skimFileStream.close()
        assert tempFile.renameTo(skimFile)
        return skimFile
    }
}
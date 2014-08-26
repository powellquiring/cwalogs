import java.util.regex.Pattern;

import groovy.io.FileType;

File logs = new File(/E:\powell\groovy\cwalogs\logs/)
TreeSet files = []
logs.eachFileMatch(FileType.FILES, ~/^cwa-\d*\.log/) {File file -> files << file}
Pattern nameP = ~/(n0002466)|(n0003160)/
//Pattern nameP = ~/n0002466/
files.each{File file ->
//    println file
    file.eachLine {String line ->
        if (nameP.matcher(line)) {
            println line
        }
    }
}
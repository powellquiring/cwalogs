package activity.main
import system.TeeOutputStream
import cwalog.Constants

//Closure fileFilter = {File file -> (file.name == Constants.todaysLogFileName)}
Closure fileFilter = {File file -> (file.name >= 'cwa-20140701.log')}
//Closure fileFilter = {File file -> (file.name >= 'cwa-20140610.log') || (file.name == Constants.todaysLogFileName)}

File outFile = File.createTempFile("cawlog", ".txt")

OutputStream tee = new TeeOutputStream((OutputStream)System.out, outFile.newOutputStream())
PrintStream oldSystemOut = System.out

System.out = new PrintStream(tee)

println "Fetching:"
cwalog.Fetch.instance.fetch(fileFilter)
println "Skimming:"
cwalog.Skim.instance.main(fileFilter)


println "Printing:"

cwalog.PrintGroups.instance.main(fileFilter)
tee.close()
["gvim", "${outFile.getCanonicalPath()}"].execute()


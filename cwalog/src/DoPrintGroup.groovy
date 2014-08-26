import system.TeeOutputStream
import cwalog.Constants


File outFile = File.createTempFile("cawlog", "txt")

OutputStream tee = new TeeOutputStream((OutputStream)System.out, outFile.newOutputStream())
PrintStream oldSystemOut = System.out

System.out = new PrintStream(tee)
println "Printing:"

["gvim", "${outFile.getCanonicalPath()}"].execute()

cwalog.PrintGroups.instance.main(({File file -> (file.name == Constants.todaysLogFileName)}))
//cwalog.PrintGroups.instance.main(({File file -> (file.name >= 'cwa-20140514.log') || (file.name == Constants.todaysLogFileName)}))


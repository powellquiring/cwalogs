package cwalog

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

import cwalog.Constants;
import cwalog.Line;
import cwalog.Group;
import cwalog.Group.Value;
import cwalog.Line.PatternName;
import groovy.io.FileType;
import system.StandardOutToFile;
import system.TeeOutputStream;
import system.StandardOutToFile;

class PrintGroups {
    
    static public final PrintGroups instance = new PrintGroups()
    private PrintGroups(){}

        // These are the skimmed log files that have the non parsed lines removed
    File logs = Constants.logSkimDir

    public List<File> fileList(Closure fileFilter) {
        List<File> files = []
        logs.eachFileMatch(FileType.FILES, ~Constants.validLogName) {File file -> 
            if (!fileFilter(file)) {
                return
            }
            files << file
        }
        return files
    }

    public void main(Closure fileFilter) {
        printLog(fileList(fileFilter), fileFilter)
    }
    
    public void printLog(File fileName, cwalog.Skim skim, cwalog.Fetch fetch) {
        File skimFile = skim.fetchAndSkim(fileName, fetch)
        List<File> files = [skimFile]
        printLog(files, {true})
    }
    
    void printLog(List<File>files, Closure fileFilter) {
        //combine all files Set<String> uniqeUids = new HashSet<String>()
        Group group = new Group()
        files.each{File file ->
            if (!fileFilter(file)) {
                return
            }
            Set<String> uniqeUids = new HashSet<String>()
            println "print processing $file"
            file.eachLine {String line ->
                Line l = Line.parse(line)
                if (l) {
                    group.addLine(l)
                    uniqeUids << l.userId
                }
            }
            // println "${logFileToDateString(file)}\t${uniqeUids.size()}"
            group.endOfInput()
        }
        // combine all files println "---- ${uniqeUids.size()} unique users"
        printDeep(group)
    }
        
    private String logFileToDateString(File log) {
        return "${log.name[4..7]}-${log.name[8..9]}-${log.name[10..11]}"
    }
    
    private void printDeep(Group group) {
        printSortByTime(group, 60)

        Collection<Value> completeValues = group.completeValues.findAll{Group.Value value -> (value.complete.patternName == PatternName.FOOQueryComplete)}

        printCompleteLineCollection("---- complete success values sorted by duration",
            completeValues
                .sort{Group.Value value -> (value.complete.date.getTime() - value.start.date.getTime())})
        
        printCompleteLineCollection("---- complete success values sorted by date",
            completeValues
                .sort{Group.Value value -> (value.start.date.getTime())})

        printCompleteLineCollection("---- complete success values sorted by date less then 100 more then 2 min",
            group.completeValues.findAll{Group.Value value -> ((value.complete.patternName == PatternName.FOOQueryComplete) &&
                (value.packageAvailable + value.packageFailure) <= 100) &&
                ((value.complete.date.getTime() - value.start.date.getTime()) >= (1000 * 120))}
                    .sort{Group.Value value -> (value.start.date.getTime())})
        
        printCompleteLineCollection("---- complete EccManager: ECC Message Id:   Gen.OperationFailed",
            group.completeValues.findAll{Group.Value value -> (value.complete.patternName == PatternName.ECCManagerGenOperationFailed)}
                .sort{Group.Value value -> (value.start.date.getTime())})

        printCompleteLineCollection("---- complete EccManager: Fault: com.ibm.ecc.protocol.ClientAuthenticationNotAuthorized",
            group.completeValues.findAll{Group.Value value -> (value.complete.patternName == PatternName.ECCManagerFaultCANA)}
                .sort{Group.Value value -> (value.start.date.getTime())})

        List<Value> cached = group.incompleteValues.findAll{Group.Value value -> (value.packageAvailable != 0) && (value.start == null) && (value.complete == null)}
        println "---- Total requests: ${completeValues.size() + cached.size()} due to requests with no restrictions satisfied from cache: ${cached.size()} cached:"
        int allCachedOrders = cached.packageInCache.sum()
        int partialCachedOrders = group.completeValues.packageInCache.sum()
        println "total: ${allCachedOrders + partialCachedOrders} = all cached $allCachedOrders + partial cache $partialCachedOrders"
        

                
        List<Value> incompleteValues = group.incompleteValues
            
        println "---- incomplete values minus restrictions satisfied from cache:"
        incompleteValues.minus(cached).each{Group.Value value ->
            println ("start: " + stringOrNull(value.start))
            println ("error: " + stringOrNull(value.notRegistered))
            println ("compl: " + stringOrNull(value.complete))
            println ("entitledRepositoryServiceStarting: " + stringOrNull(null));
            println ("packageAvailable: " + value.packageAvailable.toString())
            println ("packageFailure: " + value.packageFailure.toString())
        }

        int entitlementCachedSize = groupEntitled(group).getCompleteValues().findAll{Group.ValueEntitled value -> value.cached}.size()
        println ("---- entitlement cached: ${entitlementCachedSize}")
        
        printCompleteEntitledCollection("---- entitlement values sorted by duration", groupEntitled(group).getCompleteValues()
            .sort{Group.ValueEntitled value -> (value.complete.date.getTime() - value.start.date.getTime())})
        
        printCompleteEntitledCollection("---- entitlement values sorted by date", groupEntitled(group).getCompleteValues()
            .sort{Group.ValueEntitled value -> (value.start.date.getTime())})
        
        println "---- incomplete values"
        groupEntitled(group).getIncompleteValuesMap().each{String key, Group.ValueEntitled value ->
            println ("key: ${key}")
            println ("start: " + stringOrNull(value.start))
            println ("cached: " + value.cached.toString())
            println ("compl: " + stringOrNull(value.complete))
        }

        long totalTime = completeValues.inject(0) {
            long total, Value value ->
            long diff = (value.complete.date.getTime() - value.start.date.getTime())
            return total + diff
        }
        println "---- Average values"
        printAverageTime("Average time when order required", totalTime, completeValues.size())
        printAverageTime("Average time including cached orders and cached entitlements", totalTime, completeValues.size() + cached.size() + entitlementCachedSize)

        if (groupEntitled(group).getCompleteValues().size() > 0) {
            totalTime = groupEntitled(group).getCompleteValues().inject(0) {
                long total, Group.ValueEntitled value ->
                long diff = (value.complete.date.getTime() - value.start.date.getTime())
                return total + diff
            }
            printAverageTime("Average time for entitlement when order required", totalTime, groupEntitled(group).getCompleteValues().size() - entitlementCachedSize)
            printAverageTime("Average time for entitlement including cached entitlements", totalTime, groupEntitled(group).getCompleteValues().size())
        } else {
            println "No entitlements computed"
        }
    }
    
    void printAverageTime(String message, long Time, int requests) {
        long averageTime = Time / requests
        println("$message: ${secondFormat(averageTime)}")
    }
    
    // entitlement
    private Group.ValueEntitled groupEntitled(Group group) {
        group.getValueForClass(Group.ValueEntitled)
    }

    private void printCompleteEntitledCollection(String header, Collection collection) {
        println header + " : ${collection.size()}"
        collection.each {Group.ValueEntitled value ->
            printCompleteEntitled(value)
        }
    }
    
    private void printCompleteEntitled(Group.ValueEntitled value) {
        Line end = value.complete
        long diff = (value.complete.date.getTime() - value.start.date.getTime())
        println (secondFormat(diff) + " " + end.getOrderId() +  " " + value.start.dateString() + " " + value.start.getUserId() + " " + value.start.getGuid())
    }


    // non entitlement - just normal stuff
	private String stringOrNull(Object o) {
		return o ? o.toString() : "null"
	}

    private void printCompleteLineCollection(String header, Collection collection) {
        println header + " : ${collection.size()}"
        printCompleteLineHeading()
        collection.each {Group.Value value ->
            printCompleteLine(value)
        }
    }
	
    private void printCompleteLineHeading() {
        println " * - request is after entry has expired.  Order - calculated. Actual - when available is the stated order.  Cache - supplied by the cache.  Fail - fixes failed entitilement"
        println ("Min:Sec".padRight(8) + "HSB ID".padRight(10) + "*".padRight(2) + "End Date".padRight(26) + "Order".padLeft(5).padRight(6) + "Actual".padLeft(5).padRight(6) + "Cache".padLeft(5).padRight(6) + "Fail".padLeft(5).padRight(6) + "userid guid")
    }
    private void printCompleteLine(Group.Value value) {
        Line end = value.complete
        long diff = (value.complete.date.getTime() - value.start.date.getTime())
        String complete = (value.notRegistered ? "F" : "S")
        String ordered = (value.packageAvailable + value.packageFailure - value.packageInCache).toString()
        String orderId = end.getOrderId() ? end.getOrderId() : ""
        println (secondFormat(diff).padRight(8) + orderId.padRight(10) +  complete.padRight(2) + value.start.dateString().padRight(26) + ordered.padLeft(5).padRight(6) + value.packagesBeingOrdered.toString().padLeft(5).padRight(6) + value.packageInCache.toString().padLeft(5).padRight(6) + value.packageFailure.toString().padLeft(5).padRight(6) + value.start.getUserId() + " " + value.start.getGuid())
    }
	
    private String secondFormat(long milliSeconds) {
        long seconds = milliSeconds / 1000L
        long minutes = seconds / 60L
        long secondsRemaining = seconds - (minutes * 60)
        String.format("%03d:%02d", minutes, secondsRemaining);
    }

	private long calculateNextIntervalStartInMilliseconds(long intervalStart, int minutes) {
		return intervalStart + (minutes * 60 * 1000)
	}
	private long roundDownToStartOfInterval(long milliseconds, int minutes) {
		long ret = (milliseconds / (minutes * 60 * 1000))
		ret *  (minutes * 60 * 1000)
	}
    private static TimeZone tz = TimeZone.getTimeZone("CST")
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z")
    static {
        sdf.setTimeZone(tz)
    }
    private void displayIntervalHeading() {
        println("Date".padRight(27) + ": " +  "Count")
    }
	private void displayInterval(long currentIntervalStartInMilliseconds, int countInInterval) {
		Date intervalDate = new Date(currentIntervalStartInMilliseconds)
		String dateString = sdf.format(intervalDate)
		println("${dateString}: ${countInInterval}")
	}
	private void printSortByTime(Group group, int minutes) {
		println "---- bar chart divided by ${minutes} minutes sorted by start time"
		def sortedByDate = group.completeValues.findAll{Group.Value value -> (value.complete.patternName == PatternName.FOOQueryComplete)}
		.sort{Group.Value value -> (value.start.date.getTime())}
		Group.Value firstInInterval = sortedByDate[0]
		long currentIntervalStartInMilliseconds = roundDownToStartOfInterval(firstInInterval.start.date.getTime(), minutes)
		long nextIntervalStartInMilliseconds = calculateNextIntervalStartInMilliseconds(currentIntervalStartInMilliseconds, minutes)
		int countInInterval = 0
        displayIntervalHeading()
		sortedByDate.each{Group.Value value -> 
			long valueMilliseconds = value.start.date.getTime()
			if ( valueMilliseconds < nextIntervalStartInMilliseconds) {
				countInInterval++
			} else {
				displayInterval(currentIntervalStartInMilliseconds, countInInterval)
				countInInterval = 0
				currentIntervalStartInMilliseconds = nextIntervalStartInMilliseconds
				nextIntervalStartInMilliseconds = calculateNextIntervalStartInMilliseconds(currentIntervalStartInMilliseconds, minutes)
			}
		}
		displayInterval(currentIntervalStartInMilliseconds, countInInterval)
	}

    public static main(String[] args) {
        StandardOutToFile out = StandardOutToFile.create("cawlog", ".txt")
        instance.main{File file -> file.name == "cwa.log"}
        out.closeAndGvim()
	}

}
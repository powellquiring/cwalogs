package cwalog

import system.FileStatic
import system.UriHelper;

class FetchEccLog {
	public static final FetchEccLog instance = new FetchEccLog()
	private FetchEccLog(){} // use the instance

	public void main() {
		getAndProcessEccLog("eccTrace0.1.log")
	}
	
	private void getAndProcessEccLog(String logString) {
		File fetchedFile = fetch(logString)
		rename(fetchedFile)
		
		
	}
	private File fetch(String logString) {
		File logFile = new File(Constants.logDir, logString)
		if (logFile.exists()) return logFile
		println("+${logFile}")
		FileStatic.copyUrlToFile(new URL(Constants.eccLogUrl, logString), logFile)
		return logFile
	}

	private void rename(File fetchedFile) {
		if (fetchedFile == null) return
		if (!fetchedFile.exists()) return
		BufferedReader reader = fetchedFile.newReader()
		String line1 = reader.readLine()
		println ":${line1}:"
		String line2 = reader.readLine()
		println ":${line2}:"
		reader.close()
		// :   <date>2014-05-15T16:40:26.569-0500</date>: 
		String date = (line2 =~ / +<date>(.*)<\/date>/)[0][1]
		println ":${date}:"
		date = date.replaceAll(/:/, /-/)
		println ":${date}:"
		String prefix = fetchedFile.getName().replaceFirst(/\.[01]\.log$/, "")
		println ":${prefix}:"
		File outputFile = new File(fetchedFile.getParentFile(), "${prefix}_${date}.log")
		println outputFile
		assert(fetchedFile.renameTo(outputFile))
	}

	public static main(String[] args) {
		UriHelper.getSingleton().initAuthenticator();
		instance.main()
	}
}
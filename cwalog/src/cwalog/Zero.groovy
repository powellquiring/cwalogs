package cwalog;

import groovy.io.FileType;

import java.io.File;
import java.util.TreeSet;

class Zero {
	
	public static final Zero instance = new Zero()
	
	File logs = Constants.logSkimDir

	public void main22() {
		Group group = new Group()
		TreeSet files = []
		logs.eachFileMatch(FileType.FILES, ~Constants.validLogName) {File file -> files << file}
		files.each{File file ->
			if ((file.name < 'cwa-20140401.log') &&  (file.name != Constants.todaysLogFileName)){
				println "- " + file.toString()
				file.delete()
				file.newOutputStream() << ""
			}
				
		}
	}
}


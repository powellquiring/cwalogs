package system

class StandardOutToFile {
	final PrintStream oldSystemOut
	final OutputStream tee
	final File outFile
    final OutputStream outFileStream
    final File finalOutFile
    
    // return a 
    static StandardOutToFile createWithTemp(File outFile) {
        outFile.parentFile.mkdirs()
        File tmpOutFile = new File(outFile.getParent(), ".${outFile.name}")    // preceed a "."
        return new StandardOutToFile(outFile, tmpOutFile)
    }
    
    static StandardOutToFile create(String tempFileId, String tempFileExtension, File parentDirectory) {
        parentDirectory.mkdirs()
        File outFile = File.createTempFile(tempFileId, tempFileExtension, parentDirectory)
        return new StandardOutToFile(outFile, null)
    }
    
    static StandardOutToFile create(String tempFileId, String tempFileExtension) {
        File outFile = File.createTempFile(tempFileId, tempFileExtension)
        return new StandardOutToFile(outFile, null)
    }
    
    private StandardOutToFile(File outFile, File tmpOutFile) {
        if (tmpOutFile) {
            this.outFile = tmpOutFile
            this.finalOutFile = outFile
            this.finalOutFile.delete()
        } else {
            this.outFile = outFile
        }
        this.outFile.getParentFile().mkdirs()
        outFileStream = this.outFile.newOutputStream()
        tee = new TeeOutputStream((OutputStream)System.out, outFileStream)
        oldSystemOut = System.out
        System.out = new PrintStream(tee)
    } 
    
	void closeAndGvim() {
        close()
		["gvim", "${outFile.getCanonicalPath()}"].execute()
	}
    
    void close() {
        
        outFileStream.close();
        if(finalOutFile) {
            assert outFile.renameTo(finalOutFile)
        }
        System.out = oldSystemOut
    }
}
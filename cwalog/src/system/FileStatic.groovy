package system

import java.io.File;
import java.net.URL;

class FileStatic {
	static def copyUrlToFile(URL inputUrl, File outputFile) {
		File tmpFile = new File(outputFile.getParentFile(), "." + outputFile.getName())
		if (tmpFile.exists()) {
			tmpFile.delete()
		}
		BufferedOutputStream tmpStream = tmpFile.newOutputStream()
		tmpStream << inputUrl.openStream()
		tmpStream.close()
		assert(tmpFile.renameTo(outputFile))
	}
    
    static File tempFile(File file) {
        return new File(file.parentFile, ".${file.name}")
    }
}

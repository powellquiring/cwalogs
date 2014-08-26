package activity.model

import groovy.io.FileType;
import groovy.lang.Closure;
import groovy.util.Node;
import system.TeeOutputStream;
import cwalog.Constants

class PrintActivity {
    static public final PrintActivity instance = new PrintActivity()
    private PrintActivity(){
    }

    static final String beginDownload = "beginDownload"
    static final String downloadComplete = "downloadComplete"
    static final String getInitData = "getInitData"
    static final String installComplete = "installComplete"
    static final String saveContext = "saveContext"
    static final String signin = "signin"

    final File activitysDir = Constants.activityDir
    public void main(Closure fileFilter) {
        XmlParser parser = new XmlParser()
        TreeSet files = []
        activitysDir.eachFileMatch(FileType.FILES, ~Constants.validActivityName) {File file -> files << file}
        files.each{File file ->
            if (!fileFilter(file)) {
                // println "-$file"
                return
            }
            File tempActivityFile = createTempActivityFile(file)
            
            boolean deleteTempActivityFile = true
            final Node root
            try {
                root = parser.parse(tempActivityFile)

            } catch (Exception e) {
                if (e.getMessage().startsWith("Open quote is expected")) {
                    // zero out these files, this was done on some old files from 2011
                    // assert file.delete()
                    // assert file.createNewFile()
                    println "missing quotes in xml file - ignored"
                } else {
                    deleteTempActivityFile = false
                    e.printStackTrace(System.out)
                }
            }
            
            println "${file.name}\t${root.installComplete.size()}"
            
            // printXml(root)
            
            if (deleteTempActivityFile) {
                assert tempActivityFile.delete()
            }
        }
    }

    private Node getStatusNode(Node ic) { // installComplete node
        return getOneChildByElementOrReturnNull(ic, "status")
    }
    private Node getOfferingNode(Node ic) { // installComplete node
        return getOneChildByElementOrReturnNull(ic, "offering")
    }
    // zero or one child
    private Node getOneChildByElementOrReturnNull(Node node, String elementName) {
        List<Node> child = node."$elementName"
        if(child.size() == 0) return null
        assert child.size() == 1
        return child[0]
    }
    
    /*
     *  The input file has some problems:  It does not have a root element.  It has some bad characters.
     *  Fix these problems and return a temporary file that is good xml
     */
    private File createTempActivityFile(File file) {
        File tempActivityFile = File.createTempFile("activity", ".xml")
        BufferedOutputStream tempActivityFileStream = tempActivityFile.newOutputStream();

        // replace non ascii chars with spaces
        byte[] inputBytes = file.getBytes()
        for(int i = 0; i < inputBytes.size(); i++) {
            if((inputBytes[i] < 1) || (inputBytes[i] == 16)) {
                inputBytes[i] = 32 // space
            }
        }
        InputStream byteInputStream = new ByteArrayInputStream(inputBytes);
        tempActivityFileStream << "<activity>\n"
        tempActivityFileStream << byteInputStream
        tempActivityFileStream << "</activity>\n"
        tempActivityFileStream.close()
        byteInputStream.close()
        return tempActivityFile

    }
    private printXml(Node root) {
        root.children().each{Node child ->
            switch(child.name()) {
                case(beginDownload): break
                case(downloadComplete): break
                case(getInitData): break
                case(installComplete): break
                case(saveContext): break
                case(signin): break
                default:
                    println child.name()
                    break;
            }
        }
    root.installComplete.each{Node ic ->
        String statusString
        String offeringString
        Node offeringNode = getOfferingNode(ic)
        if (offeringNode == null) {
            offeringString = "NOOFFERING"
            statusString = 'NOOFFERING'
        } else {
            offeringString = "${offeringNode.'@name'}_${offeringNode.'@version'}"
            Node statusNode = getStatusNode(ic)
            if (statusNode == null) {
                statusString = "NOSTATUS"
            } else {
                statusString = statusNode.text().trim()
            }
        }
        
        String statusEnum
        println statusString.size()
        switch(statusString) {
            case(~/OK:.*/):
            case(~/(?s)Status OK:.*/):
                statusEnum = "OK"
                break;
            default:
                statusEnum = "BAD"
        }
        println "$offeringString : $statusEnum : $statusString"
    }
    

    }
    
    public static main(String[] args) {
        File outFile = File.createTempFile("activity", ".txt")

        OutputStream tee = new TeeOutputStream((OutputStream)System.out, outFile.newOutputStream())
        PrintStream oldSystemOut = System.out

        System.out = new PrintStream(tee)
        //        instance.main{File file -> (file.name == Constants.todaysActivityFileName)}
                instance.main{File file -> (file.name >= 'activity-20110000.txt')}
//        instance.main{File file -> (true)}
        tee.close()
        [
            "gvim",
            "${outFile.getCanonicalPath()}"
        ].execute()
    }
}

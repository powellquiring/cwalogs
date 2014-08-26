import groovy.xml.XmlUtil;

            XmlParser xmlParser = new XmlParser()
            File destinationLAFile = new File(/c:\tmp\in.xml/)
            File normalizedLAFile = new File(/c:\tmp\out.xml/)
            Node root = xmlParser.parse(destinationLAFile)
//            OutputStream os = normalizedLAFile.newOutputStream()
            PrintWriter os = normalizedLAFile.newPrintWriter()
            XmlUtil.serialize(root, os)
            os.close()

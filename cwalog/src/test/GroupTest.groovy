package test;

import java.awt.geom.Line2D;

import org.junit.Test;

import static org.junit.Assert.*
import cwalog.Group;
import cwalog.Line

public class GroupTest extends GroovyTestCase{
    
    @Test
    public void testGroup() {
        Group group = new Group()
        group.addLine(Line.parse('[ INFO] [03/02/2014 12:02:47 AM CST] [juks524@gmail.com-98474ad3b98242dd9b615441689c3c04766dbfe4-1474] FixOrderOperation: Making eCC orderUpdates query...'))
        group.addLine(Line.parse('[ INFO] [03/02/2014 12:02:49 AM CST] [juks524@gmail.com-98474ad3b98242dd9b615441689c3c04766dbfe4-1474] FixOrderOperation: eCC orderUpdates query completed successfully (H38662748).'))
        group.endOfInput();
        assert group.completeValues.size() == 1
        group.completeValues.each {Group.Value value ->
            assert(value.start.guid == '98474ad3b98242dd9b615441689c3c04766dbfe4')
        }
        
    }
    
    private Group makeGroupFromString(String string) {
        Group group = new Group()
        string.eachLine {lineString ->
            if (lineString.trim().size() == 0) return   // ignore empty lines
			print lineString
            def line = Line.parse(lineString.stripIndent())
            assert(line)
            group.addLine(line)
        }
        group.endOfInput()
        return group
    }
    
    @Test
    public void testPackageAvailableFailureCounts() {
        Group group = makeGroupFromString("""
            [ INFO] [03/27/2014 12:21:39 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Making eCC orderUpdates query...
            [ INFO] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: eCC orderUpdates query completed successfully (H41173950).
            [ERROR] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Update package is not in an ordered state: 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPI13422-Capilano
            [ERROR] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Update package is not in an ordered state: 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPI09443-Capilano
            [ERROR] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Update package is not in an ordered state: 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPI05309-Capilano
            [ERROR] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Update package is not in an ordered state: 8.5.0.0-WS-WASIHS-MultiOS-IFPM89996-Capilano
            [ERROR] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Update package is not in an ordered state: 8.5.5.0-WS-WASIHS-MultiOS-IFPM89996-Capilano
            [ERROR] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Update package is not in an ordered state: 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPM85211-Capilano
            [ INFO] [03/27/2014 12:21:49 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Updates available after order: [8.5.5.0-WS-WASIHS-MultiOS-IFPM89996-Capilano, 8.5.0-WS-WASSupplements-FP0000002-Capilano, 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPM85211-Capilano, 8.5.0.0-WebSphere-WCTILAN-Multiplatform-Base-Capilano-8.5.0.20120501_1108, 8.5.5-WS-WASSupplements-RP-Capilano, 1.1.0.2-WebSphere-W2MTKILAN-Multiplatform-Base-Capilano-1.1.2.20120424_1538, 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPI05309-Capilano, 8.5.0.0-WS-WASIHS-MultiOS-IFPM72915-Capilano, 8.5.0.0-WS-WASIHS-MultiOS-IFPM89996-Capilano, 1.1.0.3-WS-W2MTK-Capilano, 7.0.4.1-WebSphere-LIBERTYJAVA7-Multiplatform-Base-Capilano-7.0.4001.20130510_2103, 6.0.6.0-WS-LIBERTYJAVA6-Capilano, 8.5.5-WS-WASSupplements-FP0000001-Capilano, 8.5.0.0-WebSphere-PLGILAN-Multiplatform-Base-Capilano-8.5.0.20120501_1108, 8.5.0.1-WS-WASIHS-MultiOS-IFPM87808-Capilano, 8.5.5-WS-WASWCT-FP0000001-Capilano, 8.5.0.0-WebSphere-IHSILAN-Multiplatform-Base-Capilano-8.5.0.20120501_1108, 8.5.5.0-WS-WLP-DistOnly-IFPM89011-Capilano, 8.5.0-WS-WASWCT-FP0000001-Capilano, 8.5.5.1-WS-WLP-DistOnly-IFPI05139-Capilano, 8.5.5.0-WS-WLP-DistOnly-IFPI05139-Capilano, 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPI09443-Capilano, 8.5.0-WS-WASSupplements-FP0000001-Capilano, 8.5.5.0-WebSphere-LIBERTYCORETRIAL-Multiplatform-Base-Capilano-8.5.5000.20130514_1313, 1.1.0.4-WS-W2MTK-Capilano, 8.5.5-WS-WASWCT-RP-Capilano, 8.5.5.0-WS-WASIHS-MultiOS-IFPM87808-Capilano, 7.0.5.0-WS-IBMWASJAVA-Capilano, 6.0.5.1-WebSphere-LIBERTYJAVA6-Multiplatform-Base-Capilano-6.0.5001.20130514_1116, 1.1.0.3-WS-W2MTK-IFPM77969-Capilano, 8.5.5-WS-LIBERTYCORE-FP0000001-Capilano, 8.5.0.0-WS-WASIHS_GSKit-MultiOS-IFPI13422-Capilano, 8.5.5.1-WS-WLP-DistOnly-IFPI10103-Capilano, 1.1.0.5-WS-W2MTK-Capilano, 8.5.0-WS-WASWCT-FP0000002-Capilano]
        """)
        assert group.completeValues.size() == 1
        assert group.incompleteValuesMap.size() == 0
        Group.Value v = group.completeValues[0]
        assert v.packageAvailable == 35
        assert v.packageFailure == 6
    }
    @Test
    public void testIncompleteValues() {
        Group group = makeGroupFromString("""
            [ INFO] [03/27/2014 12:21:39 AM CDT] [qiao52672@gmail.com-307775467aa3475304907ada0c224ca71f9bf9ac-242686] FixOrderOperation: Making eCC orderUpdates query...
        """)
        assert group.completeValues.size() == 0
        println group.incompleteValuesMap
        println group.incompleteValuesMap.values()
        assert group.getIncompleteValues().size() == 1
        Group.Value value = group.incompleteValues[0]
        assert value.start
        assert value.complete == null
        assert value.start.patternName == Line.PatternName.FOOQueryStart
    }
    public void testLogFormatChanges() {
        Group group = makeGroupFromString("""
            [ INFO] [06/02/2014 12:59:51 PM CDT] [cwamonitor@us.ibm.com-517fb8db0faccd426bcc44c6872f7b934f14958b-74234] FixOrderOperation: Making eCC orderUpdates query...
            [ INFO] [06/02/2014 12:59:55 PM CDT] [cwamonitor@us.ibm.com-517fb8db0faccd426bcc44c6872f7b934f14958b-74234] FixOrderOperation: eCC orderUpdates query completed successfully (H48454668).
            [ INFO] [06/02/2014 12:59:55 PM CDT] [cwamonitor@us.ibm.com-517fb8db0faccd426bcc44c6872f7b934f14958b-74234] FixOrderOperation: Updates available after order (7 total): [1.6.0.0-Rational-IBMIMCAP-Multiplatform-Update-20120831_1216, 1.6.2.0-Rational-IBMIMCAP-Multiplatform-Update-20130301_2248, 1.6.3.1-Rational-IBMIMCAP-Multiplatform-Update-20130528_1750, 1.6.1.0-Rational-IBMIMCAP-Multiplatform-Update-20121109_1537, 1.7.1.0-Rational-IBMIMCAP-Multiplatform-Update-20131119_2219, 1.7.2.0-IBMIMCAP-Multiplatform-Update-20140227_0303, 1.7.0.0-Rational-IBMIMCAP-Multiplatform-Update-20130828_2012]
        """)
        assert group.completeValues.size() == 1
        assert group.completeValues[0].packageAvailable == 7
    }
    public void testGetValue() {
        Group group = new Group()
        Group.Value v = group.getValueForClass(Group.Value.class)
        Group.ValueEntitled ve = group.getValueForClass(Group.ValueEntitled.class)

        println v
    }
    public void testLogEntitlement() {
        Group group = makeGroupFromString("""
            [ INFO] [06/02/2014 12:02:35 AM CDT] [odelgado@domiruth.com-ac6d3be1da07ea6565c144016d94eb16b9ee349e-40871] EntitledRepositoryService: Starting entitled repository job...
            [ INFO] [06/02/2014 12:02:35 AM CDT] [odelgado@domiruth.com-ac6d3be1da07ea6565c144016d94eb16b9ee349e-40871] EntitledRepositoryService: Found cached entitled repository, using previous result.
            [ INFO] [06/02/2014 12:02:35 AM CDT] [odelgado@domiruth.com-ac6d3be1da07ea6565c144016d94eb16b9ee349e-40871] EntitledRepositoryService: Completed processing entitled repository job.
        """)
        assert group.completeValues.size() == 0
        assert group.getValueForClass(Group.ValueEntitled.class).getCompleteValues().size() == 1
    }
    public void testCachedFOO() {
        Group group = makeGroupFromString("""
            [ INFO] [08/20/2014 12:00:02 AM CDT] [karel@leraks.co.nz-d18da490c9b4369d25b92b279bb5d07b1bbafda3-78548] FixOrderOperation: Updates available after order (1 total): [9.1.0.0-Rational-RBD-servicepack]
            [ INFO] [08/20/2014 12:00:02 AM CDT] [karel@leraks.co.nz-d18da490c9b4369d25b92b279bb5d07b1bbafda3-78548] FixOrderOperation: Updates unordered and retrieved from CWA cache: (1 total): [9.1.0.0-Rational-RBD-servicepack]
        """)
        assert group.completeValues.size() == 0
        List incompletes = group.incompleteValues
        assert incompletes.size() == 1
        Group.Value value = incompletes[0]
        assert value.packageInCache == 1
    }
    public void testOrderedAndCachedMixedFOO() {
        Group group = makeGroupFromString("""
            [ INFO] [08/20/2014 12:02:59 AM CDT] [cwamonitor@us.ibm.com-126b7e53d20df2c3abb1fcdc3e919aed775cf0a5-78675] FixOrderOperation: Making eCC orderUpdates query...
            [ INFO] [08/20/2014 12:02:59 AM CDT] [cwamonitor@us.ibm.com-126b7e53d20df2c3abb1fcdc3e919aed775cf0a5-78675] FixOrderOperation: Fixes being ordered (7 total): [1.6.1.0-Rational-IBMIMCAP-Multiplatform-Update-20121109_1537, 1.6.2.0-Rational-IBMIMCAP-Multiplatform-Update-20130301_2248, 1.6.3.1-Rational-IBMIMCAP-Multiplatform-Update-20130528_1750, 1.7.0.0-Rational-IBMIMCAP-Multiplatform-Update-20130828_2012, 1.7.1.0-Rational-IBMIMCAP-Multiplatform-Update-20131119_2219, 1.7.2.0-IBMIMCAP-Multiplatform-Update-20140227_0303, 1.7.3.0-IBMIMCAP-Multiplatform-Update-20140521_1925]
            [ INFO] [08/20/2014 12:03:06 AM CDT] [cwamonitor@us.ibm.com-126b7e53d20df2c3abb1fcdc3e919aed775cf0a5-78675] FixOrderOperation: eCC orderUpdates query completed successfully (H54964059).
            [ INFO] [08/20/2014 12:03:06 AM CDT] [cwamonitor@us.ibm.com-126b7e53d20df2c3abb1fcdc3e919aed775cf0a5-78675] FixOrderOperation: Updates available after order (8 total): [1.7.3.0-IBMIMCAP-Multiplatform-Update-20140521_1925, 1.6.0.0-Rational-IBMIMCAP-Multiplatform-Update-20120831_1216, 1.6.2.0-Rational-IBMIMCAP-Multiplatform-Update-20130301_2248, 1.6.3.1-Rational-IBMIMCAP-Multiplatform-Update-20130528_1750, 1.6.1.0-Rational-IBMIMCAP-Multiplatform-Update-20121109_1537, 1.7.1.0-Rational-IBMIMCAP-Multiplatform-Update-20131119_2219, 1.7.2.0-IBMIMCAP-Multiplatform-Update-20140227_0303, 1.7.0.0-Rational-IBMIMCAP-Multiplatform-Update-20130828_2012]
            [ INFO] [08/20/2014 12:03:06 AM CDT] [cwamonitor@us.ibm.com-126b7e53d20df2c3abb1fcdc3e919aed775cf0a5-78675] FixOrderOperation: Updates unordered and retrieved from CWA cache: (1 total): [1.6.0.0-Rational-IBMIMCAP-Multiplatform-Update-20120831_1216]
        """)
        assert group.completeValues.size() == 1
        assert group.incompleteValuesMap.size() == 0
        Group.Value v = group.completeValues[0]
        assert v.packageAvailable == 8
        assert v.packageInCache == 1
        assert v.packageFailure == 0
    }
}

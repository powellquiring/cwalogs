package cwalog
// [ INFO] [02/20/2014 11:38:14 AM CST] [powellquiring@gmail.com-8fd46d997959c7beb8d3478f6d263c9359eeae97-341122] FixOrderOperation: Making eCC orderUpdates query...
// [ INFO] [02/20/2014 11:41:35 AM CST] [powellquiring@gmail.com-8fd46d997959c7beb8d3478f6d263c9359eeae97-341122] FixOrderOperation: eCC orderUpdates query completed successfully (H37659812).

import java.nio.charset.MalformedInputException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.*

class Line {


    // pattern group names.  See java doc for jre 7 Pattern
    enum GN { // Group Names
        Type, Date, Day, Month, Year, Hour, Minute, Second, Period, Id, Guid, Cid, OfferingId, OfferingVersion, OrderId, UpdatePackage, Counter
    }
    
    enum TYPE {INFO, ERROR}

    // substrings for patterns
    static private final String LineBeginP = /\[ (?<${GN.Type}>INFO)\] \[(?<${GN.Date}>\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d \w\w C.T)\]/
    static private final String LineBeginErrorP = /\[(?<${GN.Type}>ERROR)\] \[(?<${GN.Date}>\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d \w\w C.T)\]/
    static private final String userIdGuidCIdP = /\[(?<${GN.Id}>.*)-(?<${GN.Guid}>[0-9a-f]{40})-(?<${GN.Cid}>[0-9]*)\]/
    static private final String userIdCIdP = /\[(?<${GN.Id}>.*)-(?<${GN.Cid}>[0-9]*)\]/
    static private final String userIdP = /(?<${GN.Id}>.*)/
    static private final String orderIdP = /\((?<${GN.OrderId}>.*)\)/
    static private final String userGuid = /(?<${GN.Guid}>[0-9a-f]{40})/
    static private final String offeringId = /(?<${GN.OfferingId}>\S*)/
    static private final String offeringVersion = /(?<${GN.OfferingVersion}>\d*\.\d*\.\d*\.\S*)/
    static private final String updatePackageP = /(?<${GN.UpdatePackage}>.*)/
    static private final String counterP = /(?<${GN.Counter}>\d+)/
    

    // pattern names
    enum PatternName { FOOQueryStart, FOOQueryComplete, FOOUpdatePackageNotOrdered, FOOUpdatesAvailable, FOOFixesBeingOrdered, FOOFixesFromCache,
        QCProcessingErrorNotRegistered, ECCManagerGenOperationFailed, ECCManagerFaultCANA, ECCManagerBadWebId,
        EntitledRepositoryServiceStarting, EntitledRepositoryServiceCompleted, EntitledRepositoryServiceFoundCached,
        
        
        QCProcessing, QCReady, QCNoRepo, QCCompleted, QCComplete, 
        QCFound, ABCCompleted, AuthenticatedPreviously, ABCHandling, SCServiceRequest, SCServiceRequestNoVersion, 
        RCSubmittedJob, SRSStarting, SRSProcessing, SRSProcessingNoVersion, SRSLookingFixes, SRSNoFixes, SRSComplete, 
        ARSFiltering, ARSAvailableFixes, RSStarting, RSProcessing, TABRequest, 
        NOT_FOUND}


    static private final Map namePattern = [
        (PatternName.FOOQueryStart) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /FixOrderOperation: Making eCC orderUpdates query\.\.\.$/) ,
        (PatternName.FOOQueryComplete) :  ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /FixOrderOperation: eCC orderUpdates query completed successfully / + orderIdP + '.$'),
        //[ERROR] [03/06/2014 12:51:34 PM CST] [rajeev_rao@conseco.com-6323] QueryController: Specified token is not registered: a3522a4fc197d41d6463d327e2c84add190cff02
        (PatternName.QCProcessingErrorNotRegistered) : ~('^' + LineBeginErrorP + " " + userIdCIdP + " " + /QueryController: Specified token is not registered: / + userGuid) ,
        //[ERROR] [03/09/2014 11:31:07 AM CDT] [thomas.nedelec@capgemini.com-188ac0cf625b06551dd3fb9f994f445aff87d4c3-17115] EccManager: ECC Message Id:   Gen.OperationFailed
        (PatternName.ECCManagerGenOperationFailed) : ~('^' + LineBeginErrorP + " " + userIdGuidCIdP + " " + /EccManager: ECC Message Id:   Gen.OperationFailed/) ,
        //[ERROR] [03/19/2014 11:13:24 PM CDT] [amoran@lighthousecs.com-cfee9541c0b6f4b2c562c9f1e52e6d6a9b662ba1-219404] EccManager: Fault: com.ibm.ecc.protocol.ClientAuthenticationNotAuthorized
        (PatternName.ECCManagerFaultCANA) : ~('^' + LineBeginErrorP + " " + userIdGuidCIdP + " " + /EccManager: Fault: com.ibm.ecc.protocol.ClientAuthenticationNotAuthorized/) ,
        //[ERROR] [03/11/2014 10:08:05 AM CDT] [zetts@us.ibm.com-97917e2f353ed5f9449906a6b1aab0b859375d36-129816] EccManager: ECC Message description: WebIdentity username and password combination not recognized
        (PatternName.ECCManagerBadWebId) : ~('^' + LineBeginErrorP + " " + userIdGuidCIdP + " " + /EccManager: ECC Message description: WebIdentity username and password combination not recognized/) ,
        //[ERROR] [03/26/2014 01:17:57 AM CDT] [ilya.kalin@rt.ru-b3477815ff726e542cc6cc5371e5637d5932cbae-171877] FixOrderOperation: Update package is not in an ordered state: 8.1.0.0-TIV-NCOMNIbus-WebGUI-capilano-beta-activationpack
        (PatternName.FOOUpdatePackageNotOrdered) : ~('^' + LineBeginErrorP + " " + userIdGuidCIdP + " " + /FixOrderOperation: Update package is not in an ordered state: / + updatePackageP) ,
        //[ INFO] [03/26/2014 01:18:22 AM CDT] [ilya.kalin@rt.ru-b3477815ff726e542cc6cc5371e5637d5932cbae-171877] FixOrderOperation: Updates available after order: [8.5.0.0-WS-WASJavaSDK-SolarisSparc32-IFPM98574-Capilano,..., 8.5.0.0-WS-WASJavaSDK-LinuxX64-IFPM98574-Capilano, 8.5.5.0-WS-WASProd-IFPI04832-Capilano]
        (PatternName.FOOUpdatesAvailable) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /FixOrderOperation: Updates available after order( \(/ + counterP + / total\))?: \[/ + updatePackageP + /\]/) ,
        
        // starting 5/29/2014:
        // before the order is made:
        // [ INFO] [08/20/2014 05:27:36 PM CDT] [capilano@us.ibm.com-cba9c6cea19229b9dc8d246a86b35308b8d80f10-128021] FixOrderOperation: Fixes being ordered (1 total): [9.1.0.0-Rational-RAD-refreshpack]
        (PatternName.FOOFixesBeingOrdered) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /FixOrderOperation: Fixes being ordered \(/ + counterP + / total\): \[/ + updatePackageP + /\]/) ,
        // after FOOUpdatesAvailable
        // [ INFO] [08/20/2014 05:27:40 PM CDT] [capilano@us.ibm.com-cba9c6cea19229b9dc8d246a86b35308b8d80f10-128021] FixOrderOperation: Updates unordered and retrieved from CWA cache: (4 total): [9.1-Rational-IBMSDKNodeJS-1104-ifix-repo, 9.1.0.0-Rational-RAD-servicepack, 9.1.0.1-Rational-IBMSDKNodeJS-1104-ifix-repo, 9.1.0.1-Rational-RAD-fixpack]
        (PatternName.FOOFixesFromCache) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /FixOrderOperation: Updates unordered and retrieved from CWA cache: \(/ + counterP + / total\): \[/ + updatePackageP + /\]/) ,
        //
        // if no fixes need to be ordered then there is no FOOQueryStart or FOOQueryComplete. Just these two FOOs:
        // FixOrderOperation: Updates available after order (6 total): [
        // FixOrderOperation: Updates unordered and retrieved from CWA cache: (6 total): [
        
        
        
        
        
        // [ INFO] [06/02/2014 12:01:08 AM CDT] [odelgado@domiruth.com-35946] EntitledController: Processing entitled repositories request...
        
        //[ INFO] [03/26/2014 01:13:21 AM CDT] [ilya.kalin@rt.ru-b3477815ff726e542cc6cc5371e5637d5932cbae-171877] EntitledRepositoryService: Starting entitled repository job...
        (PatternName.EntitledRepositoryServiceStarting) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /EntitledRepositoryService: Starting entitled repository job.../) ,
        // [ INFO] [06/02/2014 12:02:35 AM CDT] [odelgado@domiruth.com-ac6d3be1da07ea6565c144016d94eb16b9ee349e-40871] EntitledRepositoryService: Found cached entitled repository, using previous result.
        (PatternName.EntitledRepositoryServiceFoundCached) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /EntitledRepositoryService: Found cached entitled repository, using previous result./) ,
        // [ INFO] [06/02/2014 12:01:50 AM CDT] [odelgado@domiruth.com-139a4429a5e8e7821af10ccad056138ac953ca47-40835] EntitledRepositoryService: Completed processing entitled repository job.
        (PatternName.EntitledRepositoryServiceCompleted) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /EntitledRepositoryService: Completed processing entitled repository job./) ,
        
        // [ INFO] [06/02/2014 12:59:35 PM CDT] [eriuaem@gmail.com-05835cc83727fd0da263d6aee4d6aea3b313d9d6-73992] EntitledRepositoryService: Looking for private fixes: false
        // [ WARN] [06/02/2014 03:03:26 AM CDT] [thl@semler.dk-c10f0da5985ccb8f48b6380b3fe34f7bcc537ceb-46816] EntitledRepositoryService: No cached content found for: http://delivery04.dhe.ibm.com/sar/CMA/RAA/03cac/0/RDzEnt85-service/repository.xml

        // (PatternName.AuthenticatedPreviously) : ~('^' + LineBeginP + / \[\] / + /AuthenticatingInterceptor: User is already authenticated previously: / + userIdP) ,
//        (PatternName.ABCCompleted) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /AbstractBaseController: Completed handling request$/) ,
//        (PatternName.ABCHandling) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /AbstractBaseController: Handling request for user: .*\.\.\./) ,
//        (PatternName.QCProcessing) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /QueryController: Processing request to query repository status: / + userGuid) ,
//        (PatternName.QCReady) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /QueryController: Repository job is ready after waiting for: .*/) ,
//        (PatternName.QCNoRepo) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /QueryController: No repositories found/) ,
//        (PatternName.QCComplete) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /QueryController: Request for repository completed successfully/) ,
//        (PatternName.QCCompleted) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /QueryController: Request to query repository status completed$/) ,
//        (PatternName.QCFound) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /QueryController: Found.*/) ,
//
//        (PatternName.TABRequest) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /TrialAndBetaController: Processing repository request for offering .*/) ,
//
//        (PatternName.SCServiceRequest) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /ServiceController: Processing service repository request for offering / + offeringId + /, version / + offeringVersion + '...') ,
//        (PatternName.SCServiceRequestNoVersion) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /ServiceController: Processing service repository request for offering / + offeringId + /\. No offering version is requested..../) ,
//        (PatternName.RCSubmittedJob) : ~('^' + LineBeginP + " " + userIdCIdP + " " + /RepositoryController: Submitted job for token: / + userGuid) ,
//        (PatternName.SRSStarting) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /ServiceRepositoryService: Starting service repository service\.\.\.$/) ,
//        (PatternName.SRSProcessing) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /ServiceRepositoryService: Processing repository request for offering / + offeringId + /, version / + offeringVersion + '...') ,
//        (PatternName.SRSProcessingNoVersion) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /ServiceRepositoryService: Processing repository request for offering / + offeringId + /\. No offering version is requested..../) ,
//        (PatternName.SRSLookingFixes) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /ServiceRepositoryService: Looking for private fixes:.*/) ,
//        (PatternName.SRSNoFixes) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /ServiceRepositoryService: No service fixes for product found:.*/) ,
//        (PatternName.SRSComplete) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /ServiceRepositoryService: Completed processing service repository job\..*/) ,
//        (PatternName.FOOUpdatesAvailable) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /FixOrderOperation: Updates available after order: .*/) ,
//        (PatternName.ARSFiltering) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /AbstractRepositoryService: Filtering the set of available fixes\.\.\./) ,
//        (PatternName.ARSAvailableFixes) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /AbstractRepositoryService: Available fixes: .*/) ,
//
//        (PatternName.RSStarting) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /RepositoryService: Starting offering repository service.../) ,
//        (PatternName.RSProcessing) : ~('^' + LineBeginP + " " + userIdGuidCIdP + " " + /RepositoryService: Processing repository request for offering .*/) ,
        (PatternName.NOT_FOUND) :  ~('^.*$')
    ]

    String lineString
    Date date
    PatternName patternName
    String userId
    String guid
    String cId
    String orderId
    TYPE type
    String updatePackage
    int counter

    
    private static TimeZone tz = TimeZone.getTimeZone("CST")
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z")
    static {
        sdf.setTimeZone(tz)
    }

    private Line(String lineString, PatternName name, Date date, String userId, String guid, String cId, String orderId, TYPE type, String updatePackage, int counter) {
        this.lineString = lineString
        this.patternName = name
        this.date = date
        this.userId = userId
        this.guid = guid
        this.cId = cId
        this.orderId = orderId
        this.type = type
        this.updatePackage = updatePackage
        this.counter = counter
    }
    
    // convenience methods for formating strings
    String idGuidCid() {userId + guid + cId}
    String dateString() {
        sdf.format(this.date)
    }

    // return null if the GN is not found
    private static String gn(Matcher m, GN gn) {
        try {
            return gnCheck(m, gn)
        } catch (IllegalArgumentException iae) {
            return null
        }
        
    }
    // throw exception if the GN is not found
    private static String gnCheck(Matcher m, GN gn) {
        m.group(gn.toString())
    }

    static Line parse(String lineString) {
        PatternName name
        Matcher m
        (m,name) = namePattern.findResult{PatternName nameInner, Pattern pattern ->
            Matcher mInner = pattern.matcher(lineString)
            if (mInner.matches()) {
                [mInner, nameInner]
            } else {
                null
            }
        }
        if (name == PatternName.NOT_FOUND) return null
        
        int localCounter
        switch(name) {
            case PatternName.FOOFixesBeingOrdered:
            case PatternName.FOOFixesFromCache:
                localCounter = Integer.parseInt(gnCheck(m, GN.Counter))
                break;
            case PatternName.FOOUpdatesAvailable:   // newer logs have the counter, older logs do not
                String optionalCounter = gn(m, GN.Counter)
                if (optionalCounter) {
                    localCounter = Integer.parseInt(optionalCounter)
                }
                break;
        }
        return new Line(
            lineString,
            name,
            new Date(Date.parse(gnCheck(m, GN.Date))),
            gn(m, GN.Id),
            gn(m, GN.Guid),
            gn(m, GN.Cid),
            gn(m, GN.OrderId),
            TYPE.valueOf(gn(m, GN.Type)),
            gn(m, GN.UpdatePackage),
            localCounter
            )
    }
    
    public String toString() {
        lineString
    }

}

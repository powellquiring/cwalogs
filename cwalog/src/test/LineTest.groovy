package test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.junit.Test;

import cwalog.Line;
import cwalog.Line.PatternName;

public class LineTest extends GroovyTestCase{
    
    @Test
    public void testParsingOfSpecificLines() {
        String ls
        Line l
        
        ls = '[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-6aa870343b835dd20066724d11d7124126f87fd4-206130] FixOrderOperation: eCC orderUpdates query completed successfully (H37459160).'
        l = Line.parse(ls)
        assert(l.orderId == 'H37459160')
        TimeZone tz = TimeZone.getTimeZone("CST")
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z")
        sdf.setTimeZone(tz)
        assert (sdf.format(l.date)) == '02/19/2014 12:00:01 AM CST'
        assert (l.dateString() == '02/19/2014 12:00:01 AM CST')

        ls = '[ERROR] [03/06/2014 12:51:34 PM CST] [rajeev_rao@conseco.com-6323] QueryController: Specified token is not registered: a3522a4fc197d41d6463d327e2c84add190cff02'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.QCProcessingErrorNotRegistered)
        assert(l.guid == 'a3522a4fc197d41d6463d327e2c84add190cff02')
        assert(l.type == Line.TYPE.ERROR)
        
        ls = '[ INFO] [03/07/2014 12:00:10 AM CST] [christopher.moy@walgreens.com-4fbc1a4caf0bb0cb62c6e8632938160b0bc6f482-324661] FixOrderOperation: Making eCC orderUpdates query...'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOQueryStart)
        assert(l.type == Line.TYPE.INFO)
        
        ls = '[ INFO] [03/11/2014 12:00:01 AM CDT] [jwu3@statestreet.com-7ce63c9aadb9a74136a559a4be508ceef6fa128e-97392] FixOrderOperation: eCC orderUpdates query completed successfully (H39529268).'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOQueryComplete)
        
        ls = '[ERROR] [03/09/2014 11:31:07 AM CDT] [thomas.nedelec@capgemini.com-188ac0cf625b06551dd3fb9f994f445aff87d4c3-17115] EccManager: ECC Message Id:   Gen.OperationFailed'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.ECCManagerGenOperationFailed)
        
        ls = '[ERROR] [03/11/2014 10:08:05 AM CDT] [zetts@us.ibm.com-97917e2f353ed5f9449906a6b1aab0b859375d36-129816] EccManager: ECC Message description: WebIdentity username and password combination not recognized'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.ECCManagerBadWebId)
        
        ls = '[ERROR] [03/19/2014 11:13:24 PM CDT] [amoran@lighthousecs.com-cfee9541c0b6f4b2c562c9f1e52e6d6a9b662ba1-219404] EccManager: Fault: com.ibm.ecc.protocol.ClientAuthenticationNotAuthorized'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.ECCManagerFaultCANA)

        ls = '[ERROR] [03/26/2014 01:17:57 AM CDT] [ilya.kalin@rt.ru-b3477815ff726e542cc6cc5371e5637d5932cbae-171877] FixOrderOperation: Update package is not in an ordered state: 8.1.0.0-TIV-NCOMNIbus-WebGUI-capilano-beta-activationpack'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOUpdatePackageNotOrdered)
        assert(l.updatePackage == '8.1.0.0-TIV-NCOMNIbus-WebGUI-capilano-beta-activationpack')

        ls = '[ INFO] [03/26/2014 01:18:22 AM CDT] [ilya.kalin@rt.ru-b3477815ff726e542cc6cc5371e5637d5932cbae-171877] FixOrderOperation: Updates available after order: [8.5.0.0-WS-WASJavaSDK-SolarisSparc32-IFPM98574-Capilano,..., 8.5.0.0-WS-WASJavaSDK-LinuxX64-IFPM98574-Capilano, 8.5.5.0-WS-WASProd-IFPI04832-Capilano]'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOUpdatesAvailable)
        assert(l.updatePackage == '8.5.0.0-WS-WASJavaSDK-SolarisSparc32-IFPM98574-Capilano,..., 8.5.0.0-WS-WASJavaSDK-LinuxX64-IFPM98574-Capilano, 8.5.5.0-WS-WASProd-IFPI04832-Capilano')
        
        ls = '[ INFO] [03/26/2014 01:13:21 AM CDT] [ilya.kalin@rt.ru-b3477815ff726e542cc6cc5371e5637d5932cbae-171877] EntitledRepositoryService: Starting entitled repository job...'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.EntitledRepositoryServiceStarting)
        
        ls = '[ INFO] [06/02/2014 12:02:47 AM CDT] [odelgado@domiruth.com-b3744034303e45230017d4f6bc72a58c66bb8127-40871] EntitledRepositoryService: Found cached entitled repository, using previous result.'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.EntitledRepositoryServiceFoundCached)

        ls = '[ INFO] [06/02/2014 12:02:47 AM CDT] [odelgado@domiruth.com-b3744034303e45230017d4f6bc72a58c66bb8127-40871] EntitledRepositoryService: Completed processing entitled repository job.'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.EntitledRepositoryServiceCompleted)

        ls = '[ INFO] [08/20/2014 05:27:36 PM CDT] [capilano@us.ibm.com-cba9c6cea19229b9dc8d246a86b35308b8d80f10-128021] FixOrderOperation: Fixes being ordered (1 total): [9.1.0.0-Rational-RAD-refreshpack]'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOFixesBeingOrdered)
        assert(l.counter == 1)

        ls = '[ INFO] [08/20/2014 05:27:40 PM CDT] [capilano@us.ibm.com-cba9c6cea19229b9dc8d246a86b35308b8d80f10-128021] FixOrderOperation: Updates unordered and retrieved from CWA cache: (4 total): [9.1-Rational-IBMSDKNodeJS-1104-ifix-repo, 9.1.0.0-Rational-RAD-servicepack, 9.1.0.1-Rational-IBMSDKNodeJS-1104-ifix-repo, 9.1.0.1-Rational-RAD-fixpack]'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOFixesFromCache)
        assert(l.counter == 4)

    }
    
    @Test
    public void testExamples() {
        assert(Line.parse('[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-d038e7cbcdf9f73765f764b23e921d21ed4ba2cd-206130] FixOrderOperation: Making eCC orderUpdates query...'))
        assert(Line.parse('bad') == null)
    }

    // skip this stuff for now
    private void ParseStuff() {
       
"""[ INFO] [02/19/2014 12:00:00 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:00 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:01 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:01 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:01 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Processing request to query repository status: adbbfba9a7a288e2119d59d009868af16288d442
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-73113] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:01 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-73113] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-73113] ServiceController: Processing service repository request for offering com.ibm.isa.tools.memoryanalyzer, version 1.2.0.0...
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-73113] RepositoryController: Submitted job for token: 539b722e7b9b2f44e305b9abc48f52a9e8fa473f
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-539b722e7b9b2f44e305b9abc48f52a9e8fa473f-206253] ServiceRepositoryService: Starting service repository service...
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-539b722e7b9b2f44e305b9abc48f52a9e8fa473f-206253] ServiceRepositoryService: Processing repository request for offering com.ibm.isa.tools.memoryanalyzer, version 1.2.0.0...
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-539b722e7b9b2f44e305b9abc48f52a9e8fa473f-206253] ServiceRepositoryService: Looking for private fixes: false
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-539b722e7b9b2f44e305b9abc48f52a9e8fa473f-206253] ServiceRepositoryService: No service fixes for product found: Memory Analyzer
[ INFO] [02/19/2014 12:00:01 AM CST] [Rajesh.Goel@ca.com-539b722e7b9b2f44e305b9abc48f52a9e8fa473f-206253] ServiceRepositoryService: Completed processing service repository job.
[ INFO] [02/19/2014 12:00:01 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-73113] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-73113] QueryController: Processing request to query repository status: 6aa870343b835dd20066724d11d7124126f87fd4
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-6aa870343b835dd20066724d11d7124126f87fd4-206130] FixOrderOperation: eCC orderUpdates query completed successfully (H37459160).
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-6aa870343b835dd20066724d11d7124126f87fd4-206130] FixOrderOperation: Updates available after order: [9.0.0.0-Rational-RAD-servicepack, 9.0.1.0-Rational-RAD-fixpack]
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-6aa870343b835dd20066724d11d7124126f87fd4-206130] AbstractRepositoryService: Filtering the set of available fixes...
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-6aa870343b835dd20066724d11d7124126f87fd4-206130] AbstractRepositoryService: Available fixes: [9.0.0.0-Rational-RAD-servicepack, 9.0.1.0-Rational-RAD-fixpack]
[ INFO] [02/19/2014 12:00:01 AM CST] [MertonKnight@gmail.com-6aa870343b835dd20066724d11d7124126f87fd4-206130] ServiceRepositoryService: Completed processing service repository job.
[ INFO] [02/19/2014 12:00:02 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] QueryController: Processing request to query repository status: 539b722e7b9b2f44e305b9abc48f52a9e8fa473f
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] QueryController: Repository job is ready after waiting for: 0 seconds.
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] QueryController: No repositories found
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] QueryController: Request for repository completed successfully
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:02 AM CST] [Rajesh.Goel@ca.com-140400] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:03 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Processing request to query repository status: adbbfba9a7a288e2119d59d009868af16288d442
[ INFO] [02/19/2014 12:00:03 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-140400] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-140400] ServiceController: Processing service repository request for offering com.ibm.websphere.ND.v80, version 8.0.0.7...
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-140400] RepositoryController: Submitted job for token: d038e7cbcdf9f73765f764b23e921d21ed4ba2cd
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-d038e7cbcdf9f73765f764b23e921d21ed4ba2cd-206130] ServiceRepositoryService: Starting service repository service...
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-140400] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-d038e7cbcdf9f73765f764b23e921d21ed4ba2cd-206130] ServiceRepositoryService: Processing repository request for offering com.ibm.websphere.ND.v80, version 8.0.0.7...
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-d038e7cbcdf9f73765f764b23e921d21ed4ba2cd-206130] ServiceRepositoryService: Looking for private fixes: false
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-d038e7cbcdf9f73765f764b23e921d21ed4ba2cd-206130] FixOrderOperation: Making eCC orderUpdates query...
[ INFO] [02/19/2014 12:00:03 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-140400] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:03 AM CST] [rtclauss@us.ibm.com-140400] QueryController: Processing request to query repository status: d038e7cbcdf9f73765f764b23e921d21ed4ba2cd
[ INFO] [02/19/2014 12:00:03 AM CST] [MertonKnight@gmail.com-73113] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:03 AM CST] [MertonKnight@gmail.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:04 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] QueryController: Processing request to query repository status: 6aa870343b835dd20066724d11d7124126f87fd4
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] QueryController: Repository job is ready after waiting for: 0 seconds.
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] QueryController: Found 3 repositories
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] QueryController: Request for repository completed successfully
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:04 AM CST] [MertonKnight@gmail.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:04 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: oidpermana@gmail.com
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-73113] AbstractBaseController: Handling request for user: oidpermana@gmail.com ...
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-73113] TrialAndBetaController: Processing repository request for offering com.ibm.rational.rad.ext.rtcs.v80. No offering version is requested....
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-73113] RepositoryController: Submitted job for token: cbd5431ba32b849db71f81f5c34c6d04df635e19
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-cbd5431ba32b849db71f81f5c34c6d04df635e19-206253] RepositoryService: Starting offering repository service...
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-cbd5431ba32b849db71f81f5c34c6d04df635e19-206253] RepositoryService: Processing repository request for offering com.ibm.rational.rad.ext.rtcs.v80. No offering version is requested....
[ INFO] [02/19/2014 12:00:04 AM CST] [oidpermana@gmail.com-cbd5431ba32b849db71f81f5c34c6d04df635e19-206253] FixOrderOperation: Making eCC orderUpdates query...
[ INFO] [02/19/2014 12:00:04 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-73113] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-73113] ServiceController: Processing service repository request for offering com.ibm.isa.tools.memoryanalyzer.jws, version 1.2.0.0...
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-73113] RepositoryController: Submitted job for token: 70f41aff82f50ad173b7f48f59ecad0d15fe99f7
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-70f41aff82f50ad173b7f48f59ecad0d15fe99f7-206258] ServiceRepositoryService: Starting service repository service...
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-70f41aff82f50ad173b7f48f59ecad0d15fe99f7-206258] ServiceRepositoryService: Processing repository request for offering com.ibm.isa.tools.memoryanalyzer.jws, version 1.2.0.0...
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-70f41aff82f50ad173b7f48f59ecad0d15fe99f7-206258] ServiceRepositoryService: Looking for private fixes: false
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-70f41aff82f50ad173b7f48f59ecad0d15fe99f7-206258] ServiceRepositoryService: No service fixes for product found: Memory Analyzer - Java WebStart
[ INFO] [02/19/2014 12:00:04 AM CST] [Rajesh.Goel@ca.com-70f41aff82f50ad173b7f48f59ecad0d15fe99f7-206258] ServiceRepositoryService: Completed processing service repository job.
[ INFO] [02/19/2014 12:00:05 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: oidpermana@gmail.com
[ INFO] [02/19/2014 12:00:05 AM CST] [oidpermana@gmail.com-73113] AbstractBaseController: Handling request for user: oidpermana@gmail.com ...
[ INFO] [02/19/2014 12:00:05 AM CST] [oidpermana@gmail.com-73113] QueryController: Processing request to query repository status: cbd5431ba32b849db71f81f5c34c6d04df635e19
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:05 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-1262] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-1262] ServiceController: Processing service repository request for offering com.ibm.rational.functional.tester. No offering version is requested....
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-1262] RepositoryController: Submitted job for token: 579b6793f94b024ed9265ed83a968ac5de939932
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-579b6793f94b024ed9265ed83a968ac5de939932-206258] ServiceRepositoryService: Starting service repository service...
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-579b6793f94b024ed9265ed83a968ac5de939932-206258] ServiceRepositoryService: Processing repository request for offering com.ibm.rational.functional.tester. No offering version is requested....
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-579b6793f94b024ed9265ed83a968ac5de939932-206258] ServiceRepositoryService: Looking for private fixes: false
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-579b6793f94b024ed9265ed83a968ac5de939932-206258] FixOrderOperation: Making eCC orderUpdates query...
[ INFO] [02/19/2014 12:00:05 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Processing request to query repository status: adbbfba9a7a288e2119d59d009868af16288d442
[ INFO] [02/19/2014 12:00:05 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] QueryController: Processing request to query repository status: 70f41aff82f50ad173b7f48f59ecad0d15fe99f7
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] QueryController: Repository job is ready after waiting for: 0 seconds.
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] QueryController: No repositories found
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] QueryController: Request for repository completed successfully
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:05 AM CST] [Rajesh.Goel@ca.com-140262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-140400] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-140400] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:05 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-140400] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:05 AM CST] [MertonKnight@gmail.com-140400] QueryController: Processing request to query repository status: 579b6793f94b024ed9265ed83a968ac5de939932
[ INFO] [02/19/2014 12:00:05 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-140262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:05 AM CST] [rtclauss@us.ibm.com-140262] QueryController: Processing request to query repository status: d038e7cbcdf9f73765f764b23e921d21ed4ba2cd
[ INFO] [02/19/2014 12:00:07 AM CST] [oidpermana@gmail.com-73113] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:07 AM CST] [oidpermana@gmail.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:07 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:07 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:07 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-1262] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-1262] ServiceController: Processing service repository request for offering com.ibm.isa.tools.memoryanalyzer.web, version 1.1.0.0...
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-1262] RepositoryController: Submitted job for token: 426bb6342883c22ea1b534af3a425deff065dc27
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-426bb6342883c22ea1b534af3a425deff065dc27-206264] ServiceRepositoryService: Starting service repository service...
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-426bb6342883c22ea1b534af3a425deff065dc27-206264] ServiceRepositoryService: Processing repository request for offering com.ibm.isa.tools.memoryanalyzer.web, version 1.1.0.0...
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-426bb6342883c22ea1b534af3a425deff065dc27-206264] ServiceRepositoryService: Looking for private fixes: false
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-426bb6342883c22ea1b534af3a425deff065dc27-206264] ServiceRepositoryService: No service fixes for product found: Memory Analyzer - Web
[ INFO] [02/19/2014 12:00:07 AM CST] [Rajesh.Goel@ca.com-426bb6342883c22ea1b534af3a425deff065dc27-206264] ServiceRepositoryService: Completed processing service repository job.
[ INFO] [02/19/2014 12:00:07 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:07 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:07 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Processing request to query repository status: adbbfba9a7a288e2119d59d009868af16288d442
[ INFO] [02/19/2014 12:00:07 AM CST] [MertonKnight@gmail.com-140400] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:07 AM CST] [MertonKnight@gmail.com-140400] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:07 AM CST] [rtclauss@us.ibm.com-140262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:07 AM CST] [rtclauss@us.ibm.com-140262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:08 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: oidpermana@gmail.com
[ INFO] [02/19/2014 12:00:08 AM CST] [oidpermana@gmail.com-73113] AbstractBaseController: Handling request for user: oidpermana@gmail.com ...
[ INFO] [02/19/2014 12:00:08 AM CST] [oidpermana@gmail.com-73113] QueryController: Processing request to query repository status: cbd5431ba32b849db71f81f5c34c6d04df635e19
[ INFO] [02/19/2014 12:00:08 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:08 AM CST] [MertonKnight@gmail.com-140400] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:08 AM CST] [MertonKnight@gmail.com-140400] QueryController: Processing request to query repository status: 579b6793f94b024ed9265ed83a968ac5de939932
[ INFO] [02/19/2014 12:00:08 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:08 AM CST] [rtclauss@us.ibm.com-140262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:08 AM CST] [rtclauss@us.ibm.com-140262] QueryController: Processing request to query repository status: d038e7cbcdf9f73765f764b23e921d21ed4ba2cd
[ INFO] [02/19/2014 12:00:08 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Processing request to query repository status: 426bb6342883c22ea1b534af3a425deff065dc27
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Repository job is ready after waiting for: 0 seconds.
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: No repositories found
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Request for repository completed successfully
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:08 AM CST] [Rajesh.Goel@ca.com-188174] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:09 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:09 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:09 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:09 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:09 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Processing request to query repository status: adbbfba9a7a288e2119d59d009868af16288d442
[ INFO] [02/19/2014 12:00:10 AM CST] [oidpermana@gmail.com-73113] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:10 AM CST] [oidpermana@gmail.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:10 AM CST] [MertonKnight@gmail.com-140400] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:10 AM CST] [MertonKnight@gmail.com-140400] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:10 AM CST] [rtclauss@us.ibm.com-140262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:10 AM CST] [rtclauss@us.ibm.com-140262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:10 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-73113] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-73113] ServiceController: Processing service repository request for offering com.ibm.isa.tools.pmat, version 4.3.6.0...
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-73113] RepositoryController: Submitted job for token: 13aa5f1634b1d20affc927a28500f279879c6f1e
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-13aa5f1634b1d20affc927a28500f279879c6f1e-206264] ServiceRepositoryService: Starting service repository service...
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-13aa5f1634b1d20affc927a28500f279879c6f1e-206264] ServiceRepositoryService: Processing repository request for offering com.ibm.isa.tools.pmat, version 4.3.6.0...
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-13aa5f1634b1d20affc927a28500f279879c6f1e-206264] ServiceRepositoryService: Looking for private fixes: false
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-13aa5f1634b1d20affc927a28500f279879c6f1e-206264] ServiceRepositoryService: No service fixes for product found: Pattern Modeling and Analysis Tool for Java Garbage Collector
[ INFO] [02/19/2014 12:00:10 AM CST] [Rajesh.Goel@ca.com-13aa5f1634b1d20affc927a28500f279879c6f1e-206264] ServiceRepositoryService: Completed processing service repository job.
[ INFO] [02/19/2014 12:00:10 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:10 AM CST] [rtclauss@us.ibm.com-73113] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:10 AM CST] [rtclauss@us.ibm.com-73113] QueryController: Processing request to query repository status: d038e7cbcdf9f73765f764b23e921d21ed4ba2cd
[ INFO] [02/19/2014 12:00:10 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:10 AM CST] [MertonKnight@gmail.com-140400] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:10 AM CST] [MertonKnight@gmail.com-140400] QueryController: Processing request to query repository status: 579b6793f94b024ed9265ed83a968ac5de939932
[ INFO] [02/19/2014 12:00:10 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: oidpermana@gmail.com
[ INFO] [02/19/2014 12:00:10 AM CST] [oidpermana@gmail.com-140262] AbstractBaseController: Handling request for user: oidpermana@gmail.com ...
[ INFO] [02/19/2014 12:00:10 AM CST] [oidpermana@gmail.com-140262] QueryController: Processing request to query repository status: cbd5431ba32b849db71f81f5c34c6d04df635e19
[ INFO] [02/19/2014 12:00:11 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: Rajesh.Goel@ca.com
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] AbstractBaseController: Handling request for user: Rajesh.Goel@ca.com ...
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Processing request to query repository status: 13aa5f1634b1d20affc927a28500f279879c6f1e
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Repository job is ready after waiting for: 0 seconds.
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: No repositories found
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Request for repository completed successfully
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:11 AM CST] [Rajesh.Goel@ca.com-188174] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:11 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:11 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:11 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:11 AM CST] [rtclauss@us.ibm.com-1262] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:11 AM CST] [rtclauss@us.ibm.com-1262] QueryController: Processing request to query repository status: adbbfba9a7a288e2119d59d009868af16288d442
[ INFO] [02/19/2014 12:00:12 AM CST] [rtclauss@us.ibm.com-73113] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:12 AM CST] [rtclauss@us.ibm.com-73113] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:12 AM CST] [MertonKnight@gmail.com-140400] QueryController: Request to query repository status completed
[ INFO] [02/19/2014 12:00:12 AM CST] [MertonKnight@gmail.com-140400] AbstractBaseController: Completed handling request
[ INFO] [02/19/2014 12:00:12 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: MertonKnight@gmail.com
[ INFO] [02/19/2014 12:00:12 AM CST] [MertonKnight@gmail.com-73113] AbstractBaseController: Handling request for user: MertonKnight@gmail.com ...
[ INFO] [02/19/2014 12:00:12 AM CST] [MertonKnight@gmail.com-73113] QueryController: Processing request to query repository status: 579b6793f94b024ed9265ed83a968ac5de939932
[ INFO] [02/19/2014 12:00:12 AM CST] [] AuthenticatingInterceptor: User is already authenticated previously: rtclauss@us.ibm.com
[ INFO] [02/19/2014 12:00:12 AM CST] [rtclauss@us.ibm.com-140400] AbstractBaseController: Handling request for user: rtclauss@us.ibm.com ...
[ INFO] [02/19/2014 12:00:12 AM CST] [rtclauss@us.ibm.com-140400] QueryController: Processing request to query repository status: d038e7cbcdf9f73765f764b23e921d21ed4ba2cd
[ INFO] [02/19/2014 12:00:12 AM CST] [oidpermana@gmail.com-140262] QueryController: Request to query repository status completed""".eachLine {lineString ->
        println lineString
        def line = Line.parse(lineString)
        assert(line)
    }

}

	@Test
	public void testNewFixAvailable() {
        String ls
        Line l
        
        ls = '[ INFO] [06/02/2014 12:59:55 PM CDT] [cwamonitor@us.ibm.com-517fb8db0faccd426bcc44c6872f7b934f14958b-74234] FixOrderOperation: Updates available after order (7 total): [1.6.0.0-Rational-IBMIMCAP-Multiplatform-Update-20120831_1216, 1.6.2.0-Rational-IBMIMCAP-Multiplatform-Update-20130301_2248, 1.6.3.1-Rational-IBMIMCAP-Multiplatform-Update-20130528_1750, 1.6.1.0-Rational-IBMIMCAP-Multiplatform-Update-20121109_1537, 1.7.1.0-Rational-IBMIMCAP-Multiplatform-Update-20131119_2219, 1.7.2.0-IBMIMCAP-Multiplatform-Update-20140227_0303, 1.7.0.0-Rational-IBMIMCAP-Multiplatform-Update-20130828_2012]'
        l = Line.parse(ls)
        assert(l.patternName == PatternName.FOOUpdatesAvailable)
		assert(l.updatePackage == '1.6.0.0-Rational-IBMIMCAP-Multiplatform-Update-20120831_1216, 1.6.2.0-Rational-IBMIMCAP-Multiplatform-Update-20130301_2248, 1.6.3.1-Rational-IBMIMCAP-Multiplatform-Update-20130528_1750, 1.6.1.0-Rational-IBMIMCAP-Multiplatform-Update-20121109_1537, 1.7.1.0-Rational-IBMIMCAP-Multiplatform-Update-20131119_2219, 1.7.2.0-IBMIMCAP-Multiplatform-Update-20140227_0303, 1.7.0.0-Rational-IBMIMCAP-Multiplatform-Update-20130828_2012')
		
	}

}

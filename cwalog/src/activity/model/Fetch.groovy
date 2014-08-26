package activity.model
import javax.management.InstanceOfQueryExp;

import cwalog.Constants;
import system.FileStatic;
import system.UriHelper;

class Fetch {
    boolean overwriteFile = true    // default true, refetch the log html file
    boolean overwriteTodaysFile = true   // default true, refetch today's log file

    public static final Fetch instance = new Fetch()
    private Fetch(){} // use the instance

    public main() {
        // fetch the activity files.
        UriHelper.getSingleton().initAuthenticator();
        File activityHtml = new File(Constants.activityDir, /activity.html/)
        if (overwriteFile || !activityHtml.exists()) {
            println "+${activityHtml.toString()}"
            if (activityHtml.exists()){activityHtml.delete()}
            FileStatic.copyUrlToFile(Constants.activityUrl, activityHtml)
        }
        if (overwriteTodaysFile && Constants.todaysActivityFile.exists()) {
            Constants.todaysActivityFile.delete()
        }


        def parser = new org.cyberneko.html.parsers.SAXParser()
        Node root =   new XmlParser( parser ).parse( activityHtml )
        root.BODY.PRE.A.each{
            String activityName = it.'@href'
            //          println activityName
            if(activityName ==~ Constants.validActivityName) {
                File activityFile = new File(Constants.activityDir, activityName)
                //              println " $activityFile"
                if (activityFile.exists()) {
                    //                    println "-$activityFile"
                } else {
                    println "+$activityFile"
                    FileStatic.copyUrlToFile(new URL(Constants.activityUrl.toString() + "/" + activityName), activityFile)
                }
            }
        }
    }
    public static main(String[] args) {
        instance.main()
    }
}

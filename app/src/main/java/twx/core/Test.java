package twx.core;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Scanner;
import java.util.Date;
import java.util.TimeZone;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.util.SystemInfo;
import twx.core.utils.MultiTimer;

public class Test {
    final static Logger 		logger  = LoggerFactory.getLogger(Test.class);
   
  
    public void match1() throws Exception  {
    	String topic	= "SIG";
    	String filter 	= "SIG/#";
    	
		boolean match = twx.core.string.StringTopicMatcher.match(filter, topic);;
		logger.info( "Pattern: " + filter + "  Topic: " +  topic + "  Match: " +  match);
    }
    
	public void pdf1() throws Exception  {
				
	}


    public void time1() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        var date = new Date();
        logger.info( date.toString() );
        logger.info( java.util.TimeZone.getDefault().getID() );

        var zones = TimeZone.getAvailableIDs();
        for( var z : zones ) {
            logger.info( z.toString() );
        }

        //get Calendar instance
        Calendar now = Calendar.getInstance();
    
        //get current TimeZone using getTimeZone method of Calendar class
        TimeZone timeZone = now.getTimeZone();
    
        //display current TimeZone using getDisplayName() method of TimeZone class
        System.out.println("Current TimeZone is : " + timeZone.getDisplayName());

        System.currentTimeMillis();
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", "/c", "Get-TimeZone");

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

		BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
        String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}      
        logger.info( output.toString() );
    }

    public void time2() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.FULL);
    
        ZonedDateTime hereAndNow = ZonedDateTime.now();
        System.out.println(dateTimeFormatter.format(hereAndNow));
        System.out.println(hereAndNow); // formatter is not required

        System.out.println( System.getenv("time") );
    }

    public void timer1() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        MultiTimer multiTimer = new MultiTimer();
        Thread.sleep(100);
        multiTimer.push("Hallo 1");
        Thread.sleep(100);
        multiTimer.push("Hallo 2");
        Thread.sleep(100);
        multiTimer.push("Hallo 3");

        System.out.println( multiTimer.toString() ); 

    }

    public Integer systemTime() throws Exception {
        JSONObject obj = new JSONObject();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", "/c", "$now = date; (($now)-($now).touniversaltime()).TotalMinutes");
        Process process = processBuilder.start();
		BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        Integer result = Integer.parseInt(line);

    
        logger.info( "TotalOffset Minutes: "+ result );
        return result;
    }

    
    public JSONObject systemTime2() throws Exception {
        JSONObject obj = new JSONObject();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", "/c", "$now = date; (($now)-($now).touniversaltime()).TotalMinutes");
        Process process = processBuilder.start();
		BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();

		while ((line = reader.readLine()) != null) {
            String[] strArray = line.split(":");
            if( strArray.length > 1 )
                obj.put(strArray[0].trim(), strArray[1] );
        }      
        logger.info( obj.toString() );
        return obj;
    }
}

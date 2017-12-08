package com.coin.karaoke.misook.util;

public class TimeUtil {
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
 
        // Convert total duration into time
           int hours = (int)(milliseconds / 3600000L);
           int minutes = (int)(milliseconds % 3600000L) / 60000;
           int seconds = (int) (milliseconds % 3600000L % 60000L / 1000L);
           // Add hours if there
           if(hours > 0){
               finalTimerString = hours + ":";
           }
 
           // Prepending 0 to seconds if it is one digit
           if(seconds < 10){
               secondsString = "0" + seconds;
           }else{
               secondsString = "" + seconds;}
 
           finalTimerString = finalTimerString + minutes + ":" + secondsString;
 
        // return timer string
        return finalTimerString;
    }
 
    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
    	Double percentage = (double) 0;
    	 
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
 
        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;
 
        // return percentage
        return percentage.intValue();
    }
 
    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = totalDuration / 1000;
        return 1000 * (int)(progress / 100.0D * currentDuration);
    }
    
    public static String formatSecondsAsDuration(long seconds) {
	    long hour = seconds / 60 / 60;
	    long min = (seconds / 60) - (hour * 60);
	    long sec = seconds - (min * 60) - (hour * 60 * 60);
	    return 
//	    		(hour < 10 ? "0" : "") + hour + ":" + 
	           (min < 10 ? "0" : "") + min + ":" +
	           (sec < 10 ? "0" : "") + sec;
	}
}

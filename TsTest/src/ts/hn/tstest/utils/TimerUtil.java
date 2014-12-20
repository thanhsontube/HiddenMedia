package ts.hn.tstest.utils;

public class TimerUtil {
    public static int getProgressPercentage(final long currentDuration, final long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static String milliSecondsToTimer(final int milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String strMins = "";
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        // Prepending 0 to mins if it is one digit
        if (minutes < 10) {
            strMins = "0" + minutes;
        } else {
            strMins = "" + minutes;
        }
        finalTimerString = finalTimerString + strMins + ":" + secondsString;

        return finalTimerString;
    }
    
    /**
     * Function to change progress to timer
     * @param progress 
     * @param totalDuration 
     * @return current duration in milliseconds
     */
    public static int progressToTimer(final int progress, final int totalDuration) {
        int currentDuration = 0;
        int duration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * duration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}

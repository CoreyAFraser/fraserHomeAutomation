import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FraserHomeAutomation {

    private static int hour;
    private static int minute;
    private static int second;

    public static void main( final String[] args ) {
        Calendar timeOfDay = Calendar.getInstance();
        timeOfDay.set(Calendar.HOUR_OF_DAY, 11);
        timeOfDay.set(Calendar.MINUTE, 0);
        timeOfDay.set(Calendar.SECOND, 0);

        hour = timeOfDay.get(Calendar.HOUR_OF_DAY);
        minute = timeOfDay.get(Calendar.MINUTE);
        second = timeOfDay.get(Calendar.SECOND);

        startTimer();
    }

    private static void startTimer() {

        Timer timer = new Timer();
        timer.schedule(new PorchListTimerTask(),
                0,
                24*60*60*1000);



//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                new PorchListTimerTask().run();
//                startTimer();
//            }
//        }, getNextRunTime());
    }


    private static Date getNextRunTime() {
        Calendar startTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, second);
        startTime.set(Calendar.MILLISECOND, 0);

        if (startTime.before(now) || startTime.equals(now)) {
            startTime.add(Calendar.DATE, 1);
        }

        Date startDate = startTime.getTime();
        System.out.println("Start Date: " + startDate);
        return startDate;
    }
}
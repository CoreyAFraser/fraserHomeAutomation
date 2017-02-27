import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FraserHomeAutomation {

    private int hour;
    private int minute;
    private int second;

    public void main(String[] args) {
        Calendar timeOfDay = Calendar.getInstance();
        timeOfDay.set(Calendar.HOUR_OF_DAY, 11);
        timeOfDay.set(Calendar.MINUTE, 0);
        timeOfDay.set(Calendar.SECOND, 0);

        hour = timeOfDay.get(Calendar.HOUR_OF_DAY);
        minute = timeOfDay.get(Calendar.MINUTE);
        second = timeOfDay.get(Calendar.SECOND);

        startTimer();
    }

    private void startTimer() {
        new Timer("Porch Lights Control", true).schedule(new TimerTask() {
            @Override
            public void run() {
                new PorchListTimerTask().run();
                startTimer();
            }
        }, getNextRunTime());
    }


    private Date getNextRunTime() {
        Calendar startTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, second);
        startTime.set(Calendar.MILLISECOND, 0);

        if (startTime.before(now) || startTime.equals(now)) {
            startTime.add(Calendar.DATE, 1);
        }

        return startTime.getTime();
    }
}
import java.util.Timer;

public class FraserHomeAutomation {

    public static void main( final String[] args ) {
        Timer timer = new Timer();
        timer.schedule(new PorchListTimerTask(),
                0,
                24*60*60*1000);
    }
}
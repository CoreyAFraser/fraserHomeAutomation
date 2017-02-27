import com.google.gson.Gson;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PorchListTimerTask extends TimerTask {

    public static final String IFTTT_KEY = "hZEOlVeS6uK5BjcSohNiI_GnxZvIMcqWHh7Sdz4efvO";

    private Timer turnPorchLightsOffTimer;
    private Timer turnPorchLightsOnTimer;

    public void run() {
        SunriseSunsetDTO sunriseSunsetResults = getSunsetTime();

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        Date sunset = fmt.parseDateTime(sunriseSunsetResults.getResults().getSunset()).toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(sunset);
        cal.add(Calendar.HOUR, -1);
        Date oneHourBeforeSunset = cal.getTime();

        Date sunrise = fmt.parseDateTime(sunriseSunsetResults.getResults().getSunset()).toDate();
        cal.setTime(sunrise);
        cal.add(Calendar.HOUR, 1);
        cal.add(Calendar.DATE, 1);
        Date oneHourAfterSunrise = cal.getTime();

        turnPorchLightsOffTimer = new Timer("Turn Porch Lights On", true);
        turnPorchLightsOffTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                turnPorchLightsOff();
            }
        }, oneHourBeforeSunset);

        turnPorchLightsOnTimer = new Timer("Turn Porch Lights On", true);
        turnPorchLightsOnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                turnPorchLightsOn();
            }
        }, oneHourAfterSunrise);
    }

    private void turnPorchLightsOn() {
        triggerIFTTTEvent("OutsideLightsOn");
        turnPorchLightsOnTimer.cancel();
        turnPorchLightsOnTimer = null;
    }

    private void turnPorchLightsOff() {
        triggerIFTTTEvent("OutsideLightsOff");
        turnPorchLightsOffTimer.cancel();
        turnPorchLightsOffTimer = null;
    }

    public SunriseSunsetDTO getSunsetTime() {
        try {
            String url = "http://api.sunrise-sunset.org/json?lat=41.9001&lng=-71.0898&formatted=0";
            String response = makeGetRequest(url);
            return new Gson().fromJson(response.toString(), SunriseSunsetDTO.class);
        } catch (Exception e) {
        }
        return null;
    }

    private void triggerIFTTTEvent(String event) {
        try {
            String url = "https://maker.ifttt.com/trigger/" + event + "/with/key/" + IFTTT_KEY;
            makeGetRequest(url);
        } catch (Exception e) {
        }
    }

    private String makeGetRequest(String url) {
        try {
            String USER_AGENT = "Mozilla/5.0";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
        }
        return "";
    }

}
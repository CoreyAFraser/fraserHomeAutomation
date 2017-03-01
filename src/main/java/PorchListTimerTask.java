import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class PorchListTimerTask extends TimerTask {

    private static final String IFTTT_KEY = "hZEOlVeS6uK5BjcSohNiI_GnxZvIMcqWHh7Sdz4efvO";
    private static final DateFormat ISO8601DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    private Date oneHourAfterSunrise;
    private Date oneHourBeforeSunset;

    public void run() {
        System.out.println();
        System.out.println();
        SunriseSunsetDTO sunriseSunsetResults = getSunriseSunsetTime();

        System.out.println("Sunset: " + sunriseSunsetResults.getResults().getSunset());
        System.out.println("Sunrise: " + sunriseSunsetResults.getResults().getSunrise());

        Date sunset = null;
        try {
            sunset = ISO8601DATE_FORMAT.parse(sunriseSunsetResults.getResults().getSunset());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date sunrise = null;
        try {
            sunrise = ISO8601DATE_FORMAT.parse(sunriseSunsetResults.getResults().getSunrise());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(sunset);
        cal.add(Calendar.HOUR, -1);
        oneHourBeforeSunset = cal.getTime();

        cal.setTime(sunrise);
        cal.add(Calendar.HOUR, 1);
        oneHourAfterSunrise = cal.getTime();

        setCurrentState(oneHourAfterSunrise, oneHourBeforeSunset);

        System.out.println("Schedule Lights Off At: " + oneHourBeforeSunset);
        Timer turnPorchLightsOffTimer = new Timer("Turn Porch Lights Off", true);
        turnPorchLightsOffTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                turnPorchLightsOff();
            }
        }, oneHourBeforeSunset);

        System.out.println("Schedule Lights On At: " + oneHourAfterSunrise);
        Timer turnPorchLightsOnTimer = new Timer("Turn Porch Lights On", true);
        turnPorchLightsOnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                turnPorchLightsOn();
            }
        }, oneHourAfterSunrise);
    }

    private void setCurrentState(Date oneHourAfterSunrise, Date oneHourBeforeSunset) {
        Calendar now = Calendar.getInstance();
        Calendar sunriseCalendar = Calendar.getInstance();
        sunriseCalendar.setTime(oneHourAfterSunrise);
        Calendar sunsetCalendar = Calendar.getInstance();
        sunsetCalendar.setTime(oneHourBeforeSunset);

        if ((now.equals(sunriseCalendar) || now.after(sunriseCalendar)) && (now.equals(sunsetCalendar) || now.before(sunsetCalendar))) {
            turnPorchLightsOff();
            sunriseCalendar.add(Calendar.DATE, 1);
            this.oneHourAfterSunrise = sunriseCalendar.getTime();
        } else {
            turnPorchLightsOn();
            if (now.after(sunsetCalendar)) {
                sunriseCalendar.add(Calendar.DATE, 1);
                this.oneHourAfterSunrise = sunriseCalendar.getTime();

                sunsetCalendar.add(Calendar.DATE, 1);
                this.oneHourBeforeSunset = sunsetCalendar.getTime();
            }
        }
    }

    private void turnPorchLightsOn() {
        Calendar now = Calendar.getInstance();
        System.out.println("Lights On At: " + now.getTime());
        triggerIFTTTEvent("OutsideLightsOn");
    }

    private void turnPorchLightsOff() {
        Calendar now = Calendar.getInstance();
        System.out.println("Lights Off At: " + now.getTime());
        triggerIFTTTEvent("OutsideLightsOff");
    }

    private SunriseSunsetDTO getSunriseSunsetTime() {
        String url = "http://api.sunrise-sunset.org/json?lat=41.9001&lng=-71.0898&formatted=0";
        return new Gson().fromJson(makeGetRequest(url), SunriseSunsetDTO.class);
    }

    private void triggerIFTTTEvent(String event) {
        System.out.println("IFTTT Event: " + event);
        String url = "https://maker.ifttt.com/trigger/" + event + "/with/key/" + IFTTT_KEY;
        makeGetRequest(url);
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
            System.out.println("API request Error: " + url);
        }
        return "";
    }
}

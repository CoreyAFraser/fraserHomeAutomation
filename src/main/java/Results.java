import com.google.gson.annotations.SerializedName;

class Results {

    @SerializedName("sunrise")
    private String sunrise;
    @SerializedName("sunset")
    private String sunset;

    String getSunrise() {
        return sunrise;
    }

    String getSunset() {
        return sunset;
    }

}
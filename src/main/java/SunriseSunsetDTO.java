
import com.google.gson.annotations.SerializedName;

class SunriseSunsetDTO {
        @SerializedName("results")
        private Results results;

        Results getResults() {
            return results;
        }
}

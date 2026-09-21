import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Сервис для получения официальных курсов валют (EUR, USD к молдавскому лею MDL)
// Лабораторная работа 5 - пункт a
public class CurrencyService {

    public enum Trend {
        UP,    // курс вырос 🟢
        DOWN,  // курс упал 🔴
        STABLE // курс стабилен ⚪
    }

    public static class CurrencyData {
        private final double eur;
        private final double usd;
        private final Trend eurTrend;
        private final Trend usdTrend;
        private final boolean isOnline;
        private final String time;

        public CurrencyData(double eur, double usd, Trend eurTrend, Trend usdTrend, boolean online) {
            this.eur = eur;
            this.usd = usd;
            this.eurTrend = eurTrend;
            this.usdTrend = usdTrend;
            this.isOnline = online;
            this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        public double getEur() { return eur; }
        public double getUsd() { return usd; }
        public Trend getEurTrend() { return eurTrend; }
        public Trend getUsdTrend() { return usdTrend; }
        public boolean isOnline() { return isOnline; }
        public String getTime() { return time; }

        @Override
        public String toString() {
            return String.format("EUR: %.2f MDL (%s), USD: %.2f MDL (%s)", eur, eurTrend, usd, usdTrend);
        }
    }

    private double lastEur = 19.35;
    private double lastUsd = 17.82;

    // Онлайн API курсов валют
    private static final String API_URL = "https://open.er-api.com/v6/latest/USD";

    public CurrencyData fetchRates() {
        try {
            URI uri = new URI(API_URL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
                reader.close();

                double mdlRate = extractNumber(json.toString(), "\"MDL\":");
                double eurRateUsd = extractNumber(json.toString(), "\"EUR\":");

                if (mdlRate > 0) {
                    double currentUsd = mdlRate;
                    double currentEur = eurRateUsd > 0 ? (mdlRate / eurRateUsd) : 19.35;

                    Trend eTrend = currentEur > lastEur ? Trend.UP : (currentEur < lastEur ? Trend.DOWN : Trend.STABLE);
                    Trend uTrend = currentUsd > lastUsd ? Trend.UP : (currentUsd < lastUsd ? Trend.DOWN : Trend.STABLE);

                    this.lastEur = currentEur;
                    this.lastUsd = currentUsd;

                    return new CurrencyData(currentEur, currentUsd, eTrend, uTrend, true);
                }
            }
        } catch (Exception e) {
            System.out.println("Курсы валют получены из локального кэша: " + e.getMessage());
        }

        return getFallbackRates();
    }

    private double extractNumber(String json, String key) {
        try {
            int idx = json.indexOf(key);
            if (idx == -1) return 0.0;
            int start = idx + key.length();
            while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == ':')) start++;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) end++;
            return Double.parseDouble(json.substring(start, end));
        } catch (Exception e) {
            return 0.0;
        }
    }

    public CurrencyData getFallbackRates() {
        return new CurrencyData(19.38, 17.85, Trend.UP, Trend.STABLE, false);
    }
}

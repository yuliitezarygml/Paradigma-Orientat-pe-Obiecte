import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Сервис для получения данных о погоде в реальном времени через онлайн API (Open-Meteo)
// Лабораторная работа 5 - пункт a
public class WeatherService {

    // Класс для хранения данных о погоде
    public static class WeatherData {
        private final double temperature;
        private final double windSpeed;
        private final int weatherCode;
        private final String conditionText;
        private final String iconType; // SUN, CLOUD, RAIN, SNOW
        private final boolean isOnline;
        private final String time;

        public WeatherData(double temp, double wind, int code, String condition, String icon, boolean online) {
            this.temperature = temp;
            this.windSpeed = wind;
            this.weatherCode = code;
            this.conditionText = condition;
            this.iconType = icon;
            this.isOnline = online;
            this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        public double getTemperature() { return temperature; }
        public double getWindSpeed() { return windSpeed; }
        public int getWeatherCode() { return weatherCode; }
        public String getConditionText() { return conditionText; }
        public String getIconType() { return iconType; }
        public boolean isOnline() { return isOnline; }
        public String getTime() { return time; }

        @Override
        public String toString() {
            return String.format("%.1f°C, %s (Vânt: %.1f km/h)", temperature, conditionText, windSpeed);
        }
    }

    // Координаты Кишинёва
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=47.01&longitude=28.86&current_weather=true";

    // Получить текущую погоду
    public WeatherData fetchWeather() {
        try {
            URI uri = new URI(API_URL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                return parseWeatherJson(json.toString(), true);
            }
        } catch (Exception e) {
            System.out.println("Нет связи с Open-Meteo, используется локальный режим: " + e.getMessage());
        }

        // Офлайн-режим (если нет интернета)
        return getFallbackWeather();
    }

    // Простой парсинг JSON без тяжелых сторонних библиотек (студенческий стиль)
    public static WeatherData parseWeatherJson(String json, boolean online) {
        double temp = extractNumber(json, "temperature");
        double wind = extractNumber(json, "windspeed");
        int code = (int) extractNumber(json, "weathercode");

        String condition;
        String iconType;

        if (code == 0) {
            condition = "Însorit (Солнечно)";
            iconType = "SUN";
        } else if (code <= 3) {
            condition = "Parțial înnorat (Облачно)";
            iconType = "CLOUD";
        } else if (code >= 51 && code <= 67) {
            condition = "Ploaie (Дождь)";
            iconType = "RAIN";
        } else if (code >= 71 && code <= 77) {
            condition = "Ninsoare (Снег)";
            iconType = "SNOW";
        } else {
            condition = "Variabil (Переменно)";
            iconType = "CLOUD";
        }

        return new WeatherData(temp, wind, code, condition, iconType, online);
    }

    private static double extractNumber(String json, String key) {
        try {
            int idx = json.indexOf("\"" + key + "\":");
            if (idx == -1) return 0.0;
            int start = idx + key.length() + 3;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) {
                end++;
            }
            return Double.parseDouble(json.substring(start, end));
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Резервные данные при отсутствии связи
    public static WeatherData getFallbackWeather() {
        return new WeatherData(21.4, 11.2, 0, "Însorit (Offline)", "SUN", false);
    }
}

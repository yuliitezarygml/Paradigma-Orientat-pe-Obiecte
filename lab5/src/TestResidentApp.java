import java.awt.*;
import java.io.File;

// Автоматические тесты для Лабораторной работы 5 (Aplicație Rezident)
public class TestResidentApp {

    private static int passed = 0;
    private static int total = 0;

    private static void assertTrue(String name, boolean condition) {
        total++;
        if (condition) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.err.println("  [FAIL] " + name);
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Запуск тестов Лабораторной работы 5 (Aplicație Rezident)");
        System.out.println("==================================================");

        testIpService();
        testWeatherParsing();
        testCurrencyLogic();
        testSettingsManager();
        testDynamicIconGeneration();
        testHotkeyState();

        System.out.println("==================================================");
        System.out.printf("Результат: %d / %d тестов успешно пройдено!\n", passed, total);
        if (passed == total) {
            System.out.println("ВСЕ ТЕСТЫ УСПЕШНО ПРОЙДЕНЫ!");
        } else {
            System.err.println("НЕКОТОРЫЕ ТЕСТЫ НЕ ПРОЙДЕНЫ!");
            System.exit(1);
        }
    }

    // 1. Тестирование парсинга погоды (пункт a)
    private static void testWeatherParsing() {
        System.out.println("\n1. Тестирование сервиса погоды (WeatherService):");

        String mockJson = "{\"current_weather\":{\"temperature\":23.5,\"windspeed\":14.2,\"weathercode\":0}}";
        WeatherService.WeatherData w1 = WeatherService.parseWeatherJson(mockJson, true);

        assertTrue("Температура распарсена верно (23.5°C)", Math.abs(w1.getTemperature() - 23.5) < 0.01);
        assertTrue("Скорость ветра верна (14.2 km/h)", Math.abs(w1.getWindSpeed() - 14.2) < 0.01);
        assertTrue("Код 0 соответствует солнцу (SUN)", "SUN".equals(w1.getIconType()));
        assertTrue("Статус онлайн = true", w1.isOnline());

        // Проверка кода дождя
        String rainJson = "{\"current_weather\":{\"temperature\":15.0,\"windspeed\":20.0,\"weathercode\":61}}";
        WeatherService.WeatherData w2 = WeatherService.parseWeatherJson(rainJson, true);
        assertTrue("Код 61 соответствует дождю (RAIN)", "RAIN".equals(w2.getIconType()));

        // Проверка офлайн-режима
        WeatherService.WeatherData fallback = WeatherService.getFallbackWeather();
        assertTrue("Резервные данные создаются", fallback != null && !fallback.isOnline());
    }

    // 2. Тестирование логики валют (пункт a)
    private static void testCurrencyLogic() {
        System.out.println("\n2. Тестирование курсов валют (CurrencyService):");
        CurrencyService cs = new CurrencyService();
        CurrencyService.CurrencyData data = cs.getFallbackRates();

        assertTrue("Курс EUR больше 0", data.getEur() > 0);
        assertTrue("Курс USD больше 0", data.getUsd() > 0);
        assertTrue("Тренд EUR определен", data.getEurTrend() != null);
        assertTrue("Тренд USD определен", data.getUsdTrend() != null);
    }

    // 3. Тестирование настроек и таймера (пункт c)
    private static void testSettingsManager() {
        System.out.println("\n3. Тестирование менеджера настроек (SettingsManager):");
        File file = new File("settings.txt");
        if (file.exists()) file.delete();

        SettingsManager sm = new SettingsManager();
        assertTrue("Интервал по умолчанию > 0", sm.getRefreshIntervalSeconds() > 0);

        sm.setRefreshIntervalSeconds(30);
        sm.setAutoRefresh(false);

        // Проверка сохранения и повторного чтения
        SettingsManager sm2 = new SettingsManager();
        assertTrue("Интервал 30 сек сохранен и прочитан", sm2.getRefreshIntervalSeconds() == 30);
        assertTrue("Флаг авто-обновления false сохранен", !sm2.isAutoRefresh());
    }

    // 4. Тестирование динамической генерации иконки для трея (пункт d)
    private static void testDynamicIconGeneration() {
        System.out.println("\n4. Тестирование динамической генерации иконки трея (TrayManager):");

        Image sunImg = TrayManager.createWeatherIcon("SUN", "+20°C");
        assertTrue("Иконка солнца создана", sunImg != null);
        assertTrue("Ширина иконки 32px", sunImg.getWidth(null) == 32);
        assertTrue("Высота иконки 32px", sunImg.getHeight(null) == 32);

        Image rainImg = TrayManager.createWeatherIcon("RAIN", "+12°C");
        assertTrue("Иконка дождя создана", rainImg != null);

        Image cloudImg = TrayManager.createWeatherIcon("CLOUD", "+18°C");
        assertTrue("Иконка облака создана", cloudImg != null);
    }

    // 5. Тестирование менеджера горячих клавиш (пункт e)
    private static void testHotkeyState() {
        System.out.println("\n5. Тестирование горячих клавиш (HotkeyManager):");
        HotkeyManager hm = new HotkeyManager(() -> {});
        assertTrue("Горячие клавиши изначально включены", hm.isEnabled());

        hm.setEnabled(false);
        assertTrue("Горячие клавиши можно выключить", !hm.isEnabled());
    }

    // 6. Тестирование определения публичного IP (IpService)
    private static void testIpService() {
        System.out.println("\n0. Тестирование сервиса определения публичного IP (IpService):");
        String mockJson = "{\"status\":\"success\",\"country\":\"Moldova\",\"city\":\"Chisinau\",\"lat\":47.0042,\"lon\":28.8574,\"query\":\"93.116.117.194\",\"isp\":\"MOLDTELECOM\"}";
        IpService.IpInfo info = IpService.parseIpJson(mockJson, true);

        assertTrue("Публичный IP распарсен верно", "93.116.117.194".equals(info.getIp()));
        assertTrue("Город распарсен верно", "Chisinau".equals(info.getCity()));
        assertTrue("Страна распарсена верно", "Moldova".equals(info.getCountry()));
        assertTrue("ISP распарсен верно", "MOLDTELECOM".equals(info.getIsp()));
        assertTrue("Широта (lat) распарсена", Math.abs(info.getLat() - 47.0042) < 0.001);
        assertTrue("Долгота (lon) распарсена", Math.abs(info.getLon() - 28.8574) < 0.001);
        assertTrue("Статус онлайн = true", info.isOnline());

        IpService.IpInfo fallback = IpService.getFallbackIp();
        assertTrue("Fallback IP содержит IP адрес", fallback.getIp() != null && !fallback.getIp().isEmpty());
    }
}

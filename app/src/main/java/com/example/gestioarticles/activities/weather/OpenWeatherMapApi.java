package com.example.gestioarticles.activities.weather;

public class OpenWeatherMapApi {

    public static final String URL_BASE = "http://api.openweathermap.org/";
    public static final String URL_BASE_API = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String URL_BASE_IMG = "http://openweathermap.org/img/wn/";
    public static final String API_KEY = "0bdefd036c7db93052f0e94b1ea5dd95";

    public static String getUrlWeather(String location, String lang) {
        return URL_BASE_API + location + "&lang=" + lang + "&units=metric&appid=" + API_KEY;
    }

    public static String getUrlIcon(String icon) {
        return URL_BASE_IMG + icon + "@2x.png";
    }

}

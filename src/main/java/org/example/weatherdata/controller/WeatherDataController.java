package org.example.weatherdata.controller;


import org.example.weatherdata.model.WeatherData;
import org.example.weatherdata.service.WeatherDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherDataController {

    @Autowired
    private WeatherDataService service;

    @GetMapping("/month/{year}/{month}")
    public List<WeatherData> getWeatherByMonth(@PathVariable int year, @PathVariable int month) {
        return service.getWeatherByMonth(year, month);
    }

    @GetMapping("/date/{year}/{month}/{day}")
    public List<WeatherData> getWeatherByDate(@PathVariable int year, @PathVariable int month, @PathVariable int day) {
        return service.getWeatherByDate(year, month, day);
    }

    @GetMapping("/stats/{year}/{month}")
    public WeatherDataService.MonthlyStats getMonthlyStats(@PathVariable int year, @PathVariable int month) {
        return service.getMonthlyTemperatureStats(year, month);
    }
}
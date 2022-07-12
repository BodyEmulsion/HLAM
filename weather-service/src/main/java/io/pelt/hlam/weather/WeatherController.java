package io.pelt.hlam.weather;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello, it's weather controller of weather-service";
    }
}

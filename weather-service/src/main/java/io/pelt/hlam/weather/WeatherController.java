package io.pelt.hlam.weather;

import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    @GetMapping("mirror")
    public String mirror(RequestEntity<String> request){
        return request.toString();
    }

    @GetMapping("hello")
    public String hello(RequestEntity<String> request) {
        return request.toString();
    }

    @GetMapping("privilege")
    public String privilege(RequestEntity<String> request) {
        return request.toString();
    }
}

package com.busgo.backend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cities")
public class CityController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCities() {
        return ResponseEntity.ok(Map.of("cities", List.of(
            Map.of("name", "Lagos", "code", "LOS", "state", "Lagos"),
            Map.of("name", "Abuja", "code", "ABV", "state", "FCT"),
            Map.of("name", "Port Harcourt", "code", "PHC", "state", "Rivers"),
            Map.of("name", "Kano", "code", "KAN", "state", "Kano"),
            Map.of("name", "Ibadan", "code", "IBD", "state", "Oyo")
        )));
    }
}

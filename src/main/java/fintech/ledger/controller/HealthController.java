package fintech.ledger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthController {

    @GetMapping("health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("component", "LedgerStream API");
        return ResponseEntity.ok(response);
    }

    @GetMapping("actuator/health")
    public ResponseEntity<Map<String, String>> actuatorHealth() {
        return health();
    }
}

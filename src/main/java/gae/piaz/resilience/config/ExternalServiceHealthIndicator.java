package gae.piaz.resilience.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private static final String EXTERNAL_SERVICE_HEALTH_STATUS_URL = "https://status.tiingo.com/summary.json";

    @Override
    public Health health() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(EXTERNAL_SERVICE_HEALTH_STATUS_URL, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode pageNode = root.path("page");
            String status = pageNode.path("status").asText();
            if ("UP".equalsIgnoreCase(status)) {
                log.info("External Service up and running.");
                return Health.up().withDetail("External Service", "Available").build();
            } else {
                return Health.down().withDetail("External Service", "Status: " + status).build();
            }
        } catch (RestClientException | NullPointerException | IllegalArgumentException e) {
            return Health.down().withDetail("External Service", "Error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Health.down().withDetail("External Service", "Unexpected error: " + e.getMessage()).build();
        }
    }

}
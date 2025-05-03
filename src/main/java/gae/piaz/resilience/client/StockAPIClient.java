package gae.piaz.resilience.client;

import java.util.List;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import gae.piaz.resilience.config.FuseBreaker;
import gae.piaz.resilience.dto.StockPriceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockAPIClient {

    private static final String STOCK_INFO_URL = "https://api.tiingo.com/tiingo/daily/%s/prices";

    private final RestTemplate restTemplate;

    @Retryable(
        value = Exception.class,
        maxAttempts = 4,
        backoff = @org.springframework.retry.annotation.Backoff(delay = 500)
    )
    @CircuitBreaker(
        maxAttempts = 4,
        openTimeout = 5000,
        resetTimeout = 20000
    )
    @FuseBreaker(
        numberOfAggregatedFailures = 3,
        method = "fuseBreakNotifier")
    public List<StockPriceDTO> fetchStockInfo(String symbol) {
        String url = String.format(STOCK_INFO_URL, symbol);
        ResponseEntity<List<StockPriceDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Recover
    public List<StockPriceDTO> recover(Exception ex, String symbol) {
        log.error("Failed to fetch stock info for {}", symbol, ex);
        throw new RuntimeException("Failed to fetch stock info after retries", ex);
    }

    public void fuseBreakNotifier() {
        log.error("Fuse breaker triggered");
        // the service is down! stop calling it, notify everyone.
        // disable the feature flag
        // send an email
    }

}

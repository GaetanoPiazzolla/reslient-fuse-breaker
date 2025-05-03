package gae.piaz.resilience.service;

import java.util.List;

import gae.piaz.resilience.client.StockAPIClient;
import gae.piaz.resilience.dto.StockPriceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockAPIClient stockApiClient;

    public List<StockPriceDTO> getStockInfo(String symbol) {
        log.info("Getting stock info for {}", symbol);
        return stockApiClient.fetchStockInfo(symbol);
    }

}

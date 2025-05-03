package gae.piaz.resilience.controller;

import java.util.List;

import gae.piaz.resilience.dto.StockPriceDTO;
import gae.piaz.resilience.service.StockService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{symbol}")
    public ResponseEntity<List<StockPriceDTO>> getStockInfo(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.getStockInfo(symbol));
    }

}

package gae.piaz.resilience.dto;

import java.time.OffsetDateTime;

public record StockPriceDTO(
    OffsetDateTime date,
    float open,
    float high,
    float low,
    float close,
    long volume,
    float adjOpen,
    float adjHigh,
    float adjLow,
    float adjClose,
    long adjVolume,
    float divCash,
    float splitFactor
) {}

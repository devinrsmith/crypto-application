package io.deephaven.coinbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingService;
import info.bitrich.xchangestream.coinbasepro.dto.CoinbaseProWebSocketTransaction;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;

public class What {

    private final ObjectMapper om;

    private void what(CoinbaseProStreamingService s) {

        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

        s.subscribeChannel("todo").map(this::adapt);
    }

    private CoinbaseProWebSocketTransaction adapt(JsonNode jn) throws JsonProcessingException {
        return om.treeToValue(jn, CoinbaseProWebSocketTransaction.class);
    }
}

package io.deephaven.coinbase;

import info.bitrich.xchangestream.coinbasepro.dto.CoinbaseProWebSocketTransaction;
import java.time.Instant;

public class Adapter {

  public static Match match(CoinbaseProWebSocketTransaction m) {
    return Match.newBuilder()
        .setTradeId(m.getTradeId())
        .setMakerOrderId(m.getMakerOrderId())
        .setTakerOrderId(m.getTakerOrderId())
        .setTime(Instant.parse(m.getTime()))
        .setProductId(m.getProductId())
        .setSize(m.getSize())
        .setPrice(m.getPrice())
        .setSide(Side.valueOf(m.getSide()))
        .build();
  }
}

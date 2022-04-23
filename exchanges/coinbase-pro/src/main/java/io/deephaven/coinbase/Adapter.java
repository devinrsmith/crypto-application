package io.deephaven.coinbase;

import info.bitrich.xchangestream.coinbasepro.dto.CoinbaseProWebSocketTransaction;
import org.apache.avro.util.Utf8;

import java.time.Instant;
import java.util.UUID;

public class Adapter {

  public static Match match(CoinbaseProWebSocketTransaction m) {
    return Match.newBuilder()
        .setTradeId(m.getTradeId())
        .setMakerOrderId(UUID.fromString(m.getMakerOrderId()))
        .setTakerOrderId(UUID.fromString(m.getTakerOrderId()))
        .setTime(Instant.parse(m.getTime()))
        .setProductId(new Utf8(m.getProductId()))
        .setSize(m.getSize())
        .setPrice(m.getPrice())
        .setSide(Side.valueOf(m.getSide()))
        .build();
  }
}

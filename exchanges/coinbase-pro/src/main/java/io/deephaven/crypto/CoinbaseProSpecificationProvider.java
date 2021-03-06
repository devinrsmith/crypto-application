package io.deephaven.crypto;

import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import java.util.Collections;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;

public class CoinbaseProSpecificationProvider implements SpecificationProvider {

  private static final String EXCHANGE_NAME = "coinbase-pro";

  @Override
  public List<Specification> specifications() {
    return Collections.singletonList(
        ImmutableSpecification.builder()
            .exchangeName(EXCHANGE_NAME)
            .streamingClass(CoinbaseProStreamingExchange.class)
            .addCurrencyPairs(CurrencyPair.BTC_USD)
            .addCurrencyPairs(CurrencyPair.ETH_USD)
            .addCurrencyPairs(CurrencyPair.DOGE_USD)
            .build());
  }
}

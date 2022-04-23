package io.deephaven.coinbase;

import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import io.deephaven.crypto.ImmutableSpecification;
import io.deephaven.crypto.Specification;
import io.deephaven.crypto.SpecificationProvider;
import java.util.Collections;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;

public class Provider implements SpecificationProvider {

  private static final String EXCHANGE_NAME = "coinbase-pro";
  public static final Specification SPEC =
      ImmutableSpecification.builder()
          .exchangeName(EXCHANGE_NAME)
          .streamingClass(CoinbaseProStreamingExchange.class)
          .addCurrencyPairs(CurrencyPair.BTC_USD)
          .addCurrencyPairs(CurrencyPair.ETH_USD)
          .addCurrencyPairs(CurrencyPair.DOGE_USD)
          .build();

  @Override
  public List<Specification> specifications() {
    return Collections.singletonList(SPEC);
  }
}

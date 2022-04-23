package io.deephaven.coinbase;

import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.coinbasepro.dto.CoinbaseProWebSocketTransaction;
import info.bitrich.xchangestream.core.ProductSubscription;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import io.reactivex.Observable;
import java.time.Instant;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.avro.util.Utf8;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.knowm.xchange.currency.CurrencyPair;

public class MatchPublisher {

  private final CoinbaseProStreamingExchange e;

  public MatchPublisher(CoinbaseProStreamingExchange e) {
    this.e = Objects.requireNonNull(e);
  }

  public Observable<CoinbaseProWebSocketTransaction> messages(CurrencyPair currencyPair) {
    // todo: have a way to return unfiltered (all pairs)
    return e.getStreamingMarketDataService().getRawWebSocketTransactions(currencyPair, true);
  }

  public Observable<Match> matches(CurrencyPair currencyPair) {
    // todo: have a way to return unfiltered (all pairs)
    return messages(currencyPair).map(MatchPublisher::adapt);
  }

  // todo: this is more efficient than what underlying is doing
  //    private CoinbaseProWebSocketTransaction adapt(JsonNode jn) throws JsonProcessingException {
  //        return om.treeToValue(jn, CoinbaseProWebSocketTransaction.class);
  //    }

  private static Match adapt(CoinbaseProWebSocketTransaction m) {
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

  public static void main(String[] args) {

    final CurrencyPair pair = CurrencyPair.BTC_USD;
    final String bootstrapServers = "localhost:9092";
    final String schemaRegistryUrl = "http://localhost:8081";

    CoinbaseProStreamingExchange e = new CoinbaseProStreamingExchange();
    e.applySpecification(e.getDefaultExchangeSpecification());

    if (!e.connect(ProductSubscription.create().addTrades(pair).build())
        .blockingAwait(10, TimeUnit.SECONDS)) {
      throw new RuntimeException();
    }

    KafkaProducer<String, Match> producer = createProducer(bootstrapServers, schemaRegistryUrl);
    MatchObserver observer = new MatchObserver(producer);
    new MatchPublisher(e).matches(pair).safeSubscribe(observer);
  }

  private static KafkaProducer<String, Match> createProducer(
      String bootstrapServers, String schemaRegistryUrl) {
    final Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    props.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        org.apache.kafka.common.serialization.StringSerializer.class);
    props.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        io.confluent.kafka.serializers.KafkaAvroSerializer.class);
    props.put(
        AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class);
    KafkaProducer<String, Match> producer = new KafkaProducer<>(props);
    return producer;
  }
}

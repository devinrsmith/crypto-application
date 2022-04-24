package io.deephaven.coinbase;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.util.Objects;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class MatchObserver implements Observer<Match>, Callback {

  private static final String TOPIC = "io.deephaven.coinbase";

  private final KafkaProducer<String, Match> producer;
  private Disposable disposable;

  public MatchObserver(KafkaProducer<String, Match> producer) {
    this.producer = Objects.requireNonNull(producer);
  }

  @Override
  public void onSubscribe(Disposable d) {
    disposable = d;
  }

  @Override
  public void onNext(Match match) {
    try {
      producer.send(new ProducerRecord<>(TOPIC, match.getProductId(), match), this);
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw new E("Unable to serialize " + match, e);
    }
  }

  @Override
  public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override
  public void onComplete() {}

  @Override
  public void onCompletion(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      exception.printStackTrace();
      disposable.dispose();
    }
  }

  private static class E extends RuntimeException {
    public E(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

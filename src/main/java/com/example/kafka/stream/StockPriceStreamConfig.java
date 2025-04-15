package com.example.kafka.stream;

import com.example.kafka.model.StockAverage;
import com.example.kafka.model.StockPrice;
import com.example.kafka.repository.StockAverageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

@Configuration
@EnableKafkaStreams
public class StockPriceStreamConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StockAverageRepository repository;

    public StockPriceStreamConfig(StockAverageRepository repository) {
        this.repository = repository;
    }

    @Bean
    public KafkaStreams kafkaStreams(StreamsBuilder builder) {
        // Ensure proper configuration for key serde
        Properties streamsConfig = new Properties();
        streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "stock-price-stream-app");
        streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // update if needed

        // Specify default key and value serdes globally
        streamsConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Define the stream topology
        KStream<String, String> inputStream = builder.stream("stock-prices", Consumed.with(Serdes.String(), Serdes.String()));

        inputStream
                .mapValues(value -> {
                    try {
                        return objectMapper.readValue(value, StockPrice.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter((key, value) -> value != null)
                .selectKey((key, value) -> value.getSymbol())
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
                .aggregate(
                        () -> new double[]{0.0, 0.0},
                        (key, value, agg) -> new double[]{agg[0] + value.getPrice(), agg[1] + 1},
                        Materialized.with(Serdes.String(), new DoubleArraySerde())
                )
                .toStream()
                .foreach((windowedKey, agg) -> {
                    double avg = agg[0] / agg[1];
                    String symbol = windowedKey.key();
                    Instant time = Instant.ofEpochMilli(windowedKey.window().end());

                    StockAverage average = new StockAverage();
                    average.setSymbol(symbol);
                    average.setAverage(avg);
                    average.setWindowTime(time);

                    repository.save(average);
                });

        // Start Kafka Streams application with the specified configuration
        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfig);
        streams.start();

        return streams;
    }

    // Custom Serde for double[]
    private static class DoubleArraySerde extends Serdes.WrapperSerde<double[]> {
        public DoubleArraySerde() {
            super(new Custom_Serialization(), new Custom_Deserialization());
        }
    }
}

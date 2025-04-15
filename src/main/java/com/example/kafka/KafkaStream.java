package com.example.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

public class KafkaStream {

    private static final String INPUT_TOPIC = "quickstart-events";
    private static final String OUTPUT_TOPIC = "output-topic";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Ensure input and output topics exist
        createTopicIfNotExists(INPUT_TOPIC, props);
        createTopicIfNotExists(OUTPUT_TOPIC, props);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream(INPUT_TOPIC);

        KTable<String, Long> wordCounts = textLines
                .flatMapValues(line -> Arrays.asList(line.toLowerCase().split(" ")))
                .groupBy((keyIgnored, word) -> word)
                .count();

        // Convert to stream, print each word count, and send to output topic
        wordCounts
                .toStream()
                .peek((key, value) -> System.out.println("Word: " + key + " Count: " + value))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static void createTopicIfNotExists(String topicName, Properties props) {
        try (AdminClient adminClient = AdminClient.create(props)) {
            if (!adminClient.listTopics().names().get().contains(topicName)) {
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
                System.out.println("Created topic: " + topicName);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to create topic " + topicName + ": " + e.getMessage());
        }
    }
}

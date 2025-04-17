package com.example.kafka.grafana_main_project;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;

import com.sun.net.httpserver.HttpServer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.Random;

public class KafkaProducerStockPrices {

    public static void main(String[] args) throws Exception {
        // Prometheus metrics registry
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Counter sentCounter = registry.counter("kafka_messages_sent_total");

        // HTTP server to expose /metrics
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/metrics", httpExchange -> {
            String response = registry.scrape();
            httpExchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.start();

        // Kafka producer config
        Properties props = new Properties();
        props.put("bootstrap.servers", "34.78.158.172:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        Random random = new Random();
        while (true) {
            String stock = "AAPL";
            double price = 100 + random.nextDouble() * 50;
            String value = stock + ":" + String.format("%.2f", price);
            producer.send(new ProducerRecord<>("stock-prices", stock, value));
            System.out.println("Sent: " + value);

            sentCounter.increment(); // track messages

            Thread.sleep(1000);
        }
    }
}

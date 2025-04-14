package com.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;

public class KafkaProducerStockPrices {
        public static void main(String[] args) throws Exception{
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
//            props.put("bootstrap.servers", "34.78.158.172:9092");
             props.put("acks", "all"); // Options: "0", "1", or "all"
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
                Thread.sleep(1000);
            }
        }
    }



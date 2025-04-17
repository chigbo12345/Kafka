package com.example.kafka.producer;
import com.example.kafka.model.StockPrice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StockPriceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private final String[] symbols = {"AAPL", "GOOGL", "AMZN", "MSFT"};


    public StockPriceProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 3000)
    public void sendPriceUpdate() throws Exception {
        String symbol = symbols[random.nextInt(symbols.length)];
        double price = 100 + (1000 * random.nextDouble());
        StockPrice stockPrice = new StockPrice();
        stockPrice.setSymbol(symbol);
        stockPrice.setPrice(price);



        kafkaTemplate.send("stock-prices", symbol, objectMapper.writeValueAsString(stockPrice));
        System.out.println("Sent: " + price);
        System.out.println("Sent: " + symbol);
    }

}

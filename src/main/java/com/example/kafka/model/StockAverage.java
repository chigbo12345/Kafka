package com.example.kafka.model;

import java.time.Instant;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class StockAverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private double average;
    private Instant windowTime;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }
    public Instant getWindowTime() { return windowTime; }
    public void setWindowTime(Instant windowTime) { this.windowTime = windowTime; }

}

package com.example.kafka.repository;

import com.example.kafka.model.StockAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAverageRepository extends JpaRepository<StockAverage, Long> {



}


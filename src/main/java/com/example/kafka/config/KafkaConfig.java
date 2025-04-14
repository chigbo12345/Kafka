package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic myTopic(){
        return TopicBuilder
                .name("newTopic")
                /** Incase you want to use partitions and replicas***/
                .partitions(10)
                .replicas(2)
                .build();


    }


}

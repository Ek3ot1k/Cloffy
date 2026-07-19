package ru.amin.Rest.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.amin.Rest.dto.LocationDTO;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, LocationDTO> consumerFactory(
            ObjectMapper objectMapper,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ){
        Map<String,Object> configProperties=new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG,"location-consumer-group");

        JsonDeserializer<LocationDTO> deserializer = new JsonDeserializer<>(LocationDTO.class, objectMapper);
        deserializer.addTrustedPackages("ru.amin.Rest.dto");

        return new DefaultKafkaConsumerFactory<>(
                configProperties,
                new StringDeserializer(),
                deserializer
        );

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,LocationDTO> kafkaListenerContainerFactory(
            ConsumerFactory<String,LocationDTO> consumerFactory
    ){
        ConcurrentKafkaListenerContainerFactory<String,LocationDTO> factory=new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}

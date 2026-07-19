package ru.amin.Rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.amin.Rest.dto.LocationDTO;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, LocationDTO> producerFactory(
            ObjectMapper objectMapper,@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers){
        Map<String,Object> configProperties=new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);

        JsonSerializer<LocationDTO> serializer=new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                new StringSerializer(),
                serializer);

    }

    @Bean
    public KafkaTemplate<String,LocationDTO> kafkaTemplate(
            ProducerFactory<String,LocationDTO> producerFactory){
        
        return new KafkaTemplate<>(producerFactory);
    }
}

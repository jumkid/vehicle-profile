package com.jumkid.vehicle.config;

import com.jumkid.vehicle.service.dto.Vehicle;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${com.jumkid.events.topic.vehicle-create}")
    private String kafkaTopicVehicleCreate;

    @Value("${com.jumkid.events.topic.vehicle-delete}")
    private String kafkaTopicVehicleDelete;

    @Bean
    public NewTopic topicVehicleCreate() {
        return TopicBuilder.name(kafkaTopicVehicleCreate).build();
    }

    @Bean
    public NewTopic topicVehicleDelete() {
        return TopicBuilder.name(kafkaTopicVehicleDelete).build();
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, Vehicle> vehicleProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 7000);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 1);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Vehicle> kafkaTemplate() {
        return new KafkaTemplate<>(vehicleProducerFactory());
    }

}

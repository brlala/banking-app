import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Apache Kafka - Building Kafka Consumers, Scalability and Pub/Sub
 */
public class Application {
    private static final String TOPIC = "events";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {
        String consumerGroup = "defaultConsumerGroup";
        if (args.length == 1) {
            consumerGroup = args[0];
        }

        System.out.println("Consumer is part of consumer group " + consumerGroup);

        Consumer<Long, String> kafkaConsumer = createKafkaConsumer(BOOTSTRAP_SERVERS, consumerGroup);

        consumeMessages(TOPIC, kafkaConsumer);
    }

    public static void consumeMessages(String topic, Consumer<Long, String> kafkaConsumer) {
        // takes a list of topic
        kafkaConsumer.subscribe(Collections.singletonList(topic));

        while (true) {
            // will return empty if poll time expires, if yes, it will return a batch of records
            ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

            if (consumerRecords.isEmpty()) {
                // do something else
            }

            for (ConsumerRecord<Long, String> record : consumerRecords) {
                System.out.printf("Received record (key: %d, value: %s, partition: %d, offset: %d%n",
                        record.key(), record.value(), record.partition(), record.offset());
            }
            // do something with the records

            // tell kafka that the messages have been successfully consumed
            kafkaConsumer.commitAsync();
        }
    }

    public static Consumer<Long, String> createKafkaConsumer(String bootstrapServers, String consumerGroup) {
        // Properties container for Kafka
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // deserializing method
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // set the group id property to the consumer group
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        // Good practice: Manually notify kafka that the message has been successfully consumed so it doesn't be sent twice in case it has been consumed but not processed
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(properties);
    }

}

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Apache Kafka - Kafka Producer with Java
 */
public class Application {
    private static final String TOPIC = "events";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {
        Producer<Long, String> kafkaProducer = createKafkaProducer(BOOTSTRAP_SERVERS);

        try {
            // create 10 messages to the event topics
            produceMessages(10, kafkaProducer);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // make sure all outstanding messages have been sent
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }

    public static void produceMessages(int numberOfMessages, Producer<Long, String> kafkaProducer) throws ExecutionException, InterruptedException {
        int partition = 1;

        for (int key = 0; key < numberOfMessages; key++) {
            String value = String.format("event %d", key);

            long timeStamp = System.currentTimeMillis();

            // How to publish, if a partition is not specified, it will hash the key by itself, if no key is specified(e.g. global event) it will be sent in a round robin fashion
            // ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, partition, timeStamp, (long) key, value);
            // ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, (long) key, value);  // not specifying key, partition is random
            ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, value);  // not specifying key, partition is random
            // Where the record has landed in our distributed kafka topic
            RecordMetadata recordMetadata = kafkaProducer.send(record).get();

            System.out.printf("Record with (key: %s, value: %s), was sent to (partition: %d, offset: %d%n",
                    record.key(), record.value(), recordMetadata.partition(), recordMetadata.offset());
        }
    }

    public static Producer<Long, String> createKafkaProducer(String bootstrapServers) {
        // Container to pass to Kafka producer
        Properties properties = new Properties();

        // Comma seperated list of Kafka addresses producer will use to establish the initial connection to the entire Kafka cluster
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // human-readable name for logs
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "events-producer");
        // Any Java object can be used as key value as long as they are serializable
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }

}

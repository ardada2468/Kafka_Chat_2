package org.example.KUtils;
import org.apache.kafka.clients.producer.*;

import org.example.Messeging.Message;
import org.example.Messeging.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Kafka Producer Class Wrapper
 */
public class KProducer {
    public static final String TOPIC = "Messages";
    private static final Logger log = LoggerFactory.getLogger(KProducer.class);
    private KafkaProducer<String, String> producer;

    /**
     * Initializes the Kafka Producer
     */
    public KProducer() {
        try {
            Properties properties = loadConfig("./client.properties");
            producer = new KafkaProducer<>(properties);
            log.info(producer.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a message with the producer
     * @param s Message body
     * @param user User Object that is sending the messege
     */
    public void send(String s, User user){
        Message m = new Message(s, user.getName());
        sendMessageKafka(TOPIC, user.getName(), m.toString());
    }

    /**
     * Sends a messege with the producer
     * @param m Messege Object to be sent
     */
    public void send(Message m){
        sendMessageKafka(TOPIC, m.getFrom(), m.toString());
    }

    /**
     * Sends a message with Producer
     * @param Topic Kafka Topic on server
     * @param Key Key
     * @param Value value
     */
    private void sendMessageKafka(String Topic, String Key, String Value) {
        try {
            // Create a ProducerRecord with the topic, key, and value
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(Topic, Key, Value);

            // Send the record and get a Future for tracking the result
            Future<RecordMetadata> sendResult = producer.send(producerRecord);

            // You can use the Future to wait for the result if needed
            RecordMetadata metadata = sendResult.get();
            log.info("Message sent successfully. Topic: {}, Partition: {}, Offset: {}",
                    metadata.topic(), metadata.partition(), metadata.offset());

        } catch (Exception e) {
            log.error("Error sending message", e);
        } finally {
            // Close the producer when done
        }
    }


    /**
     * Loads the properties for the producer
     * @param configFile Config file path
     * @return Properties for Kafka Producer
     * @throws IOException In case file is not read
     */

    public static Properties loadConfig(final String configFile) throws IOException {
        //Loads the config file
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        cfg.put("key.serializer", StringSerializer.class.getName());
        cfg.put("value.serializer", StringSerializer.class.getName());
        return cfg;
    }



}

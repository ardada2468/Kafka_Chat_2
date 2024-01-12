package org.example.KUtils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.Messeging.Message;
import org.example.Messeging.User;
import org.example.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class KConsumer {

    KafkaConsumer<String, String> consumer;
    private final AtomicBoolean shutdown;
    private final CountDownLatch counter;
    private static final Logger log = LoggerFactory.getLogger(KConsumer.class);
    private User user;

    private static int id = 0;

    public KConsumer(String User) {
       try{
           Properties properties = loadConfig("./client.properties");
           consumer = new KafkaConsumer<String, String>(properties);
           log.info(consumer.toString());
       } catch (IOException e) {
           throw new RuntimeException(e);
       }finally {
           shutdown = new AtomicBoolean(false);
           counter = new CountDownLatch(1);
           user = new User(User);
       }
    }

    public Message recordToMessage(ConsumerRecord<String, String> record){
        Message m =  new Message(record.value(), record.key(), record.timestamp());
        return m;
    }

    public void run(UserInterface userInterface){
        try {
            consumer.subscribe(Collections.singleton(KProducer.TOPIC));
            while (!shutdown.get()){
                ConsumerRecords<String, String> records = consumer.poll(500);
                records.forEach(record -> {
                    Message m = recordToMessage(record);
                    log.info(m.getContent());
                        String myMessege = m.getTimestamp() + " " + m.getFrom() + "\t\t| " + m.getContent() + "\n";
                        userInterface.appendToChat(myMessege);

                });
            }
        }finally {
            consumer.close();
            counter.countDown();
        }
    }



    public static Properties loadConfig(final String configFile) throws IOException {
        //Loads the config file
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        cfg.put("key.deserializer", StringDeserializer.class.getName());
        cfg.put("value.deserializer", StringDeserializer.class.getName());
//        cfg.put("group.id", String.valueOf((int) Math.random()*1000000*System.currentTimeMillis()));
        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-java-getting-started" + String.valueOf(System.currentTimeMillis()));
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return cfg;
    }
}

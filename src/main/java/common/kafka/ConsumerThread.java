package common.kafka;

import client.ClientApplication;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerThread extends Thread{
    private String topicName;
    private KafkaConsumer<String,String> kafkaConsumer;
    private KafkaTopic topic;
    private String groupId;

    public ConsumerThread(String topicName, KafkaTopic topic){
        this.topicName = topicName;
        this.topic = topic;
        this.groupId="1";
    }

    public ConsumerThread(String topicName, KafkaTopic topic, String groupId){
        this.topicName = topicName;
        this.topic = topic;
        this.groupId=groupId;
    }

    public void run() {
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        //Figure out where to start processing messages from
        kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        //Start processing messages
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records)
                {
                    if(!record.value().startsWith(ClientApplication.name+":"))
                        if(topic.isActive())
                            System.out.println(record.value());
                        else {
                            if(!ClientApplication.name.equals("")) {
                                System.out.println("new message received.");
                                topic.addUnreadMessage(record.value());
                            }
                        }
                }
            }
        }catch(WakeupException ex){
            System.out.println("Exception caught " + ex.getMessage());
        }finally{
            kafkaConsumer.close();
            System.out.println("After closing KafkaConsumer");
        }
    }
    public KafkaConsumer<String,String> getKafkaConsumer(){
        return this.kafkaConsumer;
    }
}

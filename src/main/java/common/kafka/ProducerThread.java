package common.kafka;

import client.ScannerThread;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Scanner;

public class ProducerThread extends Thread {

    private static Scanner in;
    private String topicName;
    private ScannerThread scannerThread;

    public ProducerThread(String topicName, ScannerThread scannerThread) {
        this.topicName = topicName;
        this.scannerThread = scannerThread;
    }

    public void run() {

        in = new Scanner(System.in);
        System.out.println("Enter message(type exit to quit)");

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
        String line = in.nextLine();
        while (true) {
            if (line.equals("/exit")) {
                scannerThread.setState(ScannerThread.clientState.Idle);
                System.out.println("You have left the conversation.");
                this.suspend();

            } else if (line.equals("/help")) {
                String availableCommands = "/exit\n/help";
                System.out.println("Available commands: \n"+availableCommands);

            } else {
                ProducerRecord<String, String> rec = new ProducerRecord<String, String>(this.topicName, line);
                producer.send(rec);
            }

            line = in.nextLine();
        }
    }

}

package client;

import common.kafka.KafkaTopic;
import common.models.ServerModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import static common.ConnectionConstants.pingingPort;

public class ClientApplication {
    private static final int expectedArgs = 1;
    private static final String expectedArgsMeanings = "<ip>";

    private static ConcurrentLinkedQueue<KafkaTopic> topics = new ConcurrentLinkedQueue<>();

    private static ServerModel server;

    public static String name = "";
    private static String availableCommands = "/list-list all users \n" +
            "/help -display this \n" +
            "/name -display user name\n" +
            "/msg -create a private chat or join an existing one\n" +
            "/msg everyone -open the global chat room\n"+
            "/mymessages -display unread messages from topics\n"+
            "/mymessages-a -display all topics in which the user is connected\n";

    public static ConcurrentLinkedQueue<KafkaTopic> getTopics() {
        return topics;
    }

    public static void main(String[] args) {
        checkArguments(args);
        printWelcome(args);
        //
        try {
            server = new ServerModel(args[0]); //constructor also creates the communication socket
        } catch (IOException e) {
            System.out.println("Failed to create communication connection to server.");
            return;
        }

        PingerThread connectionChecker = new PingerThread(server);
        connectionChecker.start();
        System.out.println("Connected to server successfully. Please select a nickname.");

        BufferedReader systemInReader = new BufferedReader(new InputStreamReader(System.in));//stdin reader
        ScannerThread scannerThread = new ScannerThread(server.getReader());
        scannerThread.start();

        while (connectionChecker.isAlive()) {

            if (scannerThread.getCurrentState() == ScannerThread.clientState.WaitingForRoom ||
                    scannerThread.getCurrentState() == ScannerThread.clientState.InConversation)
                continue;

//            System.out.println("Current state is " + scannerThread.getCurrentState());

            String readText = null;
            try {
                readText = systemInReader.readLine();
                if(readText.trim().equals(""))
                    continue;
                //local commands
                if (readText.equals("/help")) {
                    System.out.println("Available commands: \n" + availableCommands);
                    continue;
                }
                if (readText.equals("/name")) {
                    System.out.println("Your name is: " + name);
                    continue;
                }
                if (readText.startsWith("/mymessages")) {
                    if(readText.contains("-a")) {
                        if (topics.stream().findAny().isPresent()) {
                            topics.stream()
                                    .forEachOrdered(topic -> System.out.println(topic.getConnectedUserName() + " -> " + topic.getUnreadMessageCount() + " new messages"));

                        } else
                            System.out.println("You have no open topics.");
                    }
                    else {
                        ArrayList<KafkaTopic> reversedTopics = new ArrayList<>(topics);
                        //Collections.reverse(reversedTopics);
                        if (reversedTopics.stream()
                                .filter(topic -> topic.getUnreadMessageCount() > 0).findAny().isPresent()) {
                            topics.stream()
                                    .filter(topic -> topic.getUnreadMessageCount() > 0)
                                    .forEachOrdered(topic -> System.out.println(topic.getConnectedUserName() + " -> " + topic.getUnreadMessageCount() + " new messages"));

                        } else
                            System.out.println("You have no new messages.");
                    }
                    continue;
                }

                scannerThread.setLastRequest(readText);
                //server commands
                if (readText.equals("/list")) {
                    scannerThread.setState(ScannerThread.clientState.WaitingForList);
                } else if (readText.startsWith("/msg ")) {
                    final String userToConnect = readText.substring(5);
                    if (userToConnect.equals("everyone")) {
                        Optional<KafkaTopic> everyoneTopic = topics.stream().filter(topic -> topic.getConnectedUserName().equals("everyone")).findAny();
                        if (everyoneTopic.isPresent()) {
                            KafkaTopic topic = everyoneTopic.get();
                            topic.setActive(true);
                            topic.printUnreadMessages();
                            if (topic.getPt().isAlive())
                                topic.getPt().resume();
                            else
                                topic.getPt().start();
                            scannerThread.setState(ScannerThread.clientState.InConversation);
                        } else {
                            System.out.println("General topic not found!");
                        }
                        continue;
                    }

                    Optional<KafkaTopic> topic = topics.stream().filter(kafkaTopic -> kafkaTopic.getConnectedUserName().equals(userToConnect)).findAny();
                    if (topic.isPresent()) {
                        topic.get().printUnreadMessages();
                        if (topic.get().getPt().isAlive())
                            topic.get().getPt().resume();
                        else
                            topic.get().getPt().start();
                        scannerThread.setState(ScannerThread.clientState.InConversation);
                        continue;
                    } else {
                        scannerThread.setState(ScannerThread.clientState.WaitingForRoom);
                    }


                }


            } catch (IOException e) {
                System.out.println("Could not read from stdin");
                e.printStackTrace();
            }
//            System.out.println("****sent to server:"+readText+"****");
            server.getWriter().println(readText);
            server.getWriter().flush();

        }

    }

    private static void checkArguments(String[] args) {
        if (args.length < expectedArgs) {
            System.out.println("expected arguments: " + expectedArgsMeanings);
            throw new IllegalArgumentException("Illegal launch");
        }
    }

    private static void printWelcome(String[] args) {
        System.out.println("Welcome! Attempting connection to server at " + args[0] + " on port " + pingingPort);
    }


}

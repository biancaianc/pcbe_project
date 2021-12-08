package client;

import common.kafka.KafkaTopic;
import common.models.ServerModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static common.ConnectionConstants.pingingPort;

public class ClientApplication {
    private static final int expectedArgs = 1;
    private static final String expectedArgsMeanings = "<ip>";

    private static List<KafkaTopic> topics = new ArrayList<>();

    private static ServerModel server;

    public static String name="";
    private static String availableCommands="/list-list all users \n/help-display this \n/name-display user name\n/msg-create a private chat";

    public static List<KafkaTopic> getTopics() {
        return topics;
    }

    public static void main(String[] args) {
        checkArguments(args);
        printWelcome(args);

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

        while(connectionChecker.isAlive()) {

            if(scannerThread.getCurrentState() == ScannerThread.clientState.WaitingForRoom ||
                    scannerThread.getCurrentState() == ScannerThread.clientState.InConversation)
                continue;

//            System.out.println("Current state is " + scannerThread.getCurrentState());

            String readText = null;
            try {
                readText = systemInReader.readLine();
                //local commands
                if(readText.equals("/help")){
                    System.out.println("Available commands: \n"+availableCommands);
                    continue;
                }
                if(readText.equals("/name")){
                    System.out.println("Your name is: " + name );
                    continue;
                }
                if(readText.equals("/mymessages")){
                    topics.stream()
                            .filter(topic->topic.getUnreadMessageCount()>0)
                            .forEachOrdered(topic-> System.out.println(topic.getConnectedUserName()+" -> "+topic.getUnreadMessageCount()+" new messages"));
                    continue;
                }

                scannerThread.setLastRequest(readText);
                //server commands
                if(readText.equals("/list")){
                    scannerThread.setState(ScannerThread.clientState.WaitingForList);
                } else if(readText.startsWith("/msg ")){
                    final String userToConnect = readText.substring(5);
                    Optional<KafkaTopic> topic = topics.stream().filter(kafkaTopic -> kafkaTopic.getConnectedUserName().equals(userToConnect)).findAny();
                    if(topic.isPresent()) {
                        topic.get().printUnreadMessages();
                        if (topic.get().getPt().isAlive())
                            topic.get().getPt().resume();
                        else
                            topic.get().getPt().start();
                        scannerThread.setState(ScannerThread.clientState.InConversation);
                        continue;
                    }
                    else {
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
        if(args.length < expectedArgs) {
            System.out.println("expected arguments: " + expectedArgsMeanings);
            throw new IllegalArgumentException("Illegal launch");
        }
    }

    private static void printWelcome(String[] args) {
        System.out.println("Welcome! Attempting connection to server at "+args[0]+" on port "+ pingingPort);
    }




}

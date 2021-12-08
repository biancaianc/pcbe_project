package client;

import common.models.ServerModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static common.ConnectionConstants.pingingPort;

public class ClientApplication {
    private static final int expectedArgs = 1;
    private static final String expectedArgsMeanings = "<ip>";

    private static ServerModel server;
    public static String name="";
    private static String availableCommands="/list-list all users \n/help-display this \n/name-display user name\n/msg-create a private chat";


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

            System.out.println("Current state is " + scannerThread.getCurrentState());

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

                scannerThread.setLastRequest(readText);
                //server commands
                if(readText.equals("/list")){
                    scannerThread.setState(ScannerThread.clientState.WaitingForList);
                } else if(readText.startsWith("/msg ")){
                    scannerThread.setState(ScannerThread.clientState.WaitingForRoom);
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

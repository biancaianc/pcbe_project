package client;

import common.models.ServerModel;

import java.io.IOException;
import java.net.Socket;

import static common.ConnectionConstants.pingingPort;

public class ClientApplication {
    private static final int expectedArgs = 2;
    private static final String expectedArgsMeanings = "<ip>";

    private static ServerModel server;

    public static void main(String[] args) {
        checkArguments(args);
        printWelcome(args);

        PingerThread connectionChecker = new PingerThread(server);
        connectionChecker.start();

        if (!createConnection(args)) return;


        while(connectionChecker.isAlive()) {
            //do stuff
        }
    }

    private static void checkArguments(String[] args) {
        if(args.length < expectedArgs) {
            System.out.println("usage: ./" + args[0] + " " + expectedArgsMeanings);
            throw new IllegalArgumentException("Illegal launch");
        }
    }

    private static void printWelcome(String[] args) {
        System.out.println("Welcome! Attempting connection to server at "+args[1]+" on port "+ pingingPort);
    }

    private static boolean createConnection(String[] args) {
        try {
            server = new ServerModel(args[1]);
        } catch (IOException e) {
            System.out.println("failed to create communication connection to server.");
            return false;
        }
        return true;
    }




}

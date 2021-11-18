package client;

import common.models.ServerModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static common.ConnectionConstants.pingingPort;

public class ClientApplication {
    private static final int expectedArgs = 1;
    private static final String expectedArgsMeanings = "<ip>";

    private static ServerModel server;
    private static String name="";
    private static String availableCommands="/list-list all users \n/help-display this";


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

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));//stdin reader

        while(connectionChecker.isAlive()) {

            System.out.print(name + ": ");
            String str = null;
            try {
                str = br.readLine();
                if(str.equals("/help")){

                    System.out.println("Available commands: \n"+availableCommands);
                    continue;
                }
            } catch (IOException e) {
                System.out.println("Could not read from stdin");
                e.printStackTrace();
            }

            server.getWriter().println(str);
            server.getWriter().flush();
            try {
                String resp = server.getReader().readLine();
                if(resp.contains("Nickname") && resp.contains("success")) {
                    name = str;
                    System.out.println("Type /help for help");
                }
                System.out.println(resp);

            } catch (IOException e) {
                System.out.println("Could not read response from server. Make sure you do not have two instances of the client running.");
                e.printStackTrace();
            }
            //do stuff
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

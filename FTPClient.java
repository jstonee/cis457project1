import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;


public class FTPClient {
    private static Socket ControlSocket;

    public static void main(String argv[]) throws Exception {
        String input;
        System.out.println("Type connect <ip> <port>");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter a command: ");
        input = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(input);

        if (input.startsWith("connect")) {
            String serverName = tokens.nextToken(); // connect
            serverName = tokens.nextToken(); // serverIP
            int port = Integer.parseInt(tokens.nextToken()); // port
            System.out.println("Connecting to " + serverName + ":" + port);
            try {
                ControlSocket = new Socket(serverName, port);
            } catch (IOException ioEx) {
                System.out.println("Unable to connect to " + serverName + ":" + port);
                System.exit(1);
            }
            while (true) {
                DataOutputStream toServer = new DataOutputStream(ControlSocket.getOutputStream());
                DataInputStream fromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
                System.out.print("Enter a command: ");
                input = inFromUser.readLine();

                if (input.equals("list:")) {
                    int port1 = port + 2;
                    ServerSocket welcomeData = new ServerSocket(port1);
                    toServer.writeBytes(port1 + " " + input + " " + '\n');

                    Socket dataSocket = welcomeData.accept();
                    System.out.println("Server sending list data");
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    dataSocket.close();
                } else if (input.startsWith("retr")) {
                    tokens = new StringTokenizer(input);
                    String file = tokens.nextToken();
                    file = tokens.nextToken();
                    System.out.println("Requesting " + file + " from server");
                } else if (input.startsWith("stor")) {
                    tokens = new StringTokenizer(input);
                    String file = tokens.nextToken();
                    file = tokens.nextToken();
                    System.out.println("Storing " + file + " from server");
                } else if (input.equals("close")) {
                    System.out.println("Closing Control Socket");
                    toServer.writeBytes(0 + " " + input + " " + '\n');
                    ControlSocket.close();
                    System.out.println("Exiting");
                    System.exit(0);
                } else {
                    System.out.println("Invalid command");
                }
            }

        } else {
            System.out.println("You must connect to a server");
            System.exit(1);
        }

    }
}

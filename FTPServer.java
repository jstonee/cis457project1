import java.io.*; 
import java.net.*;
import java.util.*;

public final class FTPServer {
    private static ServerSocket welcomeSocket;
    public static void main(String argv[]) throws Exception {

	String fromClient;
	String clientCommand;
	byte[] data;
	try {
	    welcomeSocket = new ServerSocket(5568);
	} catch(IOException ioEx) {
	    System.out.println("Unable to set up port!");
	    System.exit(1);
	}
	String frstln;
        
	while(true)
            {
		System.out.println("Waiting for a connection...");
                Socket connectionSocket = welcomeSocket.accept();
                
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		System.out.println("Client accepted.");
		
		fromClient = inFromClient.readLine();
                
		StringTokenizer tokens = new StringTokenizer(fromClient);
		
		frstln = tokens.nextToken();
		int port = Integer.parseInt(frstln);
		System.out.println(port);
		clientCommand = tokens.nextToken();
                
		if(clientCommand.equals("list:"))
		    { 
			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			System.out.println("Data Socket opened.");
			DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
			File folder = new File(".");
			File[] listOfFiles = folder.listFiles();
			String temp;
			for (int i=0; i < listOfFiles.length; i++) {
			    if (listOfFiles[i].isFile()) {
				//System.out.println("File " + listofFiles[i].getName());
				//data.parseByte(listOfFiles[i].getName());
				temp = listOfFiles[i].getName();
				data = temp.getBytes();
				dataOutToClient.write(data, 0, data.length);
			    }    
			}	
			//dataOutToClient.write(data, 0, data.length);
			dataSocket.close();
			System.out.println("Data Socket closed.");
		    }
	    }
    }
}

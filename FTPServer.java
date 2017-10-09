import java.io.*; 
import java.net.*;
import java.util.*;

public final class FTPServer {
    public static void main(String argv[]) throws Exception {

	String fromClient;
	String clientCommand;
	byte[] data;
	
        try {
	ServerSocket welcomeSocket = new ServerSocket(5568);
	} catch (IOException ioEx) {
	    System.out.println("Unable to set up port.");
	    System.exit(1);
	}
	String frstln;
        
	while(true)
            {
                Socket connectionSocket = welcomeSocket.accept();
                
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		fromClient = inFromClient.readLine();
                
		StringTokenizer tokens = new StringTokenizer(fromClient);
		
		frstln = tokens.nextToken();
		int port = Integer.parseInt(frstln);
		System.out.println(port);
		clientCommand = tokens.nextToken();
                
		if(clientCommand.equals("list:"))
		    { 
			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
			File folder = new File(".");
			File[] listOfFile = folder.listFiles();
			for (int i=0; i < listOfFiles.length; i++) {
			    if (listofFiles[i].isFile()) {
				//System.out.println("File " + listofFiles[i].getName());
				data.parseByte(listOfFiles[i].getName());
				dataOutToClient.write(data, 0, data.length);
			    }
			    
			}		
		    }
		
		dataSocket.close();
		System.out.println("Data Socket closed");
	    }
        
	// ......................
            
	    
	//if(clientCommand.equals("retr:"))
	//  {
		// ..............................
		// ..............................
	//  }
	
    }
}

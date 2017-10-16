import java.io.*; 
import java.net.*;
import java.util.*;

public final class FTPServer {

    private static ServerSocket welcomeSocket;

    public static void main(String argv[]) throws Exception {

		try {
			welcomeSocket = new ServerSocket(5568);
		} catch (IOException ioEx) {
			System.out.println("Unable to set up port!");
			System.exit(1);
		}

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			System.out.println("Connected to " + connectionSocket.getRemoteSocketAddress().toString());

			// Create ClientHandler thread to handle client
			ClientHandler handler = new ClientHandler(connectionSocket);
			handler.start();
		}
	}
}

class ClientHandler extends Thread {

	private Socket clientSocket;
	private Scanner input;

	public ClientHandler(Socket socket) {
		//Set up reference to associated socket
		clientSocket = socket;

		try
		{
			input = new Scanner(clientSocket.getInputStream());
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
	}

	public void run() {
		String fromClient;
		String clientCommand;
		byte[] data;
		String frstln;

		do {
			// read in initial command line from client
			fromClient = input.nextLine();

			StringTokenizer tokens = new StringTokenizer(fromClient);

			frstln = tokens.nextToken();
			int port = Integer.parseInt(frstln);
			clientCommand = tokens.nextToken();

			//if the command is "close", end this thread
			if(clientCommand.equals("close")){
				endConnection();
				return;
			}

			try {
				if (clientCommand.equals("list:")) {
					// connect to client's Data Socket
					Socket dataSocket = new Socket(clientSocket.getInetAddress(), port);
					DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
					// get local files
					File folder = new File(".");
					File[] listOfFiles = folder.listFiles();
					String temp;
					// get data for each file
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							temp = listOfFiles[i].getName() + '\n';
							data = temp.getBytes();
							dataOutToClient.write(data, 0, data.length);
						}
					}
					dataOutToClient.close();
					dataSocket.close();
				}
				else if(clientCommand.equals("stor")) {
				    clientCommand = tokens.nextToken();
				    Socket dataSocket = new Socket(clientSocket.getInetAddress(), port);
				    BufferedInputStream dataFromClient = new BufferedInputStream(new DataInputStream(dataSocket.getInputStream()));
				    FileOutputStream file = new FileOutputStream(new File(clientCommand));
				    byte[] buffer = new byte[8192];
				    int count;
				    while((count = dataFromClient.read(buffer)) > 0) {
					file.write(buffer, 0, count);
				    }
				    file.close();
				    dataFromClient.close();
				    dataSocket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} while (true);
	}

	/**
	 * Closes the thread
	 */
	private void endConnection() {
		System.out.println("Disconnecting from client "+clientSocket.getRemoteSocketAddress().toString());
		input.close();
		try {
			clientSocket.close();
		} catch(IOException ioEx) {
			System.out.println("Unable to disconnect!");
		}
		System.out.println("Disconnected from client");
	}
}

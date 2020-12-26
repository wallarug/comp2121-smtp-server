//import java.net.ServerSocket;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;


//import java.io.IOException;
//import java.net.SocketTimeoutException;
import java.net.*;
import java.io.*;



public class MySMTPServer extends Thread{
	String IPADDRESS = "192.168.12.10";
	String PORT = "5000";
	private ServerSocket serverSocket;
		
	
	public MySMTPServer(String ip, int port) throws IOException{
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}
	
	
	// method used by threads
	public void run(){
		
		while(true){
			try{
				// wait for an incoming connection on port 25...
				Socket server = serverSocket.accept();
				
				
				System.out.println("Just connected to "+ server.getRemoteSocketAddress());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
		        out.writeUTF("G'day, I'm " + server.getLocalSocketAddress());
		        
		        server.close();
				
			}catch(SocketTimeoutException s){
				System.out.println("Socket timed out!");
	            break;
			}catch(IOException e){
				e.printStackTrace();
	            break;
			}
		}
	}
	
	public void SMTPSession(){
		
	}
		
	public void BuildLists(){
		List<String> Commands = new ArrayList<String>();
		Map<Integer, String> ErrorReps = new HashMap<Integer, String>();
		
		// Accepted commands for the server...
		Commands.add("HELO");
		Commands.add("MAIL");
		Commands.add("RCPT");
		Commands.add("DATA");
		Commands.add("QUIT");
		Commands.add("\n.");
		
		// Error Codes will message...
		ErrorReps.put(250, "OK");
		ErrorReps.put(500, "Command not recognised");
		ErrorReps.put(214, "Response to help command");
		ErrorReps.put(221, "bye");
		ErrorReps.put(354, "bye");
		
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// get the server settings (MySMTPServer.java <ip address> <port>
		int PORT = Integer.parseInt(args[1]);
		String IPADDR = args[0];
		
		try{
			Thread t = new MySMTPServer(IPADDR, PORT);
			t.start();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}

class SMTPSession extends Thread {
	// Declare the global variables for the session
	Socket conn;
	int currentState;
	int[][] stateMachine = new int[5][5];
	Map<String, Integer> commands = new HashMap<String, Integer>(5);
	DataOutputStream outputStream;
	DataInputStream inputStream;
	
	/*
	 * 	Initializes a new SMTP Session
	 */
	public SMTPSession(Socket conn) throws IOException{
		// dump the connection object into here...
		this.conn = conn;
		
		// set the start state...
		this.currentState = 0;
		
		// setup the input and output streams
		this.outputStream = new DataOutputStream(this.conn.getOutputStream());
		this.inputStream = new DataInputStream(this.conn.getInputStream());
		
		
		// create map for the commands...
		this.commands.put("QUIT", 0);
		this.commands.put("MAIL FROM", 1);
		this.commands.put("RCPT TO", 2);
		this.commands.put("DATA", 3);
		this.commands.put("\n.", 4);
		
		// create a transition table...
		// [state][command]
		//  -> Idle (0)
		this.stateMachine[0][0] = 4; // QUIT
		this.stateMachine[0][1] = 1; // MAIL FROM
		this.stateMachine[0][2] = 8; // RCPT TO
		this.stateMachine[0][3] = 8; // DATA
		this.stateMachine[0][4] = 8; // '.'
		// -> Mail in Progress (1)
		this.stateMachine[1][0] = 4; // QUIT
		this.stateMachine[1][1] = 8; // MAIL FROM
		this.stateMachine[1][2] = 2; // RCPT TO
		this.stateMachine[1][3] = 8; // DATA
		this.stateMachine[1][4] = 8; // '.'
		// -> Recipient Received (2)
		this.stateMachine[2][0] = 4; // QUIT
		this.stateMachine[2][1] = 8; // MAIL FROM
		this.stateMachine[2][2] = 2; // RCPT TO
		this.stateMachine[2][3] = 3; // DATA
		this.stateMachine[2][4] = 8; // '.'
		// -> Waiting for '.' (3)
		this.stateMachine[3][0] = 8; // QUIT
		this.stateMachine[3][1] = 8; // MAIL FROM
		this.stateMachine[3][2] = 8; // RCPT TO
		this.stateMachine[3][3] = 8; // DATA
		this.stateMachine[3][4] = 0; // '.'
		// -> Pseudo Quitting (4)
		this.stateMachine[4][0] = 8; // QUIT
		this.stateMachine[4][1] = 8; // MAIL FROM
		this.stateMachine[4][2] = 8; // RCPT TO
		this.stateMachine[4][3] = 8; // DATA
		this.stateMachine[4][4] = 8; // '.'
	}
	
	/*
	 * 	Checks if a given transition is valid in the SMTP machine.
	 * 	Return:	state to transition to or '8' (no states).
	 */
	private int checkTransition(int state, String cmd){
		if(checkCommand(cmd)){
			// The key exists, so there must be a state in the machine.
			// This will return the state to go to or '8' if there are 
			//  no transitions for that combination of [state][command].
			return this.stateMachine[state][this.commands.get(cmd)];
		}else{
			// The key does not exist...
			return 8;
		}
	}
	
	/*
	 * 	Checks if a command is valid.
	 * 	Return:  boolean (true/false)
	 */
	private boolean checkCommand(String cmd){
		// Checks to see if the given command exists in our system.
		//  IF exists:  true
		//	ELSE:		false
		if(this.commands.containsKey(cmd)) return true;
		else return false;
	}
	
	private String cleanCommand(String cmd){
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	
	public void run(){
		try{
			// Welcome user
			this.outputStream.writeUTF("G'day, I'm " + this.conn.getLocalSocketAddress());
			boolean status = true;
			String input;
			
			while(status){
				// wait to read commands
				input = this.inputStream.readUTF();
				
				// check if it is a command
				if()
				
			}
		
		}catch(SocketTimeoutException s){
			System.out.println("Socket timed out!");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}


/*
 *   ~ References ~
 *   SocketServer: http://www.tutorialspoint.com/java/java_networking.htm
 *   
 * 
 * 
 * 
 */
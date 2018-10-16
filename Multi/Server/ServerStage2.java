import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerStage2 {
	//global variables for thread to use
	public static boolean newDataRequired = false;
	public static boolean gamefinish = false;
	public static boolean gameStarted = false;
	public static ArrayList<gameData> gameData = new ArrayList<gameData>();
	public static ArrayList<String> nameList = new ArrayList<String>();
	public static ArrayList<gameRecord> gameRecord = new ArrayList<gameRecord>();
	public static int remainPlayers = 0;
	public static int startPlayers = 0;
	public static int ChosenPlayers =0;
	
	
	public static void main(String[] args) {
		//initialization setup 
		final int PORT_NUMBER = 80;
	    ServerSocket server = null;
	    int threadCount =0;
	    
	    System.out.println("Simple Guessing Game (Multiplayer version)");
	    
	    //creating thread objects
	    try {
	        server = new ServerSocket(PORT_NUMBER);
	        while (true) {
	        	 threadCount++;
	             System.out.println("current thread count = "+threadCount);
	             
	             //making sure to ask the first player how many digits does he want
	             if(threadCount==1) {
	             	newDataRequired = true;
	             	System.out.println("Asking 1st player to specific digit..");
	             }
	            new ServerThread(server.accept());
	        }
	    } catch (IOException ex) {
	        System.out.println("Unable to start server.");
	    } finally {
	        try {
	            if (server != null)
	                server.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	}
}

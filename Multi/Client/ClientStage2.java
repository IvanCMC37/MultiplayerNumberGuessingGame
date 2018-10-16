import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientStage2 {

    public static void main(String args[]) {
        String host = "127.0.0.1";
        int port = 80;
        new ClientStage2(host, port);
    }

    public ClientStage2(String host, int port) {
        try {
        	//variables initialization
            String serverHostname = new String("127.0.0.1");
            System.out.println("Connecting to host " + serverHostname + " via port " + port + ".");
            Socket echoSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;
            String serverInput = "";

            try {
                //connect to server
                echoSocket = new Socket(serverHostname, port);
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                //setup for receiving input from server
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
               
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + serverHostname);
                System.exit(0);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server");
                System.exit(0);
            }
            //setup for BufferReader
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            //read though all server input, different cases
            while (true) {
            	serverInput =in.readLine();
            	if(serverInput.equals("START_MSG")){
            		boolean reading=true;
	                while(reading)
	                {
	                	serverInput=in.readLine();
	                     if(serverInput.equals("END_MSG")){
		                         reading = false;
		                 }
	                     else if(serverInput.equals("END_MSG2")){
	                     }
	                     else if(serverInput.equals("START_MSG")) {}
	                     else if (serverInput.equals("You chose not to play again")){
	                    	 
	                    	 System.out.println("[Server] : " + serverInput);
	                    	 out.close();
	                    	 in.close();
	                    	 stdIn.close();
	                         echoSocket.close();
	                    	 System.exit(0);
	                     }
	                     else{
	                    	 System.out.println("[Server] : " + serverInput);
		                 }
	                }
            	}
            	
            	//player can input to server after server finish it's broadcast
                System.out.print("Client Input: ");
                String userInput = stdIn.readLine();
                
                //output to server
                out.println(userInput);
            } 
        } catch (Exception e) {
            e.printStackTrace();
      }
   }
}

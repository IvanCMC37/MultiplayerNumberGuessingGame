import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ServerStage1 extends Thread {
	//variables setup 
    public static final int PORT_NUMBER = 80;
    protected Socket socket;
    Random rand = new Random();
    ArrayList <Integer> pool = new ArrayList<Integer>();
    ArrayList <String> serverOutput = new ArrayList<String>();
    int guess =0;
    OutputStream out = null;
    String numInit ="";
    int random =0;
    boolean hasWinner = false;
    PrintWriter pwG =null;
    PrintWriter pwC =null;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
    
    //start the socket operation thread
    private ServerStage1(Socket socket) {
    	try {
			pwG = new PrintWriter("Single-Gamming.txt");
			pwC = new PrintWriter("Single-Communication.txt");
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
		}
        this.socket = socket;
        System.out.print("New client connected from " + socket.getInetAddress().getHostAddress());
        System.out.println(" via port "+socket.getPort());
        PrintShortCut(1,"New client connected from " + socket.getInetAddress().getHostAddress()+" via port "+socket.getPort());
        
        start();
       
    }
    
    public void PrintShortCut(int i, String input) {
    	if(i==1) {
    		pwG.println("["+timeStamp+"]");
        	pwG.println(input);
    	}
    	else if (i==2) {
    		pwC.println("["+timeStamp+"]");
        	pwC.println(input);
    	}
    	else {
    		pwG.println("["+timeStamp+"]");
        	pwG.println(input);
    		pwC.println("["+timeStamp+"]");
        	pwC.println(input);
    	}
    	
    	
    }
    
    //function to randomize the digit 
    public String dataInitializer() {
    	pool.clear();
    	String output = "";
	    for(int i=0; i<10;i++) 
        	pool.add(i);
        
	    for(int i=0; i<10;i++)
        	System.out.print(pool.get(i)+" ");
	    System.out.println();
	    
	    Collections.shuffle(pool);
	    for(int i=0; i<10;i++)
        	System.out.print(pool.get(i)+" ");
	    System.out.println();
        random = rand.nextInt((8 - 3) + 1) + 3;
        System.out.println("Choosing "+random+" numbers from pool...");
        PrintShortCut(1,"Choosing "+random+" numbers from pool...");
        for(int i=0; i<random;i++) {
        	output +=pool.get(i);
        }
    	System.out.println("Generated number : "+output);
    	PrintShortCut(1,"Generated number : "+output);
    	return output;
    }
    
    //function to check the similarity of the user input and digit
    public String dataChecker(String generated, String input) {
    	String result ="";
    	int count1 = 0;
        int count2 =0;
        hasWinner = false;
        PrintShortCut(1,"[IN]"+input);
        for (int i=0; i< generated.length();i++) {
        	char[] array1 = generated.toCharArray();
        	char[] array2 = input.toCharArray();
        	//must have same length
        	if(array1.length != array2.length) {
        		result = "Number must be the same size of required, remaining : "+Integer.toString(10-guess-1)+" times" ;
        		return result;
        	}
        	//cant be duplicated
        	if(checkDuplicate(array2)) {
        		result ="Your input cannot contains duplicated numbers, remaining : "+Integer.toString(10-guess-1)+" times" ;		
        		return result;
        	}
        	//go though all digit
        	for(int j=0; j<array1.length;j++) {
        		if(array1[i] ==array2[j]) {
        			count1++;
        			if(i==j)
        				count2++;
        		}	
        	}
        }
        result = "corret number : "+Integer.toString(count1)+" correct position : "+Integer.toString(count2)+" remaining : "+Integer.toString(10-guess-1)+" times";
       //if player guess correctly
        if(count1 ==count2 && count2==random)
        	hasWinner =true;
    	return result;
    }
    
    //function to check duplicate
    public static boolean checkDuplicate(char[] array) {
		boolean duplicates=false;
		for (int j=0;j<array.length;j++)
		  for (int k=j+1;k<array.length;k++)
		    if (k!=j && array[k] == array[j])
		      duplicates=true;
		return duplicates;
	}
    
    //function to make sure that client will not give response until server finish the broadcast
    public void arrayRebuild() {
    	serverOutput.add(0, "START_MSG");
    	serverOutput.add(serverOutput.size(),"END_MSG");
    	for(int i =0; i<serverOutput.size();i++) {
    		try {
				out.write((serverOutput.get(i)+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}	
    	serverOutput.clear();
    }
    
    //initialization for number
    public void numberInitialization() {
    	guess=0;
    	numInit = dataInitializer();
        serverOutput.add("New Game Started");
        serverOutput.add("The unique number has "+random+" digit(s)");
        PrintShortCut(2,"New Game Started");
        PrintShortCut(2,"The unique number has "+random+" digit(s)");
        arrayRebuild();
    }
    
    //thread start method
    public void run() {
    	InputStream in = null;

        try {
        	//setup for operation
            in = socket.getInputStream();
            out = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String request;
            String msgToClient;
            numberInitialization();
            
            //read though the input from client
            while ((request = br.readLine()) != null) {
            	//if player guessed more than 10 time/ already guess correctly 
            	while(guess>=10 || hasWinner==true) {
            		//choose to quit
            		PrintShortCut(2,"[IN]"+request);
                    if(request.equals("q")) {
                    	serverOutput.clear();
                    	System.out.println("Client requested to end the game");
                    	PrintShortCut(1,"Client requested to end the game");
                    	serverOutput.add("You chose not to play again");
                    	PrintShortCut(2,"[OUT]You chose not to play again");
                    	arrayRebuild();
                    	pwG.close();
                    	pwC.close();
    	                System.exit(0);
                    }
                    //choose to replay
                    else if(request.equals("p")) {
                    	System.out.println("Client requested to play again.");
                    	PrintShortCut(1,"Client requested to play again.");
                    	serverOutput.add("You chose to play again");
                    	PrintShortCut(2,"[OUT]You chose not to play again");
                    	numberInitialization();
                    	hasWinner=false;
                    	request = br.readLine();
                    }
                    //invalid input
                    else {
                    	serverOutput.add("Your input is invalid, try again!");
                    	serverOutput.add("Want to play again? Play again(p)/ Quit(q)");
                    	PrintShortCut(2,"Your input is invalid, try again!");
                    	PrintShortCut(2,"[OUT]Want to play again? Play again(p)/ Quit(q)");
                    	arrayRebuild();
                    	request = br.readLine();
                    } 
            	}
            	//choose to forfeit
                if (request.equals("f")){
                	
	                System.out.println("Player chose to forfeit");
	                PrintShortCut(1,"Player chose to forfeit");
	                serverOutput.add("Player chose to forfeit");
                	serverOutput.add("The answer is : "+numInit);
                	serverOutput.add("Want to play again? Play again(p)/ Quit(q)");
                	PrintShortCut(2,"[OUT]Player chose to forfeit");
                	PrintShortCut(2,"[OUT]The answer is : "+numInit);
                	PrintShortCut(2,"[OUT]Want to play again? Play again(p)/ Quit(q)");
	                guess=11;   
	                arrayRebuild();
                }
                //guessing the digit otherwise
                else{
                     msgToClient =dataChecker(numInit,request);
                     PrintShortCut(3,msgToClient);
                     System.out.println("Message sent :" + msgToClient); 
                     serverOutput.add(msgToClient);
                    guess++;
                    if(guess >= 10 && hasWinner==false) {
                    	PrintShortCut(1,"Game ended, maximum guess reached");
                    	System.out.println("Game ended, maximum guess reached");
                    	serverOutput.add("No more guess left, game ended for you.");
                    	serverOutput.add("The answer is : "+numInit);
                    	serverOutput.add("Want to play again? Play again(p)/ Quit(q)");
                    	PrintShortCut(2,"[OUT]No more guess left, game ended for you.");
                    	PrintShortCut(2,"[OUT]The answer is : "+numInit);
                    	PrintShortCut(2,"[OUT]Want to play again? Play again(p)/ Quit(q)");
                    }
                    else if(hasWinner==true) {
                    	PrintShortCut(1,"Player got the correct number...");
                    	PrintShortCut(1,"Announcing winner now...");
                    	System.out.println("Player got the correct number...");
                    	System.out.println("Announcing winner now...");
                    	serverOutput.add("Game ended");
                    	serverOutput.add("You got the correct answer "+numInit);
                    	serverOutput.add("Want to play again? Play again(p)/ Quit(q)");
                    	PrintShortCut(2,"[OUT]Game ended");
                    	PrintShortCut(2,"[OUT]You got the correct answer "+numInit);
                    	PrintShortCut(2,"[OUT]Want to play again? Play again(p)/ Quit(q)");
                    }
                    arrayRebuild();
                }            
            }
        } catch (IOException e) {
			System.out.println("No stream from clinet...");	
		} finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Simple Guessing Game");
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT_NUMBER);
            while (true) {
                new ServerStage1(server.accept());
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
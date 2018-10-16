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
import java.util.Scanner;

public class ServerThread extends Thread {
    
	//local variables for thread to use
    protected Socket socket;
    Random rand = new Random();
    ArrayList <Integer> pool = new ArrayList<Integer>();
    ArrayList <String> pool2 = new ArrayList<String>();
    ArrayList <String> serverOutput = new ArrayList<String>();
    int guess =1;
    OutputStream out = null;
    String numInit ="";
    int random =0;
    boolean guessCorrect = false;
    String name ="";
    boolean setupRequired = true;
    boolean needInput1 = true;
    boolean needInput2 = false;
    PrintWriter pwG =null;
    PrintWriter pwC =null;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
    
    //start the socket operation thread
    ServerThread(Socket socket) {
    	try {
			pwG = new PrintWriter("Multi-Gamming.txt");
			pwC = new PrintWriter("Multi-Communication.txt");
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
    
    //function to select shuffled 0-9 array to desire digits
    public String dataInitializer() {
    	pool.clear();
    	String output = "";
	    for(int i=0; i<10;i++) 
        	pool.add(i);
        
	    for(int i=0; i<10;i++)
        	System.out.print(pool.get(i)+" ");
	    System.out.println();
	    
	    //shuffle the array by using collections
	    Collections.shuffle(pool);
	    for(int i=0; i<10;i++)
        	System.out.print(pool.get(i)+" ");
	    
	    System.out.println();
	    PrintShortCut(1,"Choosing "+random+" numbers from pool...");
        System.out.println("Choosing "+random+" numbers from pool...");
        for(int i=0; i<random;i++) {
        	output +=pool.get(i);
        }
    	System.out.println("Generated number : "+output);
    	PrintShortCut(1,"Generated number : "+output);
    	return output;
    }
    
    //function to check similarity of the user input and the guess value
    public String dataChecker(String generated, String input) {
    	String result ="";
    	
    	//count1 is correct number, count2 is correct location
    	int count1 = 0;
        int count2 = 0;
        guessCorrect = false;
        PrintShortCut(1,"[IN]"+input);
        //go though the whole length of the guess value
        for (int i=0; i< generated.length();i++) {
        	
        	//split into char arrays in order to compare bit by bit
        	char[] array1 = generated.toCharArray();
        	char[] array2 = input.toCharArray();
        	
        	//if length not the same, wrong guess+1
        	if(array1.length != array2.length) {
        		result = "Number must be the same size of required, remaining : "+Integer.toString(10-guess)+" times" ;
        		return result;
        	}

        	//if duplicated digit , wrong guess +1
        	if(checkDuplicate(array2)) {
        		result = "("+name+"), input "+input+ " contains duplicated numbers, remaining : "+Integer.toString(10-guess)+" times" ;	
        		return 	result;
        	}
        		
        	//if passed both tests above, we can finally get the real comparison
        	for(int j=0; j<array1.length;j++) {
        		if(array1[i] ==array2[j]) {
        			count1++;
        			if(i==j)
        				count2++;
        		}	
        	}
        }
        
        result = "corret number : "+Integer.toString(count1)+" correct position : "+Integer.toString(count2)+" remaining : "+Integer.toString(10-guess)+" times";
        
        //if player guessed correctly
        if(count1 ==count2 &&count1==random)
        	guessCorrect =true;
    	return result;
    }
    
    //function to check if the user input has duplicated digits
    public static boolean checkDuplicate(char[] array) {
		boolean duplicates=false;
		
		for (int j=0;j<array.length;j++)
		  for (int k=j+1;k<array.length;k++)
		    if (k!=j && array[k] == array[j])
		      duplicates=true;
		
		return duplicates;
	}
    
    //function to make sure client continue receive msg from server before give input to server 
    public void arrayRebuild(int version) {
    	if(!serverOutput.contains("START_MSG"))
    		serverOutput.add(0, "START_MSG");
    	if(version==1)
    		serverOutput.add(serverOutput.size(),"END_MSG");
    	else
    		serverOutput.add(serverOutput.size(),"END_MSG2");
    	for(int i =0; i<serverOutput.size();i++) {
    		try {
				out.write((serverOutput.get(i)+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}	
    	serverOutput.clear();
    }
    
    //function to make sure player input the range of 3-8 as digits
    public void digitInputCheck() {
    	for(int i=3;i<9;i++)
    		pool2.add(String.valueOf(i));
    	InputStream in;
		try {
			String input;
			in = socket.getInputStream();
			BufferedReader brDigit = new BufferedReader(new InputStreamReader(in));
			 while ((input = brDigit.readLine()) != null) {
				 PrintShortCut(1,"[IN]"+input);
				  if(pool2.contains(input)) {
					 random = Integer.parseInt(input);
					 System.out.println(name+" chose "+random+" digit(s) number.");
					 PrintShortCut(1,name+" chose "+random+" digit(s) number.");
					 break;
				 }
				 else {
					 //means input is either not number or within range
					 PrintShortCut(3,"[OUT]Invalid Input, make sure it's integer within 3-8.");
					 serverOutput.add("Invalid Input, make sure it's integer within 3-8.");
					 arrayRebuild(1);
				 }	 
			 } 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //initialize the digit and final guess number
    public void numberInitialization_1() {
    	if(ServerStage2.newDataRequired ==true) {
    		PrintShortCut(1,"Player one registered");
    		System.out.println("Player one registered");
    		ServerStage2.newDataRequired = false;
    		ServerStage2.gameData.clear();
    		ServerStage2.gameRecord.clear();
    		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		//if enough players have registered within time limit, game will start in max 3 players
    		if(ServerStage2.nameList.size()>3) {
    			System.out.println("Enough player, game starting...");
    			PrintShortCut(1,"Enough player, game starting...");
    			ServerStage2.gameStarted=true;
    			
    		}
    		//if registered player is less than three the game will still start
    		else if(ServerStage2.nameList.size()<=3) {
    			try {
    				Thread.sleep(7000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    			PrintShortCut(1,"Less than 3 players game started");
				System.out.println("Less than 3 players game started");
				ServerStage2.gameStarted=true;
				
    		}
    		
    		numInit="";
    		PrintShortCut(2,"[OUT]Please type a number within 3-8 in order to start game");
    		serverOutput.add("Please type a number within 3-8 in order to start game");
    		arrayRebuild(1);
    		digitInputCheck();
    		numInit = dataInitializer();
    		ServerStage2.gameData.add(new gameData(random,numInit));
    	}
    }
    
    //2nd part of the initialization of digit 
    public void numberInitialization_2() {
    	while(ServerStage2.gameData.isEmpty()) {
    		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	//get the size of total players that has connected to the game
    	ServerStage2.startPlayers= ServerStage2.nameList.size();
    	
    	//we only want 3 in one time
    	if(ServerStage2.startPlayers>3)
    		ServerStage2.startPlayers=3;
    	
    	//get variables for operation (comparison later)
    	random = ServerStage2.gameData.get(0).digit;
    	numInit = ServerStage2.gameData.get(0).number;
		guess=1;
        serverOutput.add("New Game Started");
        serverOutput.add("The unique number has "+random+" digit(s)");
        serverOutput.add("Input your guess now!");
        PrintShortCut(2,"[OUT]New Game Started");
        PrintShortCut(2,"[OUT]The unique number has "+random+" digit(s)");
        PrintShortCut(2,"[OUT]Input your guess now!");
        arrayRebuild(2);
    }
    
    //function to let player to register their name
    public void registerName() {
		  PrintShortCut(2,"[OUT]Register your name now!");
		  PrintShortCut(2,"[OUT]Please enter your name");
    	serverOutput.add("Register your name now!");
    	serverOutput.add("Please enter your name");
        arrayRebuild(1);
        
        InputStream in;
		try {
			String input2;
			in = socket.getInputStream();
			BufferedReader brDigit = new BufferedReader(new InputStreamReader(in));
			 while ((input2 = brDigit.readLine()) != null) {
				 //no repeat name allowed
				 PrintShortCut(3,"[IN]"+input2);
				  if(!ServerStage2.nameList.contains(input2)) {
					 ServerStage2.nameList.add(input2);
					 name =input2;
					 ServerStage2.remainPlayers++;
					 PrintShortCut(2,"[OUT]You have been added to queue one sec...");
					 serverOutput.add("You have been added to queue one sec...");
				     arrayRebuild(2);
					 break;
				 }
				 else {
					 PrintShortCut(2,"[OUT]Someone registered this name, please try another name!");
					 serverOutput.add("Someone registered this name, please try another name!");
					 arrayRebuild(1);
				 } 
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //function to announce ranking of the winner
    public void winnerAnnouncer() {
    	//get their total moves and sort it from small to large
    	ArrayList<Integer> result = new ArrayList<Integer>();
    	for(int i= 0;i<ServerStage2.gameRecord.size();i++)
    		result.add(ServerStage2.gameRecord.get(i).Guess);
    	for(int i= 0;i<ServerStage2.gameRecord.size();i++) {
    		System.out.print(ServerStage2.gameRecord.get(i).Name+ "  ");
    		System.out.println(ServerStage2.gameRecord.get(i).Guess);
    		PrintShortCut(1,ServerStage2.gameRecord.get(i).Name+ "  "+ServerStage2.gameRecord.get(i).Guess);
    	}
    	System.out.println();
    	System.out.println(result);
    	Collections.sort(result);
    	System.out.println(result);
    	//then swap from the actual arraylist
    	for(int i=0;i<result.size();i++) {
    		for(int j=0; j<ServerStage2.gameRecord.size();j++) {
    			if(result.get(i)==ServerStage2.gameRecord.get(j).Guess &&i!=j) {
    				Collections.swap(ServerStage2.gameRecord, i, j);
    			}	
    		}
    	}
    	
    	//print all out
    	System.out.println("Final Ranking:");
    	serverOutput.add("Final Ranking");
    	PrintShortCut(3,"Final Ranking:");
    	for(int i= 0;i<ServerStage2.gameRecord.size();i++) {
    		System.out.print(ServerStage2.gameRecord.get(i).Name+ "  ");
    		System.out.println(ServerStage2.gameRecord.get(i).Guess);
    		serverOutput.add(ServerStage2.gameRecord.get(i).Name+" "+ServerStage2.gameRecord.get(i).Guess);
    		PrintShortCut(3,"[OUT]"+ServerStage2.gameRecord.get(i).Name+ "  "+ServerStage2.gameRecord.get(i).Guess);
    	}
    	PrintShortCut(2,"Want to play again? Play again(p)/ Quit(q)");
    	serverOutput.add("Want to play again? Play again(p)/ Quit(q)");
    	arrayRebuild(2);
    	needInput1=false;
    }
    
    //function to check if the player need to wait until completion of another game
    public void queueCheck() {
    	//if the top 3 name in namelist can go first, if the game hasn't started
    	while(ServerStage2.gameStarted==false) {
    		if(ServerStage2.nameList.get(0).equals(name))
    			break;
    		else if(ServerStage2.nameList.get(1).equals(name))
    			break;
    		else if(name.equals(ServerStage2.nameList.get(2)))
    			break;
    		else {
    			try {
    				Thread.sleep(5000);
    				System.out.println("Wait for it...");
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    		try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 		
    	}
    	
    	//if not, player gotta wait until game finished
    	while(ServerStage2.gameStarted==true) {
    		try {
				Thread.sleep(10000);
				PrintShortCut(2,"[OUT]Game Already started... Please wait...");
				serverOutput.add("Game Already started... Please wait...");
				arrayRebuild(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    //thread start method
    public void run() {
    	InputStream in = null;

        try {
        	//variables for the whole function
            in = socket.getInputStream();
            out = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String request;
            String request2;
            String msgToClient;
            registerName();
            queueCheck();
            numberInitialization_1();
            numberInitialization_2();
            setupRequired =false;
            
            
            //read though the input from client
            while(true) {
            	//before player finish the game
            	while(needInput1==true) {
            		
            		if(setupRequired==true) {
            			PrintShortCut(1,"Entering setup");
                		System.out.println("Entering setup");
                		registerName();
                		queueCheck();
                		numberInitialization_1();
                        numberInitialization_2();
                        ServerStage2.gamefinish = false;
    	                setupRequired=false;
                	}
            		PrintShortCut(2,name+"[OUT]Please give your input for guessing...");
            		serverOutput.add("Please give your input for guessing...");
            		arrayRebuild(1);
            		request = br.readLine();
            		PrintShortCut(3,name+"[IN]"+request);
            		if (request.equals("f")){
            			actionOfInputF();
                    }
                    else if (setupRequired==false){
		                 msgToClient =dataChecker(numInit,request);
		                 System.out.println("Message sent :" + msgToClient); 
		                 PrintShortCut(3,name+"[OUT]"+msgToClient);
		                 serverOutput.add(msgToClient);
		                 actionOfInputNormal();
                    }
            	}
            	//after player finish the game
            	while(needInput2==true) {
            		PrintShortCut(2,name+"[OUT]Please give your input for further operation...");
            		serverOutput.add("Please give your input for further operation...");
            		arrayRebuild(1);
            		request2 = br.readLine();
            		PrintShortCut(3,name+"[IN]"+request2);
            		if(request2.equals("q")) {
            			actionOfInputQ();
                    }
                    else if(request2.equals("p")) {
                    	actionOfInputP();
                    }
                    else {
                    	PrintShortCut(2,name+"[OUT]Your input is invalid, try again!");
                    	PrintShortCut(2,name+"[OUT]Want to play again? Play again(p)/ Quit(q)");
                    	serverOutput.add("Your input is invalid, try again!");
                    	serverOutput.add("Want to play again? Play again(p)/ Quit(q)");
                    	arrayRebuild(2);
                    } 
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
    
    //action when player choose to forfeit the the game
    public void actionOfInputF() {
    	PrintShortCut(3,name+"[OUT]Player "+name+" chose to forfeit");
    	System.out.println("Player "+name+" chose to forfeit");
        serverOutput.add("Player chose to forfeit, used "+guess+" guess in total.");
    	serverOutput.add("The answer is : "+numInit);
    	serverOutput.add("Checking if current player(s) has finished as well...");
    	PrintShortCut(2,name+"[OUT]The answer is : "+numInit);
    	PrintShortCut(2,name+"[OUT]Checking if current player(s) has finished as well...");
        guess=11;   
    	ServerStage2.gameRecord.add(new gameRecord(name,guess));
//    	System.out.println(ServerStage2.gameRecord.size());
        arrayRebuild(2);
//        System.out.println(ServerStage2.gameRecord.size()+"size");
//    	System.out.println(ServerStage2.startPlayers);
        while (ServerStage2.gameRecord.size()!=ServerStage2.startPlayers) {
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    	ServerStage2.gamefinish=true;
    	winnerAnnouncer();
    	needInput2=true;
    	needInput1=false;
    }
    
    //action to deal with normal guessing
    public void actionOfInputNormal() {
    	//if player guessed wrong 10 times
    	 if(guess >= 10 && guessCorrect==false) {
    		 PrintShortCut(1,name+" has run out of guess...");
				System.out.println(name+" has run out of guess...");
				serverOutput.add("No more guess left, game over for you.");
				serverOutput.add("The answer is : "+numInit);
				serverOutput.add("Checking if current player(s) has finished as well...");
				PrintShortCut(2,name+"[OUT]No more guess left, game over for you.");
				PrintShortCut(2,name+"[OUT]The answer is : "+numInit);
		    	PrintShortCut(2,name+"[OUT]Checking if current player(s) has finished as well...");
				ServerStage2.gameRecord.add(new gameRecord(name,guess));
//				System.out.println(ServerStage2.gameRecord.size());
				arrayRebuild(2);
//				System.out.println(ServerStage2.gameRecord.size()+"size");
//				System.out.println(ServerStage2.startPlayers);
				while (ServerStage2.gameRecord.size()!=ServerStage2.startPlayers) {
             	try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
             }
         	ServerStage2.gamefinish=true;
         	winnerAnnouncer();
         	needInput2=true;
         	needInput1=false;
         }
    	 //if player guessed the correct digits
         else if(guessCorrect==true) {
        	PrintShortCut(3,name+"[OUT]You got the correct answer "+numInit+" with "+(guess)+" guess used.");
        	PrintShortCut(3,name+"[OUT]Checking if current player(s) has finished as well...");
         	System.out.println(name+" guessed the correct number "+ numInit);
         	System.out.println("Checking if current player(s) has finished as well...");
         	serverOutput.add("You got the correct answer "+numInit+" with "+(guess)+" guess used.");
         	serverOutput.add("Checking if current player(s) has finished as well...");
         	ServerStage2.gameRecord.add(new gameRecord(name,guess));
//         	System.out.println(ServerStage2.gameRecord.size());
         	arrayRebuild(2);
//         	System.out.println(ServerStage2.gameRecord.size()+"size");
//         	System.out.println(ServerStage2.startPlayers);
         	while (ServerStage2.gameRecord.size()!=ServerStage2.startPlayers) {
             	try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
             }
         	ServerStage2.gamefinish=true;
         	winnerAnnouncer();
         	needInput2=true;
         	needInput1=false;
         }
          guess++;
     	arrayRebuild(2);
    }
    
    //when player choose to quit the game
    public void actionOfInputQ() {
    	serverOutput.clear();
    	PrintShortCut(3,name+"[OUT]You chose not to play again");
    	System.out.println(name+" requested to end the game");
    	serverOutput.add("You chose not to play again");
    	arrayRebuild(1);
    	for(int i =0; i<ServerStage2.gameRecord.size();i++) {
    		if(ServerStage2.gameRecord.get(i).Name.equals(name)) {
//    			System.out.println("before"+ServerStage2.gameRecord.size());
    			ServerStage2.gameRecord.remove(i);
    			ServerStage2.nameList.remove(name);
//    			System.out.println("after"+ServerStage2.gameRecord.size());
    			try {
					socket.shutdownOutput();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			needInput2=false;
    			ServerStage2.remainPlayers--;
    			ServerStage2.startPlayers--;
				
//    			System.out.println("remain "+ServerStage2.remainPlayers);
//    			System.out.println("start "+ServerStage2.startPlayers);
    		}	
    	}
    	//check if the server have player waiting, if not end it
    	if(ServerStage2.gameRecord.size()==0) {
			setupRequired=true;
			ServerStage2.gameStarted=false;
			ServerStage2.gamefinish=false;
			if(ServerStage2.remainPlayers==0) {
				PrintShortCut(1,"No new player joined, exiting...");
				System.out.println("No new player joined, exiting...");
				pwG.close();
				pwC.close();
				System.exit(0);
			}			
//			break;
		}  
    }
    
    //when player choose to continue another round
    public void actionOfInputP() {
    	PrintShortCut(3,name+"[OUT]You chose to play again");
    	System.out.println(name+" requested to play again.");
    	serverOutput.add("You chose to play again");
    	arrayRebuild(2);
    	
    	guess = 1;
    	for(int i =0; i<ServerStage2.gameRecord.size();i++) {
    		if(ServerStage2.gameRecord.get(i).Name.equals(name)) {
//    			System.out.println("before"+ServerStage2.gameRecord.size());
    			ServerStage2.gameRecord.remove(i);
    			ServerStage2.nameList.remove(name);
//    			System.out.println("after"+ServerStage2.gameRecord.size());
    			ServerStage2.remainPlayers--;
    			ServerStage2.startPlayers--;
//    			System.out.println("remain "+ServerStage2.remainPlayers);
//    			System.out.println("start "+ServerStage2.startPlayers);
    		}	
    	}
    	if (ServerStage2.startPlayers>0) {
    		try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	if(ServerStage2.gameRecord.size()==0) {
			setupRequired=true;
			ServerStage2.gameStarted=false;
			ServerStage2.newDataRequired=true;
			needInput1=true;
			needInput2=false;
//			break ;
		} 	
    }

   
}
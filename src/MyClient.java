import java.net.*;
import java.io.*;  

class MyClient{  

	public static void main(String args[]){  
		
		long timestamp, tcheck;
		//while(true){
			try {
				Socket s=new Socket("localhost",3333); 
		
				//ObjectOutputStream clientOutputResult = new ObjectOutputStream(s.getOutputStream());
			    ObjectInputStream clientInputChallenge = new ObjectInputStream(s.getInputStream());
			    DataInputStream din=new DataInputStream(s.getInputStream());  
				DataOutputStream dout=new DataOutputStream(s.getOutputStream());
				 
			    Block ch = (Block)clientInputChallenge.readObject();
			    int difficulty = din.readInt();
			//    s.close();
			    System.out.println("Client received Block...");
			    System.out.println("Client mining Block...");
			    int result = ch.mineBlock(difficulty);
			    System.out.println("Block mined Sending information to server..."); 
			    
			   
			   dout.writeInt(result);
			    
				int status = din.readInt();
				
				if(status == 1) {
					
					System.out.println("\nYou have been awarded..");
					
				}else if(status == 2) {
					
					System.out.println("\nYour answer is INCORRECT..");
					
				}else if(status == 3){
					
					System.out.println("\nYou were little late for submission...");
					
				}else{
					
					System.out.println("\nYour answer is not submitted to the server... \nYou exceeded the provided time...");
				}
				
				Boolean nextChallenge = din.readBoolean();
				
				if(nextChallenge) {
					
					System.out.println("Please reconnect after 30 seconds for next challenge..");
					
				}else {
					
					System.out.println("Thank You for staying with us...");
				}
				
				din.close();
				dout.close();
				clientInputChallenge.close();
				System.out.println("Client exitting..");	
				s.close();
			        
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		//}	
	}
}	
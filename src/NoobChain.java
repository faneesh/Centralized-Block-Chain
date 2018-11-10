
import java.util.ArrayList;
import java.util.Collections;
import java.net.*;  
import java.io.*;
import java.util.Date;


public class NoobChain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static ArrayList<Client> ClientData = new ArrayList<Client>();
	public static int difficulty = 5;	 

	public static void main(String[] args) {	
		
		int j = 0;
		int i = 0;
		int k = 0;
		int numClients = 3;
		int numChallenges = 3;
		long timestamp = 0, tcheck;

		System.out.println("\nServer is  Mining Genesis block ... ");
		addBlock(new Block("Hi im the first block", "0"));
		
		try {
			ServerSocket serverSocket=new ServerSocket(3333);
			Socket[] client = new Socket[numClients];
			ObjectOutputStream[] serverOutputChallenge = new ObjectOutputStream[numClients];
			DataOutputStream[] dout=new DataOutputStream[numClients];
			DataInputStream[] din=new DataInputStream[numClients];
			Thread[] t = new Thread[numClients];
			
		while( j != numChallenges){
				
				try {			
							
					System.out.printf("\nCreating Chaallenge No....%d...", j+1);
					Block challenge = new Block("Hi im second block",blockchain.get(blockchain.size()-1).hash);
					System.out.println("\nChallenge is created...");
					
					System.out.println("\nWaiting for clients..");
					
					
					i = 0;
					
					 while (i != numClients)  
				        {             
				            try 
				            { 
				     
				            	System.out.printf("\nWaiting for client ..%d..", i);
				            	client[i] = serverSocket.accept(); 
				                  
				                  
				                
				                System.out.println("\nAssigning new thread for this client"); 
				                
				                serverOutputChallenge[i] = new ObjectOutputStream(client[i].getOutputStream());
				        		dout[i] = new DataOutputStream(client[i].getOutputStream());
				        		din[i] = new DataInputStream(client[i].getInputStream());
				     
				                // create a new thread object 
				                
				        		String ip = (((InetSocketAddress) client[i].getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
				    			Client c = new Client(i, ip);
				    			ClientData.add(c);
				    			ClientData.get(i).timestamp = new Date().getTime() + 50000000;
				        		
				        		t[i] = new ClientHandler(client[i], serverOutputChallenge[i], dout[i], din[i], challenge, difficulty, ClientData, i, timestamp); 
				  
				                // Invoking the start() method
				        		if(i == numClients-1) {
				        			
				        			while(k < numClients) {
				        				
				        				t[k].start();
				        				k++;
				        			}
				        		}
				                
				                if(i == numClients-1) {
				                	timestamp = new Date().getTime();
				                	timestamp += 15000;
				                	tcheck = 0;
				                	while(tcheck <= timestamp){
				                		
				                		tcheck = new Date().getTime();
				                	}
				                }
				           } 
				            catch (Exception e){ 
				                System.out.print(e); 
				            } 
				            
				            i++;
				        } 
					i = 0;
					while(i != numClients) {
						System.out.printf("\nClient no. %d nonce :- %d", i, ClientData.get(i).nonce);
						System.out.printf("\nClient no. %d timestamp :- %d", i, ClientData.get(i).timestamp);
						i++;
				
					}
					
					i = 0;
					
					System.out.println("\nGot clients responses..");
					System.out.println("Verifying the block..");
							
					Sort(ClientData);
					
					while(i != numClients){			
						
						if(verifyBlock(challenge, ClientData.get(i).nonce)) {
							
							System.out.println("\nBlock is added to the chain..");
							System.out.printf("\nClient %d result is accepted...", ClientData.get(i).id);
							dout[ClientData.get(i).id].writeInt(1);
							break;
							
						}else{
			
							System.out.printf("\nClient %d result is INCORRECT...", ClientData.get(i).id);
							dout[ClientData.get(i).id].writeInt(2);
							i++;
							
						}
						
					}
					
					k = i+1;
					
					while( k < numClients ) {
							
						if(ClientData.get(k).nonce == 0){
							
							System.out.printf("\nClient %d did not send answer within given time...", ClientData.get(k).id);
							dout[ClientData.get(k).id].writeInt(4);
							k++;	
						}else {
								
							System.out.printf("\nClient %d answer is not chosen for verification because of late submission...", ClientData.get(k).id);
							dout[ClientData.get(k).id].writeInt(3);
							k++;
						}
					}
					
					System.out.println("\nBlockchain is Valid: " + isChainValid());
					
					String blockchainJson = StringUtil.getJson(blockchain);
					System.out.println("\nThe block chain: ");
					System.out.println(blockchainJson);		
					//System.out.println("Server exitting..");
					
					if( j == numChallenges-1 ) {
						
						i = 0;
						while(i!=numClients) {
							
							dout[i].writeBoolean(false);
							i++;				    
						}				
					}else {
						
						i = 0;
						while(i!=numClients) {
							
							dout[i].writeBoolean(true);
							i++;				    
						}
					}
					
					i = 0;
					while(i!=numClients) {
						
						din[i].close();
					    dout[i].close();
					    serverOutputChallenge[i].close();
					    client[i].close();
					    i++;
					}
				    }
					catch (Exception e) {
					    e.printStackTrace();
					}    			
			j++;
		}
		
		serverSocket.close();
		
		}catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	
	//j++;
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
			
		}
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	public static boolean verifyBlock (Block challenge, int newnonce) {
		if(challenge.verify(newnonce, difficulty)) {
			blockchain.add(challenge);
			return true;
		}else {
			return false;
		}
	}
	
	public static void Sort(ArrayList<Client> Chain){
		
		int n = Chain.size();
		
		for (int i = 0; i < n-1; i++) {
		
			int min_idx = i;
			for (int j = i+1; j < n; j++)
				if(Chain.get(j).timestamp < Chain.get(min_idx).timestamp)
					min_idx = j;
			
			Collections.swap(Chain, min_idx, i);
		}
	}	
	
}
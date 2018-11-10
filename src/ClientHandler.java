import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

class ClientHandler extends Thread  
{ 
	Socket client;
	ObjectOutputStream serverOutputChallenge;
	DataOutputStream dout;
	DataInputStream din;
	Block challenge;
	int difficulty;
	ArrayList<Client> ClientData;
	int id;
	long timestamp;
  
    // Constructor 
    public ClientHandler(Socket client, ObjectOutputStream serverOutputChallenge , DataOutputStream dout, DataInputStream din, Block challenge, int difficulty, ArrayList<Client> ClientData, int id, long timestamp)  
    { 
        this.client = client;
        this.serverOutputChallenge = serverOutputChallenge;
        this.dout = dout; 
        this.din = din;
        this.challenge = challenge;
        this.difficulty = difficulty;
        this.ClientData = ClientData;
        this.id = id;
        this.timestamp = timestamp;
    } 
  
    @Override
    public void run()  
    { 
    	int i = id;
    	int newnonce;
    	long tcheck=0;
        //while (i==id)  
        //{ 
            try { 
                 
            	serverOutputChallenge.writeObject(challenge);	
    			dout.writeInt(difficulty);
    			//timestamp = new Date.getTime();
                newnonce = din.readInt();
                System.out.printf("\nReceived Data from Client..%d..", i);
                ClientData.get(id).nonce = newnonce;
                ClientData.get(id).timestamp = new Date().getTime();
                System.out.printf("\nclient %d thread exitting", i);
                
                } catch (Exception e) { 
                e.printStackTrace(); 
          //  } 
           // i++;
        } 
    } 
} 
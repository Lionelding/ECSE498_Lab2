import java.net.*;
public class DnsClient {

	
//Field
	private static final int DEFAULT_PACKETSIZE = 512;			//Maximum Packet Size
	private static int RESET_TIMEOUT;				// Store the timeout amount we allow to wait
	private static int DEFAULT_PORT;					
	private static String ASKED_SERVER;	// Store the DNS server to which we send the request
	private static int TriesCount = 1;	// Indicates the number of Tries
    private static int MAX;            // Store the number of max tries permitted
    private static int TYPE;           // Store the type of request


	public static void main(String args[]) throws Exception{

		//Import all of the arguments of the input into an Object called input
		Input a=new Input(args);
		//Get variables from the Object
		RESET_TIMEOUT=a.getTimeout();
		DEFAULT_PORT=a.getPort();
		String AskedDomainName=a.getName();
		ASKED_SERVER=a.getDnsserver();
		MAX=a.getMaxretries();
		TYPE= a.getType();
		System.out.println("DnsClient sending request for: "+AskedDomainName);
		
		//Executing a single method to send request and receive response
		QueryandDecoding(AskedDomainName,true, MAX,TYPE);
		
		
		//End...
	}
	
	
	//Send the request based on input, and decode the response
	private static String QueryandDecoding(String AskedDomainName, boolean printFound, int MAXtemp, int TYPEtemp) throws Exception{
		
		String AskedServer = ASKED_SERVER;	
		boolean Tries =  false; 
		Packet Validanswer = new Packet();
		Packet Getananswer = new Packet();
		
		// infinite loop that allows to continue to send queries until maximum number of tries reached
		while (!Tries){
	
			//Put inputs into a packet and send it
			Getananswer = TrySendOnce(AskedDomainName,AskedServer,TYPEtemp); 
			
			//If there is no answer in the response until max retry reached
			while(Getananswer == null)
			{
				Getananswer = TrySendOnce(AskedDomainName,AskedServer,TYPEtemp); 
				if(TriesCount==MAXtemp)
				{
					System.out.println("NOTFOUND");
					System.exit(0); 
					return null;
				}	
			}
			

			
			if(Getananswer != null && Getananswer.numberofRRinauthor()>0 && (Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_NS_RECORD)&&
					(Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_MX_RECORD)&&
					Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_CNAME_RECORD&&
					(Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_A_RECORD))
					
			{
						if(TriesCount==MAXtemp)
						{
						System.out.println("NOTFOUND");
						System.exit(0);
						return null;
						}
					
			}
				
					
			else{
					
					Validanswer = Getananswer;
			}
			

			if(Validanswer.numberofRRinanwers()==0){ 				
				if(TriesCount==MAXtemp)
				{
					System.out.println("NOTFOUND");
					return null;
				}
			}


			//For loop to go through response to match if it A type or CNAME
			for(int i=0;i<Validanswer.numberofRRinanwers();i++){			
					return null; 				
			}	
			
		}
		return null;
	}
	


	//Construct a packet and send to the Dns server
	private static Packet TrySendOnce(String AskedDomainName, String ipAddr,int TYPEtemp) throws Exception{
		//Time 
		long startTime = System.nanoTime();
		System.out.println("Server:  " + ipAddr);
		if (TYPEtemp == 1)
		{
			System.out.println("Request type: A");	
		}else if(TYPEtemp == 2)
		{
			System.out.println("Request type: NS");
		}
		else if(TYPEtemp == 15)
		{
			System.out.println("Request type: MX");
		}
		
		//Create UPD socket
		DatagramSocket clientSocket = new DatagramSocket();
		
		//Convert the ipAddr from String to byte
		String[] split = ipAddr.split("\\.");
		 byte [] IP=new byte [4];
		int i1,i2,i3,i4;
		 
		 i1= Integer.valueOf(split[0]).intValue();
		 i2= Integer.valueOf(split[1]).intValue();
		 i3= Integer.valueOf(split[2]).intValue();
		 i4= Integer.valueOf(split[3]).intValue();
		 
		 IP[0]=  (byte) ((0xFF) & i1);
		 IP[1]=  (byte) ((0xFF) & i2);
		 IP[2]=  (byte) ((0xFF) & i3);
		 IP[3]=  (byte) ((0xFF) & i4);
		InetAddress IPAddress = InetAddress.getByAddress(IP);
		
		//Put everything together and be ready to send
		Packet outpacket = new Packet(AskedDomainName,TYPEtemp);
		DatagramPacket PackettoSend = new DatagramPacket(outpacket.Getwholedata(), outpacket.Getwholedata().length, IPAddress, DEFAULT_PORT);
		clientSocket.send(PackettoSend);


	
		//This part is to receive the DNS packet
		byte[] receiveData = new byte[DEFAULT_PACKETSIZE];
		clientSocket.setSoTimeout(RESET_TIMEOUT);	
		//Create the container and be ready to put everything in
		DatagramPacket receivePackettoReceive = new DatagramPacket(receiveData, receiveData.length);
		try{	
			clientSocket.receive(receivePackettoReceive);

		}catch (SocketTimeoutException s) {
			TriesCount++;
			clientSocket.close();
			return null;
		}
				
		Packet inPacket = new Packet(receiveData, AskedDomainName, startTime, TriesCount);
		TriesCount++;
		
		
		//Close UDP socket 
		clientSocket.close();
		
		return inPacket;
	}

	
}

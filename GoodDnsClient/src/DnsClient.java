import java.io.InterruptedIOException;
import java.net.*;
public class DnsClient {

	
//Field
	//private static final String null = "";				//Value for returning no ip.
	private static final int DEFAULT_PACKETSIZE = 512;			//Max Packet Size
	private static int RESET_TIMEOUT;				//!!!Timeout after 4 sec
	private static int DEFAULT_PORT;					//Default Port is 53
	private static String ASKED_SERVER;	//!!!ROOT IP Adress
//	private static final String AU_ROOT_IP = "8.8.8.8";
	private static int TriesCount = 1;								//Keeps track of the number of server replys it has encountered
    private static int MAX; 
    private static int TYPE;


	public static void main(String args[]) throws Exception{
		//String AskedDomainName = "www.baidu.ca";		//WORKS: +FIX_TEST+ arc.gov.au NS doe not reply, needs to try next ns (Works)
		//String AskedDomainName = "www.plan-international.gov";


		Input a=new Input(args);

		RESET_TIMEOUT=a.getTimeout();
		DEFAULT_PORT=a.getPort();
		String AskedDomainName=a.getName();
		ASKED_SERVER=a.getDnsserver();
		MAX=a.getMaxretries();
		TYPE= a.getType();
		System.out.println("DnsClient sending requestQue for: "+AskedDomainName);
		QueryandDecoding(AskedDomainName,true, MAX,TYPE);
		
		//resolveDomain(args[0],true);
		//GAME OVER.. 
		
		
	}
	
	
	/**
	 * This method returns a byte from a bitset.
	 * @param AskedDomainName The domain name we are looking for.
	 * @param printFound Sets weather it should print if the domain IP has been found.
	 * @return String A string IP address.
	 */  
	private static String QueryandDecoding(String AskedDomainName, boolean printFound, int MAXtemp, int TYPEtemp) throws Exception{
		
		String AskedServer = ASKED_SERVER;	//Holds the IP to ask next
		boolean Tries =  false; 
		Packet Validanswer = new Packet();
		Packet Getananswer = new Packet();
		
		
		//!!!Keeps looping until a SOA is encountered, no response from all servers or hopefully it finds the IP
		while (!Tries){
		
			//CHECKS FOR ERRORS : SOA, Error Codes, NULL Return (Timeout)
			
			Getananswer = TrySendOnce(AskedDomainName,AskedServer,TYPEtemp); 
			//long startTime = System.nanoTime();//at the beginning for store the start time in starttime
			//christine
		/*	if(Getananswer == null)
				{
				System.out.println(AskedDomainName + " NOTFOUND");//christine
				return null;
				}*/
				//christine
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
			
			if(Getananswer != null &&( Getananswer.RCODEreader() == 1
					|| Getananswer.RCODEreader() == 2
					|| Getananswer.RCODEreader() == 3
					|| Getananswer.RCODEreader() == 4
					|| Getananswer.RCODEreader() == 5))
				{
				
					
			
			if(Getananswer != null && Getananswer.numberofRRinauthor()>0 && (Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_NS_RECORD)&&
					(Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_MX_RECORD)&&
					Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_CNAME_RECORD&&
					(Getananswer.GetrecordsInAuthoritative(0).getType()!=RRecord.TYPE_A_RECORD))
					
				{
				
				//christine
			//	System.out.println(Getananswer.isError());
				//christine
				//!!!Tests for SOA record
				/*if(Getananswer != null && Getananswer.noAuthoritive()>0){
					System.out.println("debug");*/
					/*if((Getananswer.getRRAuth(0).getType()!=RRecord.TYPE_CNAME_RECORD)&&
							(Getananswer.getRRAuth(0).getType()!=RRecord.TYPE_NS_RECORD)&&
							(Getananswer.getRRAuth(0).getType()!=RRecord.TYPE_MX_RECORD)&&
							(Getananswer.getRRAuth(0).getType()!=RRecord.TYPE_A_RECORD))
					{		//1st rec
						//SOA Rec == NXDOMAIN
*/
						if(TriesCount==MAXtemp)
						{
						System.out.println("NOTFOUND");
							
						System.exit(0);//christine
						}
					
						return null;
				}
				
					
				}else{
					
					Validanswer = Getananswer;	//If not null then has answers	=> Response Changed
				}
			

			//christine debug purposeExhausts all possible cases given a valid Reply
			
			//christine debug purpose
			//System.out.println();
			/*System.out.println(response.isResponse());*/
			//christine debug purpose
			//System.out.println();
			if(Validanswer.numberofRRinanwers()==0){
				//Gets the next IP to query. 
				
				if(TriesCount==MAXtemp)
				{
					System.out.println("NOTFOUND");
					return null;//christine
				}
			//	return null;
			}
			//	return null;
			//christine
				//Loops though answers to find if A or CNAME
			for(int i=0;i<Validanswer.numberofRRinanwers();i++){			
					return null; 				
			}
			
		
				
				//Asks to find a valid IP to query next
				//at = getNextIP(response);//christine
				
				
			
		}
		return null;
	}
		//If this point is reached no IP was resolved. CASE: All servers timed out (Highly Unlikley)
	
	
	
	
	/**
	 * This method searches though a response packet and attempts to find a NS Auth IP for the question inside the packet.
	 * If it cannot find one it will attempt to resolve an NS which did not get provided with additional information (IP)
	 * If it cannot resolve the NS it will return null.
	 * @param response A dns_packet which holds the response.
	 * @return String A string IP adress.
	 */  
	/*private static String getNextIP(Packet response) throws Exception{
		String out = null;
		
		//Scans for Type A records (Makes sure it only returns valid IPs which have not been tried)
		for(int i=0;i<response.noAditional();i++){
			if(!response.getRRAdd(i).isTried() 
			 && response.getRRAdd(i).getType() == RRecord.TYPE_A_RECORD){	//TYPE A REC not tried
				out = response.getRRAdd(i).getStringData(); 	//Gets the IP
				response.getRRAdd(i).setTried();				//Sets the IP as used
				
				return out;
			}	
		}
		
		//If no ip found it recursivly calls resolveDomain to get the IP of unsolved NS for next query.
		if (out==null && response.noAuthoritive() > 0){
			//If out is null => no A records, try solve NS in Auth Section
			for(int i=0;i<response.noAuthoritive();i++){
				out = resolveDomain(response.getRRAuth(i).getStringData(),false);	
			
				//If NS is succesfully resolved then return NS Ip to original resolve thread
				if(out != null) return out;	
			}
		}
		
		//CASE: No Ips or Root servers exist (CNAME only in reply=> Start from scratch)
		if(response.noAuthoritive()==0 && response.noAditional()==0) return AU_ROOT_IP;
		
	
		//CASE: All servers queried recursivly did not respond. No SOA, No error. ALL TimeOut (HIGHLY UNLIKLY)
		System.out.println("All servers timed out. Domain could not be solved.");
		return null;
	}//christine suggeat remove
	
	
	
	
	
	
	
	
	
	
	*/
	
	/**
	 * This function constructs a DNS packet from a domainName and sends it to a given IP adress.
	 * It deals with timeout by returning null if no reply.
	 * @param domainName The name of the domain the dns is quering for.
	 * @param ipAddr The IP adress to send query.
	 * @return DNS_packet A dns packet reply.
	 */	
	private static Packet TrySendOnce(String AskedDomainName, String ipAddr,int TYPEtemp) throws Exception{
		//christine: for requirements in slides
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
		
		//UDP Socket Open
		DatagramSocket clientSocket = new DatagramSocket();
		
		//Create DNS Query Packet and send
		/*InetAddress IPAddress = InetAddress.getByName(ipAddr); 		*///!!!change to 
		String[] split = ipAddr.split("\\.");// 
		 byte [] IP=new byte [4];
		int i1,i2,i3,i4;
		//!!!split[i]???   
		 i1= Integer.valueOf(split[0]).intValue();
		 i2= Integer.valueOf(split[0]).intValue();
		 i3= Integer.valueOf(split[0]).intValue();
		 i4= Integer.valueOf(split[0]).intValue();
		 
		 IP[0]=  (byte) ((0xFF) & i1);
		 IP[1]=  (byte) ((0xFF) & i2);
		 IP[2]=  (byte) ((0xFF) & i3);
		 IP[3]=  (byte) ((0xFF) & i4);
		 
		InetAddress IPAddress = InetAddress.getByAddress(IP);
		Packet outpacket = new Packet(AskedDomainName,TYPEtemp);
		DatagramPacket PackettoSend = new DatagramPacket(outpacket.Getwholedata(), outpacket.Getwholedata().length, IPAddress, DEFAULT_PORT);  //!!! 53 is the default port
		clientSocket.send(PackettoSend);
		// christine try get byaddress
		
		

	
		//Receives DNS packet
		byte[] receiveData = new byte[DEFAULT_PACKETSIZE];			//!!! 1024 and 512? 
		clientSocket.setSoTimeout(RESET_TIMEOUT);	//!!! Should be a variable. If no data arrives within certain time period, throw it in the exception 
		DatagramPacket receivePackettoReceive = new DatagramPacket(receiveData, receiveData.length);
		try{	//Try receive the data
			clientSocket.receive(receivePackettoReceive);
		/*}catch(InterruptedIOException iioexception){		
			clientSocket.close();
			return null;	//Timeout
		}*///christine for time out
		//Timeout
		}catch (SocketTimeoutException s) {
			
			TriesCount++;
			clientSocket.close();
			return null;
		}
				
		
        
		Packet inPacket = new Packet(receiveData, AskedDomainName, startTime, TriesCount);
		
		
		//!!!Prints only if it receives a reply
		/*System.out.println("Name server " + nsCount + ": " + ipAddr);*///change to top
		TriesCount++;
		
		
		//UDP Socket Close
		clientSocket.close();
		
		return inPacket;
	}

	
}

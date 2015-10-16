import java.io.InterruptedIOException;
import java.net.*;
public class DnsClient {

	
//Field asdfasdfasdfasdfsd
//jdjdjdjjddj
	private static final String NO_IP_FOUND = "";				//Value for returning no ip.
	private static final int DEFAULT_PAK_SIZE = 1052;			//Max Packet Size
	private static final int REPLY_TIMEOUT = 5000;				//!!!Timeout after 4 sec
	private static final int DEFAULT_PORT=53;					//Default Port is 53
//	private static final String AU_ROOT_IP = "132.206.85.18";	//!!!ROOT IP Adress
	private static final String AU_ROOT_IP = "8.8.8.8";
	private static int nsCount = 1;								//Keeps track of the number of server replys it has encountered




	public static void main(String args[]) throws Exception{
		String askfor = "www.baidu.ca";		//WORKS: +FIX_TEST+ arc.gov.au NS doe not reply, needs to try next ns (Works)
		//String askfor = "www.plan-international.gov";
		//christine: for requirements in slides
		
		System.out.println("DnsClient sending request for: "+askfor);
		resolveDomain(askfor,true);

		//resolveDomain(args[0],true);
		//GAME OVER.. 
	}
	
	
	/**
	 * This method returns a byte from a bitset.
	 * @param askfor The domain name we are looking for.
	 * @param printFound Sets weather it should print if the domain IP has been found.
	 * @return String A string IP address.
	 */  
	private static String resolveDomain(String askfor, boolean printFound) throws Exception{
		
		String at = AU_ROOT_IP;	//Holds the IP to ask next
		boolean found =  false; 
		DnsPacket response = new DnsPacket();
		DnsPacket newResponse = new DnsPacket();
		
		
		//!!!Keeps looping until a SOA is encountered, no response from all servers or hopefully it finds the IP
		while (!found){
		
			//CHECKS FOR ERRORS : SOA, Error Codes, NULL Return (Timeout)
			
			newResponse = sendPak(askfor,at); 
			//christine
		/*	if(newResponse == null)
				{
				System.out.println(askfor + " NOTFOUND");//christine
				return NO_IP_FOUND;
				}*/
				//christine
			while(newResponse == null)
			{
				newResponse = sendPak(askfor,at); 
				if(nsCount>3)
				{
					System.out.println("Response not received after [time] seconds "+nsCount+" retries");
					System.exit(0); 
				}
				return NO_IP_FOUND;
			}
			
			if(newResponse != null &&( newResponse.errorCode() == 1
					|| newResponse.errorCode() == 2
					|| newResponse.errorCode() == 3
					|| newResponse.errorCode() == 4
					|| newResponse.errorCode() == 5))
				{
				
					
			
			if(newResponse != null && newResponse.noAuthoritive()>0 && (newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_NS_RECORD)&&
					(newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_MX_RECORD)&&
					newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_CNAME_RECORD&&
					(newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_A_RECORD))
					
				{
				
				//christine
			//	System.out.println(newResponse.isError());
				//christine
				//!!!Tests for SOA record
				/*if(newResponse != null && newResponse.noAuthoritive()>0){
					System.out.println("debug");*/
					/*if((newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_CNAME_RECORD)&&
							(newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_NS_RECORD)&&
							(newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_MX_RECORD)&&
							(newResponse.getRRAuth(0).getType()!=DnsRR.TYPE_A_RECORD))
					{		//1st rec
						//SOA Rec == NXDOMAIN
*/
						if(nsCount>3)
						{
						System.out.println("Response not received after [time] seconds "+nsCount+" retries");
							
						System.exit(0);//christine
						}
					
						return NO_IP_FOUND;
				}
				
					
				}else{
				response = newResponse;	//If not null then has answers	=> Response Changed
			}
			

			//christine debug purposeExhausts all possible cases given a valid Reply
			
			//christine debug purpose
			//System.out.println();
			/*System.out.println(response.isResponse());*/
			//christine debug purpose
			//System.out.println();
			if(response.noAnswers()==0){
				//Gets the next IP to query. 
				
				if(nsCount>3)
				{
					System.out.println("Response not received after [time] seconds "+nsCount+" retries");
					return NO_IP_FOUND;//christine
				}
			//	return NO_IP_FOUND;
			}
			//	return NO_IP_FOUND;
			//christine
				//Loops though answers to find if A or CNAME
			for(int i=0;i<response.noAnswers();i++){
				if(response.getRRAns(i).getType()==DnsRR.TYPE_CNAME_RECORD){	//IF CNAME ANSWER
					System.out.println("Response received after [time] seconds "+nsCount+" retries");
					
					System.out.print("CNAME for " + askfor);
				
					//Gets CNAME	
					askfor = response.getRRAns(i).getStringData();
					
					System.out.println(" is " + askfor);
				} else if(response.getRRAns(i).getType()==DnsRR.TYPE_A_RECORD){	//If Type A (IP) Answer 
					//IP FOUND!!! WHOOO RAHHH!!!
					System.out.println("Response received after [time] seconds "+nsCount+" retries");
					
					if(printFound) System.out.println(askfor + " = " + response.getRRAns(i).getStringData());
										
					return response.getRRAns(i).getStringData(); //RETURN IP FOUND
				}
			}
			
		
				
				//Asks to find a valid IP to query next
				//at = getNextIP(response);//christine
				
				
			
		}
		return NO_IP_FOUND;
	}
		//If this point is reached no IP was resolved. CASE: All servers timed out (Highly Unlikley)
	
	
	
	
	/**
	 * This method searches though a response packet and attempts to find a NS Auth IP for the question inside the packet.
	 * If it cannot find one it will attempt to resolve an NS which did not get provided with additional information (IP)
	 * If it cannot resolve the NS it will return NO_IP_FOUND.
	 * @param response A dns_packet which holds the response.
	 * @return String A string IP adress.
	 */  
	/*private static String getNextIP(DnsPacket response) throws Exception{
		String out = NO_IP_FOUND;
		
		//Scans for Type A records (Makes sure it only returns valid IPs which have not been tried)
		for(int i=0;i<response.noAditional();i++){
			if(!response.getRRAdd(i).isTried() 
			 && response.getRRAdd(i).getType() == DnsRR.TYPE_A_RECORD){	//TYPE A REC not tried
				out = response.getRRAdd(i).getStringData(); 	//Gets the IP
				response.getRRAdd(i).setTried();				//Sets the IP as used
				
				return out;
			}	
		}
		
		//If no ip found it recursivly calls resolveDomain to get the IP of unsolved NS for next query.
		if (out==NO_IP_FOUND && response.noAuthoritive() > 0){
			//If out is NO_IP_FOUND => no A records, try solve NS in Auth Section
			for(int i=0;i<response.noAuthoritive();i++){
				out = resolveDomain(response.getRRAuth(i).getStringData(),false);	
			
				//If NS is succesfully resolved then return NS Ip to original resolve thread
				if(out != NO_IP_FOUND) return out;	
			}
		}
		
		//CASE: No Ips or Root servers exist (CNAME only in reply=> Start from scratch)
		if(response.noAuthoritive()==0 && response.noAditional()==0) return AU_ROOT_IP;
		
	
		//CASE: All servers queried recursivly did not respond. No SOA, No error. ALL TimeOut (HIGHLY UNLIKLY)
		System.out.println("All servers timed out. Domain could not be solved.");
		return NO_IP_FOUND;
	}//christine suggeat remove
	
	
	
	
	
	
	
	
	
	
	*/
	
	/**
	 * This function constructs a DNS packet from a domainName and sends it to a given IP adress.
	 * It deals with timeout by returning null if no reply.
	 * @param domainName The name of the domain the dns is quering for.
	 * @param ipAddr The IP adress to send query.
	 * @return DNS_packet A dns packet reply.
	 */	
	private static DnsPacket sendPak(String domainName, String ipAddr) throws Exception{
		//christine: for requirements in slides
		System.out.println("Server:  " + ipAddr);
		//UDP Socket Open
		DatagramSocket clientSocket = new DatagramSocket();
		
		//Create DNS Query Packet and send
		/*InetAddress IPAddress = InetAddress.getByName(ipAddr); 		*///!!!change to 
		String[] split = ipAddr.split("\\.");// 
		 byte [] IP=new byte [4];
		int i1,i2,i3,i4;
		
		 i1= Integer.valueOf(split[0]).intValue();
		 i2= Integer.valueOf(split[0]).intValue();
		 i3= Integer.valueOf(split[0]).intValue();
		 i4= Integer.valueOf(split[0]).intValue();
		 
		 IP[0]=  (byte) ((0xFF) & i1);
		 IP[1]=  (byte) ((0xFF) & i2);
		 IP[2]=  (byte) ((0xFF) & i3);
		 IP[3]=  (byte) ((0xFF) & i4);
		 
		InetAddress IPAddress = InetAddress.getByAddress(IP);
		DnsPacket out = new DnsPacket(domainName);
		DatagramPacket sendPacket = new DatagramPacket(out.packet(), out.packet().length, IPAddress, DEFAULT_PORT);  //!!! 53 is the default port
		clientSocket.send(sendPacket);
		// christine try get byaddress
		
		

	
		//Receives DNS packet
		byte[] receiveData = new byte[DEFAULT_PAK_SIZE];			//!!! 1024 and 512? 
		clientSocket.setSoTimeout(REPLY_TIMEOUT);	//!!! Should be a variable. If no data arrives within certain time period, throw it in the exception 
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try{	//Try receive the data
			clientSocket.receive(receivePacket);
		}catch(InterruptedIOException iioexception){		
			clientSocket.close();
			return null;	//Timeout
		}
		DnsPacket in = new DnsPacket(receiveData, domainName);
		
		
		//!!!Prints only if it receives a reply
		/*System.out.println("Name server " + nsCount + ": " + ipAddr);*///change to top
		nsCount++;
		
		
		//UDP Socket Close
		clientSocket.close();
		
		return in;
	}

	
}

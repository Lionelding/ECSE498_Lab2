import java.util.BitSet;
import java.util.Random;

public class Packet {
	
	// Field:
	// Create array of RRecord for answer sections, authoritative sections, and additional sections of RRecord
	private RRecord[] recordsInAnswer; 
	private RRecord[] recordsInAuthoritative;
	private RRecord[] recordsInAdditional;

	// This stores the domain name that the query and the response are about
	private String domainname;
	// This contains all the bytes that construct a whole DNS packet
	private byte[] datacontent;
	
//Constructors
	// Constructor for creating a new empty packet
	public Packet(){
		
	}
	
	 // Constructor for taking a domain name to search and a query type and construct a request packet to send out
	public Packet(String AskedDomainName, int TYPEtemp){

		domainname = AskedDomainName;
		// This part creates all the needed bytes to stores all info for 
		// the part of DNS packet except the 3 record sections
		int domain_length = domainname.length();
		int data_length_prerecord = domain_length+16+2;	//18+length is the number of bytes for covering all the information in header and question sections. 
		datacontent = new byte[data_length_prerecord];
		
		Random rand = new Random();
		byte[] b1 = new byte[1];
		rand.nextBytes(b1);
		byte[] b2 = new byte[1];
		rand.nextBytes(b2);
		datacontent[0] = b1[0]; 
		datacontent[1] = b2[0]; 
		
		// This part is to store the flags
		// 0x01 is to indicate that we desire recursion
		datacontent[2] = 0x01; 
		datacontent[3] = 0x00;
		// Number of questions is 1; number of resource record in answer
		// section; number of resource record in name server section; 
		// number of resource record in additional section is all 0 
		datacontent[4] = 0x00; 
		datacontent[5] = 0x01;
		datacontent[6] = 0x00; 
		datacontent[7] = 0x00; 
		datacontent[8] = 0x00; 
		datacontent[9] = 0x00; 
		datacontent[10] = 0x00; 
		datacontent[11] = 0x00;
		
		
		// This part is to convert a string (ie. www.mcgill.ca ) and store in 
		// datacontent in the form of 3www6mcgill2ca
		int seqNumber = 13;
		int seqCount = 0;
		for(int i=0;i<domain_length;i++)
			{
				//Checks IF is for the case when it is not a separator '.' 
				// ELSE is for '.'
				
				if(AskedDomainName.charAt(i) == '.')
					{ 
					datacontent[seqNumber-1] = (byte)(int)(seqCount);
					seqNumber += seqCount+1 ;
					seqCount=0;	
					}
				else
				{		
					datacontent[seqNumber+seqCount] = (byte)AskedDomainName.charAt(i);
					seqCount++;
				}
					
			}
		//To detect the few letter after the last dot, and replace the dot with the number
		datacontent[seqNumber-1] = (byte)(seqCount);
		//To end a domain name always with a byte 0x00
		datacontent[13 + domain_length] = 0x00; 

		//Take the input argument of query type and store it in the datacontents 
		datacontent[14 + domain_length] = 0x00; 
		if (TYPEtemp == 1)
		{
			datacontent[15 + domain_length] = 0x01; 	
		}else if(TYPEtemp == 2)
		{
			datacontent[15 + domain_length] = 0x02; 
		}
		else if(TYPEtemp == 15)
		{
			datacontent[15 + domain_length] = 0x0f; 
		}

		// 0x01 representing an Internet address
		datacontent[16 + domain_length] = 0x00; 
		datacontent[17 + domain_length] = 0x01; 
	}	
	
//Constructor used to construct a response DNS packet with an array of received bytes gotten from socket
		public Packet(byte[] bytesAllPacket, String AskedDomainName, long Starttime, int TriesCount){
		// Store the concerned domain name
		// Store the whole byte packet into variable : datacontent
		this.domainname = AskedDomainName;
		this.datacontent = bytesAllPacket;
		this.recordsInAnswer = new RRecord[this.numberofRRinanwers()];  //create the corresponding number of RRecords in the answer field
		this.recordsInAuthoritative = new RRecord[this.numberofRRinauthor()]; //create the corresponding number of RRecords in the authoritative field
		this.recordsInAdditional = new RRecord[this.numberofRRinadditionals()]; //create the corresponding number of RRecords in the Additional field
		
		
		//Print the error code
		if (RCODEreader() == 0)
		{
		}
		else if (RCODEreader() == 1)
		{
			System.out.println("RCODE = Format error: the name server was unable to interpret the query");
			System.exit(0); 

		}
		else if (RCODEreader() == 2)
		{
			System.out.println("RCODE = Server failure: the name server was unable to process this query due to a problem with the name server");
			System.exit(0); 

		}
		else if (RCODEreader() == 3)
		{
			System.out.println("RCODE = NOTFOUND");
			System.exit(0); 

		}
		else if (RCODEreader() == 4)
		{
			System.out.println("RCODE = Not implemented: the name server does not support the requested kind of query");
			System.exit(0); 

		}
		else if (RCODEreader() == 5)
		{
			System.out.println("RCODE = Refused: the name server refuses to perform the requested operation for policy reasons");
			System.exit(0); 

		}
		//Create some variables to store and to construct all of the RRecords in the Response packets
		String rDomainName = "";
		int rType;  
		int rClass; 	
		int rTTL;	
		int valueLength;
		byte[] ValueforA;  // Create an byte array to store the value for Type A answer
		String ValuefornotA = "";  //Create a string to store the value for non Type A answer 
		

		//Get the name length
		int domain_length = domainname.length();
		//domain_length+16+2 is the number of bytes for covering all the information in header and question sections
		//So the first byte for RR to extract should start at here (after 18 + domainlength)
		int newRecordStartIndex = domain_length+16+2;
		int name_LengthInBytes=0;
	

		//Print the number of RR
		if (numberofRRinanwers()> 0)
			{	
			System.out.println("Response received after " + (System.nanoTime() - Starttime)/Math.pow(10, 9) + " seconds "+TriesCount+" retries");
			
			System.out.println(" *** Answer Section ("+numberofRRinanwers()+") records ***");
			}
		
		// Use a FOR loop to go through the answer field
		for(int i=0;i<numberofRRinanwers();i++){

					//For example, if the domain name is for youtube, than rDomainName is in the format of "www.youtube.com"
					rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex));
					name_LengthInBytes = hostnametoStringLen(datacontent, newRecordStartIndex); //get the length of rDomainName
		
			// The following extracts and reads the type, class,ttl, valuelength of the RR
			rType  = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes);
			rClass = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+2);
			rTTL   = Conversion.byte4tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+4);
			valueLength = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+8);
			
			
			//Create one RR based on the type detected
			if(rType==RRecord.TYPE_NS_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));	
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			}else if(rType==RRecord.TYPE_A_RECORD){
				ValueforA =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) 
					{
					ValueforA[j] = datacontent[newRecordStartIndex+name_LengthInBytes+10+j];		
					}	
				String new_rrData = Conversion.bytesTowholeIPString(ValueforA);
				recordsInAnswer[i] = new RRecord(rType,rClass,new_rrData,rTTL,rDomainName);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			} else if( rType==RRecord.TYPE_MX_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));		
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);	
				int preference = (int)Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+10);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.print(IsAuthoritativeOrNot());
				System.out.println("	"+ "preference:"+ preference);
			}
				else if(rType==RRecord.TYPE_CNAME_RECORD){
					ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));		
					recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
					recordsInAnswer[i].RecordDetailsPrint();			
					System.out.println(IsAuthoritativeOrNot());
				}
				

				else { 
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
			}
			
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+valueLength;
		}
		
		// Start printing authoritative sections 
		if (numberofRRinauthor()> 0)
		{	
		System.out.println(" *** Authoritive Section ("+numberofRRinauthor()+") records ***");
		}
		//Use a FOR loop to go through the Authoritative section
		for(int i=0;i<numberofRRinauthor();i++){
			rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex));
			name_LengthInBytes = hostnametoStringLen(datacontent, newRecordStartIndex);
			rType  = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes);
			rClass = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+2);
			rTTL   = Conversion.byte4tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+4);
			valueLength = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+8);
			if(rType==RRecord.TYPE_NS_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			}else if(rType==RRecord.TYPE_A_RECORD){
				
				ValueforA =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) 
				{
				ValueforA[j] = datacontent[newRecordStartIndex+name_LengthInBytes+10+j];		
				}	
				String new_rrData = Conversion.bytesTowholeIPString(ValueforA);//christine it is moved from RNSRR constructor for A record
				recordsInAnswer[i] = new RRecord(rType,rClass,new_rrData,rTTL,rDomainName);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			} else if( rType==RRecord.TYPE_MX_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));			
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				int preference = (int)Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+10);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.print(IsAuthoritativeOrNot());
				System.out.println("	"+ "preference:"+ preference);
				
			}
				else if(rType==RRecord.TYPE_CNAME_RECORD){
					
					ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));			
					recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
					recordsInAnswer[i].RecordDetailsPrint();
					System.out.println(IsAuthoritativeOrNot());
				}
				
				else {
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
			}
			
			
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+valueLength;
		}
		
		if (numberofRRinadditionals()> 0)
			{	
			System.out.println(" *** Additional Section ("+numberofRRinadditionals()+") records ***");
			}
		
		//Use a FOR loop to go through the Additional field
		for(int i=0;i<numberofRRinadditionals();i++){	

			rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex));
			name_LengthInBytes = hostnametoStringLen(datacontent, newRecordStartIndex);
			rType  = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes);
			rClass = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+2);
			rTTL   = Conversion.byte4tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+4);
			valueLength = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+8);
			
			if(rType==RRecord.TYPE_NS_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			}else if(rType==RRecord.TYPE_A_RECORD){
				ValueforA =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) 
				{
				ValueforA[j] = datacontent[newRecordStartIndex+name_LengthInBytes+10+j];		
				}
				String new_rrData = Conversion.bytesTowholeIPString(ValueforA);
				recordsInAnswer[i] = new RRecord(rType,rClass,new_rrData,rTTL,rDomainName);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			} else if( rType==RRecord.TYPE_MX_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));			
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				int preference = (int)Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+10);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.print(IsAuthoritativeOrNot());
				System.out.println("	"+ "preference:"+ preference);
				
			}
				else if(rType==RRecord.TYPE_CNAME_RECORD){
					ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
					recordsInAnswer[i].RecordDetailsPrint();		
				System.out.println(IsAuthoritativeOrNot());
				}

				else { 
					System.out.println("ERROR: Incorrect input syntax: the input syntax does not allow to get a response with valid resource records");//christine debug and change it to incorrect by myself
					return;
			}
			
		
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+valueLength;
		// Increment the index for a new RRecord 
		}
		
	}
	//Getter
	public byte[] Getwholedata(){
		return datacontent;
	}
	

	public boolean IsAuthoritativeOrNot(){
		return Conversion.byteToBitset(datacontent[2]).get(2);
	}


	public int RCODEreader(){
		BitSet firstbits = Conversion.byteToBitset(datacontent[3]);
		BitSet secondbits = new BitSet();
		secondbits.set(0, firstbits.get(0));
		secondbits.set(1, firstbits.get(1));
		secondbits.set(2, firstbits.get(2));
		secondbits.set(3, firstbits.get(3));
		int rcodeInt;
		rcodeInt = (int)Conversion.bitstobyte(secondbits);
		return rcodeInt;
	}
	
	// Get the 6th and 7th byte in order to Know the number of RR in answer
	public int numberofRRinanwers(){              		    
		return (int)Conversion.byte2tointeger(datacontent,6);
	}
	// Get the 8th and 9th byte in order to Know the number of RR in authoritative
	public int numberofRRinauthor(){
		return (int)Conversion.byte2tointeger(datacontent,8);
	}
	//Get the 10th and 11th byte in order to Know the number of RR in answer
	public int numberofRRinadditionals(){
		return (int)Conversion.byte2tointeger(datacontent,10);
	}

	//Getter to get one RR in Authoritative
	public RRecord GetrecordsInAuthoritative(int i){
		return recordsInAuthoritative[i];
	}
	
	

	//Check whether is a pointer
    private boolean BoolPointer (byte b){
    	BitSet bits = Conversion.byteToBitset(b);
    	if(bits.get(7) && bits.get(6)){
    		return true;
    	}	    	
    	return false;
    }      
    //To interpret the pointer, result in an integer 
    private int DecodePointer(byte[] b,int index){   
    	//Create a two byte array in order to store the value of pointer
    	byte meaning[] = new byte[2];
    	
    	
    	BitSet bits  = Conversion.byteToBitset(b[index]);
    	bits.set(6, false); 
    	bits.set(7, false);
    	meaning[0] = Conversion.bitstobyte(bits);
    	// store directly the datacontent[index+1] following the byte containing 11
    	meaning[1] = b[index+1]; 	
    	return Conversion.byte2tointeger(meaning,0);
    }

    // This allows to get a string format host name information from a sequence of bytes
    // ie. it converts some bytes and get a string in format of "3www7youtube2ca"
    private String hostnametoString(int index){
    	String hostnamewithnumletters = "";
    	
    	// a byte ending with 0x00 indicates the end of a host name
    	while(datacontent[index]!= 0x00){
    		if(!BoolPointer(datacontent[index])){
    			hostnamewithnumletters += (char)datacontent[index];
    			index++;
    		}else{
    			hostnamewithnumletters += hostnametoString(DecodePointer(datacontent,index));
    			return hostnamewithnumletters;
    		}
    	}
    	
    	return hostnamewithnumletters;
    }
    // Get the length of hostnametoString
    private int hostnametoStringLen(byte[] in, int index){
    	int hostnamewithnumlettersLength = 0;
    	// a byte ending with 0x00 indicates the end of a host name
    	while(in[index] != 0x00){
    		hostnamewithnumlettersLength++;
    		index++;
    	}
    	return hostnamewithnumlettersLength;
    }

	
	
}

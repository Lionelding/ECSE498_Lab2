import java.util.BitSet;
import java.util.Random;

public class Packet {

//Field
	
	/*private byte[] data;
	private RRecord[] recordsInAnswer; 
	private RRecord[] recordsInAuthoritative;
	private RRecord[] recordsInAdditional;

	private String domain;*/
//christine change order:
	// Array composed of unknown number of resource records
	private RRecord[] recordsInAnswer; 
	private RRecord[] recordsInAuthoritative;
	private RRecord[] recordsInAdditional;

	// The domain name that the query and the response are about
	private String domainname;
	// This contains all the bytes that construct a whole DNS packet
	private byte[] datacontent;
//Constructors

	public Packet(){
		//EMPTY Constructor
	}
/*	public Packet(String domainnameName){
		createQuery(domainnameName);
	}*/
/*	public Packet(byte[] b, String domainnameName){
		data = b;
		domainname = domainnameName;
		readRecords();
	}	*/
//Methods
	
	 //!!!constructs the header data of a DNS query and sets the domainnameName to query
	public Packet(String AskedDomainName, int TYPEtemp){
	//private void createQuery(String domainName){
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!just create all before RR section end ARCOUNT
		//Populate header
		/*domain = domainName;
		data = new byte[18 + domainName.length()]; */  //18+length is the number of bytes for covering all the information in header and question sections. 
		// christine:
		domainname = AskedDomainName;
		/*data = new byte[18 + domainName.length()]; */
		// christine: this part creates all the needed bytes to stores all info for 
		// the part of dns packet except the 3 record sections
		int domain_length = domainname.length();
		int data_length_prerecord = domain_length+16+2;//domainwww.eee.ca will be bytes3www3eee3ca0:2:one for 0x00 one for the number bef www
		datacontent = new byte[data_length_prerecord];
		
		Random rand = new Random();
		byte[] b1 = new byte[1];
		rand.nextBytes(b1);
		byte[] b2 = new byte[1];
		rand.nextBytes(b2);
		datacontent[0] = b1[0]; 
		datacontent[1] = b2[0]; 
		
	
		//!!!Bytes 2,3: Flags, need to change later
		//data[2] = Byte.parseByte("00000000", 2); data[3] = Byte.parseByte("00000000", 2);  //Parse the string into binary

		// This part is to store the flags
		//data[2] = 0x01 is to indicate that we desire recursion
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
		
		
	/*	//TODO: Randomise DNS Packet ID
		//Bytes 0,1: ID  --NEETS TO BE RANDOMISED LATER
		data[0] = 0x00; data[1] = 0x0F;
		//!!!Bytes 2,3: Flags, need to change later
		data[2] = Byte.parseByte("00000000", 2); data[3] = Byte.parseByte("00000000", 2);  //Parse the string into binary

		
		//Bytes 4,5: Question Count (1)
		data[4] = 0x00; data[5] = 0x01;
		
		//Reply Header Info(Not for requests)
		//Bytes 6,7: Answer Count, Bytes 8,9: Name Server Count, Bytes 10,11: Aditional Resource Count
		data[6] = 0x00; data[7] = 0x00; 
		data[8] = 0x00; data[9] = 0x00; 
		data[10] = 0x00; data[11] = 0x00;

			*/
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
		
		datacontent[seqNumber-1] = (byte)(seqCount);
		// To end a domain name always with a byte 0x00
		datacontent[13 + domain_length] = 0x00; 
		//Question Bytes:
		/*int ppPosition = 12; //Byte Location for prepend value (1st one is 12)
		int dnCount = 0; //Used to keep track for the name prepend count
		
		//will insert the data[13,14,15] first and data[12]. so on and so f
		for(int i=0;i<domainName.length();i++){   
			if(domainName.charAt(i) == '.'){
				//prepend length byte
				data[ppPosition] = (byte)(int)(dnCount);
				ppPosition += dnCount +1;
				dnCount=0; i++;
			}
			
			data[ppPosition+dnCount+1] = (byte)domainName.charAt(i);
			dnCount++;
		}
		data[ppPosition] = (byte)(dnCount);
		
		data[13 + domainName.length()] = 0x00; //End of the domain name 
		*/
		//Question Type; Question Class
		//QTYPE=0001:Host Domain  QCLASS=0001:Internet 
		//!!! Need to consider other types NX, and MX queries.
		//christine
		/*data[14 + domainName.length()] = 0x00; data[15 + domainName.length()] = 0x01;  //Always type A
		data[16 + domainName.length()] = 0x00; data[17 + domainName.length()] = 0x01;  //Always 1
		*/
		//readRecords();
		//Question Type; Question Class
		//QTYPE=0001:Host Domain  QCLASS=0001:Internet 
		//!!! Need to consider other types NX, and MX queries.
		//christine change domain.leng...to domain_lenght
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
		 //Always type A
		// christine: 16-bit code specifying the class of the query
		// 0x01 representing an Internet address
		datacontent[16 + domain_length] = 0x00; 
		datacontent[17 + domain_length] = 0x01;  //Always 1
		/*
		readRecords();*/
		// christine try to erase readrecord think not needed
	}	
	
/*	private void readRecords(){
		//Alocates arrays
		recordsInAnswer = new RRecord[this.noAnswers()];
		recordsInAuthoritative = new RRecord[this.noAuthoritive()];
		recordsInAdditional = new RRecord[this.noAditional()];
		*/
		/*private void readRecords(){*/// christine replaced
		public Packet(byte[] bytesAllPacket, String AskedDomainName, long Starttime, int TriesCount){
			
		//Christine: Create necessary of array for store 3 types of
		// Resource records
		// Store the concerned domain name
		// Store the whole byte packet into variable : data
			this.domainname = AskedDomainName;
			this.datacontent = bytesAllPacket;
		this.recordsInAnswer = new RRecord[this.numberofRRinanwers()];
		this.recordsInAuthoritative = new RRecord[this.numberofRRinauthor()];
		this.recordsInAdditional = new RRecord[this.numberofRRinadditionals()];
		
		
		//christine try to create error code
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
		//christine
		
		/*String rDomainName = "";
		int rType;  	//two octets containing one of the RR TYPE codes.
		int rClass; 	//two octets containing one of the RR CLASS codes.
		int rTTL;		//a 32 bit signed integer that specifies the time interval for caching  
		
		byte[] rrData;  //For type A and AAAA recs
		String stData = "";  //For CNAME and NS
		
		//START:
		int newRecordStartIndex = 18 + domain.length();
		int valueLength = 0;
		int name_LengthInBytes = 0;*/
		
		
		//Loop though all Answer Records
		String rDomainName = "";
		int rType;  	//two octets containing one of the RR TYPE codes.
		int rClass; 	//two octets containing one of the RR CLASS codes.
		int rTTL;		//a 32 bit signed integer that specifies the time interval for caching  
		int valueLength;
		
		byte[] ValueforA;  //For type A and AAAA recs
		String ValuefornotA = "";  //For CNAME and NS
		
		//START:
		
		//christine newRecordStartIndex become newRecordStartIndex
		int domain_length = domainname.length();
		//christine: newRecordStartIndex is know the first byte index after all pre RR: first RR place to extract
		int newRecordStartIndex = domain_length+16+2;
		int name_LengthInBytes=0;
	
		/*int valueLength = 0;
		int name_LengthInBytes = 0;*/// deplaced put up and put inside!
		
		/*System.out.println("first or send sendoack "+(int)Conversion.byteToShort(data,4));
		System.out.println("first or send sendoack "+(int)Conversion.byteToShort(data,6));
		*///by us no need
		
		// christine
		
		// christine
		if (numberofRRinanwers()> 0)
			{	
			System.out.println("Response received after " + (System.nanoTime() - Starttime)/Math.pow(10, 9) + " seconds "+TriesCount+" retries");
			
			System.out.println(" *** Answer Section ("+numberofRRinanwers()+") records ***");
			}
		
		for(int i=0;i<numberofRRinanwers();i++){
			//!!! Need to look at why
/*			if(Conversion.rDomainNameIsRoot(data[newRecordStartIndex])){
				rDomainName = "<Root>";	//Set the name as root
				name_LengthInBytes = 1;
				System.out.println(" BINGOOOOO");
			} else {
				System.out.println(" BINGOOOOO22");
				//!!!Debug to see what is going on. 
				//System.out.println(hostnametoString(newRecordStartIndex));
				rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex)); //if not then name is a pointer
				//Ststem.our.println(rDomainName);
				name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
			}	*/
			
			//!!! Need to look at why
			/*		if(Conversion.rDomainNameIsRoot(data[curPos])){
						rDomainName = "<Root>";	//Set the name as root
						nameLen = 1;
					} else {
						//!!!Debug to see what is going on. 
						// christine suggest to delete the if and else
						//System.out.println(hostnametoString(curPos));
						rDomainName  = Conversion.addrseqtodots(hostnametoString(curPos)); //if not then name is a pointer
						//Ststem.our.println(rDomainName);
						nameLen = hostnametoStringLen(data, curPos);
					}	*///christine:namelen = name_LengthInBytes
					
					//christine replace root part:
					rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex));
					name_LengthInBytes = hostnametoStringLen(datacontent, newRecordStartIndex);
		
		
			//!!! Need to change the name and algorithm
					//christinein dnsprimer is all after NAME in RR
			rType  = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes);
			rClass = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+2);
			rTTL   = Conversion.byte4tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+4);
			valueLength = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+8);
			
			
			//!!! christine applied to next two section if else loop copie pasted 
			if(rType==RRecord.TYPE_NS_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
				// christine for MXrecord preference
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			}else if(rType==RRecord.TYPE_A_RECORD){
				ValueforA =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) 
					{
					ValueforA[j] = datacontent[newRecordStartIndex+name_LengthInBytes+10+j];		
					}
				//for(int j=0;j<valueLength;j++) ValueforA[j] = datacontent[newRecordStartIndex+name_LengthInBytes+10+j];		
				String new_rrData = Conversion.bytesTowholeIPString(ValueforA);//christine it is moved from RNSRR constructor for A record
				recordsInAnswer[i] = new RRecord(rType,rClass,new_rrData,rTTL,rDomainName);
				
				
				// christine
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			} else if( rType==RRecord.TYPE_MX_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));
				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
				// christine for MXrecord preference
				
				// christine for MXrecord preference
				int preference = (int)Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+10);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.print(IsAuthoritativeOrNot());
				System.out.println("	"+ "preference:"+ preference);
				
			}//christine
				else if(rType==RRecord.TYPE_CNAME_RECORD){
					
					ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					
					recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
					
					/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
							
					recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
					// christine for MXrecord preference
					recordsInAnswer[i].RecordDetailsPrint();
			
				System.out.println(IsAuthoritativeOrNot());
				}
				
				//christine
				else { //All other records , SOA
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
					/*rrData =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,rrData);
				// christine
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());*///christine comment out
			}
			
			
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+valueLength;
		}
		
		//christine
		if (numberofRRinauthor()> 0)
		{	
		System.out.println(" *** Authoritive Section ("+numberofRRinauthor()+") records ***");
		}
		//Loop though all Authoritative Records
		for(int i=0;i<numberofRRinauthor();i++){
			//!!! Need to look at why
			/*			if(Conversion.rDomainNameIsRoot(data[newRecordStartIndex])){
							rDomainName = "<Root>";	//Set the name as root
							name_LengthInBytes = 1;
							System.out.println(" BINGOOOOO");
						} else {
							System.out.println(" BINGOOOOO22");
							//!!!Debug to see what is going on. 
							//System.out.println(hostnametoString(newRecordStartIndex));
							rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex)); //if not then name is a pointer
							//Ststem.our.println(rDomainName);
							name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
						}	*/
						
						//!!! Need to look at why
						/*		if(Conversion.rDomainNameIsRoot(data[curPos])){
									rDomainName = "<Root>";	//Set the name as root
									nameLen = 1;
								} else {
									//!!!Debug to see what is going on. 
									// christine suggest to delete the if and else
									//System.out.println(hostnametoString(curPos));
									rDomainName  = Conversion.addrseqtodots(hostnametoString(curPos)); //if not then name is a pointer
									//Ststem.our.println(rDomainName);
									nameLen = hostnametoStringLen(data, curPos);
								}	*///christine:namelen = name_LengthInBytes
								
								//christine replace root part:
								rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex));
								name_LengthInBytes = hostnametoStringLen(datacontent, newRecordStartIndex);
					
					
			
			rType  = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes);
			rClass = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+2);
			rTTL   = Conversion.byte4tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+4);
			valueLength = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+8);
			//!!! christine debug data length
			
			//BitSet set = BitSet.valueOf(new byte[]{data[3]});

			//for(int k=0; k< set.length();k++){
				
			//}
			
			//System.out.println(set. + " and " + (byte)data[3]);
			//!!! christine
			//!!!NS Record || MX Record
			//!!!NS Record || MX Record
			//!!! christine applied to next two section if else loop copie pasted 
			if(rType==RRecord.TYPE_NS_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
				// christine for MXrecord preference
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
				
				
				// christine
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			} else if( rType==RRecord.TYPE_MX_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));
				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
				// christine for MXrecord preference
				
				// christine for MXrecord preference
				int preference = (int)Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+10);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.print(IsAuthoritativeOrNot());
				System.out.println("	"+ "preference:"+ preference);
				
			}//christine
				else if(rType==RRecord.TYPE_CNAME_RECORD){
					
					ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					
					recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
					
					/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
							
					recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
					// christine for MXrecord preference
					recordsInAnswer[i].RecordDetailsPrint();
			
				System.out.println(IsAuthoritativeOrNot());
				}
				
				//christine
				else { //All other records , SOA
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
					/*rrData =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,rrData);
				// christine
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());*///christine comment out
			}
			
			
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+valueLength;
		}
		
		// christine
		if (numberofRRinadditionals()> 0)
			{	
			System.out.println(" *** Additional Section ("+numberofRRinadditionals()+") records ***");
			}
		
		//Loop though all Aditional Records
		for(int i=0;i<numberofRRinadditionals();i++){	
			//!!! Need to look at why
			/*			if(Conversion.rDomainNameIsRoot(data[newRecordStartIndex])){
							rDomainName = "<Root>";	//Set the name as root
							name_LengthInBytes = 1;
							System.out.println(" BINGOOOOO");
						} else {
							System.out.println(" BINGOOOOO22");
							//!!!Debug to see what is going on. 
							//System.out.println(hostnametoString(newRecordStartIndex));
							rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex)); //if not then name is a pointer
							//Ststem.our.println(rDomainName);
							name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
						}	*/
						
						//!!! Need to look at why
						/*		if(Conversion.rDomainNameIsRoot(data[curPos])){
									rDomainName = "<Root>";	//Set the name as root
									nameLen = 1;
								} else {
									//!!!Debug to see what is going on. 
									// christine suggest to delete the if and else
									//System.out.println(hostnametoString(curPos));
									rDomainName  = Conversion.addrseqtodots(hostnametoString(curPos)); //if not then name is a pointer
									//Ststem.our.println(rDomainName);
									nameLen = hostnametoStringLen(data, curPos);
								}	*///christine:namelen = name_LengthInBytes
								
								//christine replace root part:
								rDomainName  = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex));
								name_LengthInBytes = hostnametoStringLen(datacontent, newRecordStartIndex);
					
					
			
			rType  = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes);
			rClass = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+2);
			rTTL   = Conversion.byte4tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+4);
			valueLength = Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+8);
			
			//!!!NS Record || MX Record
			//!!! christine applied to next two section if else loop copie pasted 
			if(rType==RRecord.TYPE_NS_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
				// christine for MXrecord preference
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			}else if(rType==RRecord.TYPE_A_RECORD){
				
				ValueforA =  new byte[valueLength];// chrisine: "de re de re" is 132 345 456 321
				for(int j=0;j<valueLength;j++) 
				{
				ValueforA[j] = datacontent[newRecordStartIndex+name_LengthInBytes+10+j];		
				}
				String new_rrData = Conversion.bytesTowholeIPString(ValueforA);//christine it is moved from RNSRR constructor for A record
				recordsInAnswer[i] = new RRecord(rType,rClass,new_rrData,rTTL,rDomainName);
				
				
				// christine
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(IsAuthoritativeOrNot());
			} else if( rType==RRecord.TYPE_MX_RECORD){
				ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));
				
				recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
				// christine for MXrecord preference
				
				// christine for MXrecord preference
				int preference = (int)Conversion.byte2tointeger(datacontent,newRecordStartIndex+name_LengthInBytes+10);
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.print(IsAuthoritativeOrNot());
				System.out.println("	"+ "preference:"+ preference);
				
			}//christine
				else if(rType==RRecord.TYPE_CNAME_RECORD){
				
					ValuefornotA = Conversion.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					
					recordsInAnswer[i] = new RRecord(rType,rClass,rTTL,ValuefornotA,rDomainName);
					
					/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
							
					recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,stData);*/
					// christine for MXrecord preference
					recordsInAnswer[i].RecordDetailsPrint();
			
				System.out.println(IsAuthoritativeOrNot());
				}
				
				//christine
				else { //All other records , SOA
					System.out.println("ERROR: Incorrect input syntax: the input syntax does not allow to get a response with valid resource records");//christine debug and change it to incorrect by myself
					return;
					/*rrData =  new byte[valueLength];
				for(int j=0;j<valueLength;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				
				recordsInAnswer[i] = new RRecord(rDomainName,rType,rClass,rTTL,rrData);
				// christine
				recordsInAnswer[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());*///christine comment out
			}
			
		
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+valueLength;
		// christine: to begin a new RR after all this lenght from NAME to RDATA
		}
		
	}
	//!!! Need to be removed later
	/**
	 * This method populates an empty packet with an arraylist of bytes. This is used to populate
	 * the dns packet once a reply of bytes is received.
	 * @param rawData The array of bytes.
	 * @param domainName A string of the domain that has been queried for in the packet question.
	 */  
	public byte[] Getwholedata(){
		return datacontent;
	}
	

	/**
	 * Packet Return functions
	 */  
/*	public boolean isResponse(){
		return Conversion.byteToBitset(data[2]).get(7);
	}//christinee remove
	public boolean isTruncated(){
		return Conversion.byteToBitset(data[2]).get(1);
	}*///christinee remove
	public boolean IsAuthoritativeOrNot(){
		return Conversion.byteToBitset(datacontent[2]).get(2);
	}
	/*public boolean isRecursAvail(){
		return Conversion.byteToBitset(data[3]).get(7);
	}*/	//christinee remove
/*	public boolean isError(){
		return (Conversion.byteToBitset(data[3]).get(0) 
			 || Conversion.byteToBitset(data[3]).get(1) 
			 || Conversion.byteToBitset(data[3]).get(2)
			 || Conversion.byteToBitset(data[3]).get(3));
	}*/
	//!!! This is not called by other methods
	/**
	 * 0: No error condition
	 * 1: Format error - The name server was unable to interpret the query. 
	 * 2: Server failure - The name server was unable to process this query due to a problem with the name server.
	 * 3: Name Error - Meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist.
	 * 4: Not Implemented - The name server does not support the requested kind of query.
	 * 5: Refused - The name server refuses to perform the specified operation for policy reasons.
	 **/	
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
	
	/**
	 * Return number of records for each section
	 */  
	public int numberofRRinanwers(){
		//6,7                      		    
		return (int)Conversion.byte2tointeger(datacontent,6);
	}
	public int numberofRRinauthor(){
		//8,9
		return (int)Conversion.byte2tointeger(datacontent,8);
	}
	public int numberofRRinadditionals(){
		//10,11
		return (int)Conversion.byte2tointeger(datacontent,10);
	}

	
	/**
	 * These methods return DNS_records
	 * @param i The index of the record
	 * @param i The index of the record
	 * @return DNS_record The coresponding DNS record.
	 */  
	
	public RRecord GetrecordsInAuthoritative(int i){
		return recordsInAuthoritative[i];
	}
	
	

	/**
	 * This method will check weather a specific byte is a pointer to a label in the packet.
	 * @param b The byte that needs to be checked
	 * @return boolean True if the byte is a pointer to another label.
	 */
    private boolean BoolPointer (byte b){
    	BitSet bits = Conversion.byteToBitset(b);
    	
    	if(bits.get(7) && bits.get(6)){
    		return true;
    	}
    	    	
    	return false;
    }      
    //!!! Need to see the output to have better understanding
	/**
	 * This method will check weather a specific byte is a pointer to a label in the packet.
	 * @param b This is an array of bytes
	 * @param offset This is the start of the pointer in the byte array.
	 * @return int A pointer offset number from 
	 */    
    private int DecodePointer(byte[] b,int index){   //byte b, byte b2){
    	if(!BoolPointer(b[index])) return -1; //checks byte is a pointer
    	byte meaning[] = new byte[2];
    	
    	/*						b                          b2
    	 * 0  	1  	2  	3  	4  	5  	6  	7  	8  	9	10  11  12  13  14  15
		   1 	1 	                       OFFSET
    	 */   	
    	
    	//Pads the 11s and returns new byte
    	BitSet bits  = Conversion.byteToBitset(b[index]);
    	bits.set(6, false); 
    	bits.set(7, false);
    	meaning[0] = Conversion.bitstobyte(bits);
    	meaning[1] = b[index+1];
    	
    	return Conversion.byte2tointeger(meaning,0);
    }

	/**
	 * This method will recursivly using the above two functions to return a full label
	 * @param offset This is the start of the label
	 * @return String A string of the label read out
	 */        
    private String hostnametoString(int index){
    	String hostnamewithnumletters = "";
    	
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
	
	/**
	 * This function returns the full length (in sequential bytes) of any record label
	 * This is used for knowing the offset from the name which is 
	 * used to pull all other information form the record
	 * NOTE: For full length use hostnametoString(offset).length
	 */    
    private int hostnametoStringLen(byte[] in, int index){
    	//Loop until 0 byte encountered return count
    	int hostnamewithnumlettersLength = 0;
    	
    	//Loop till 0 is found (start of type
    	while(in[index] != 0x00){
    		hostnamewithnumlettersLength++;
    		index++;
    	}
    	
    	return hostnamewithnumlettersLength;
    }

	
	
}

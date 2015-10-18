import java.util.BitSet;
import java.util.Random;

public class DnsPacket {

//Field
	
	/*private byte[] data;
	private DnsRR[] recAns; 
	private DnsRR[] recAuth;
	private DnsRR[] recAdd;

	private String domain;*/
//christine change order:
	// Array composed of unknown number of resource records
	private DnsRR[] recAns; 
	private DnsRR[] recAuth;
	private DnsRR[] recAdd;

	// The domain name that the query and the response are about
	private String domain;
	// This contains all the bytes that construct a whole DNS packet
	private byte[] data;
//Constructors

	public DnsPacket(){
		//EMPTY Constructor
	}
/*	public DnsPacket(String domainName){
		createQuery(domainName);
	}*/
/*	public DnsPacket(byte[] b, String domainName){
		data = b;
		domain = domainName;
		readRecords();
	}	*/
//Methods
	
	 //!!!constructs the header data of a DNS query and sets the domainName to query
	public DnsPacket(String AskedDomainName, int TYPEtemp){
	//private void createQuery(String domainName){
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!just create all before RR section end ARCOUNT
		//Populate header
		/*domain = domainName;
		data = new byte[18 + domainName.length()]; */  //18+length is the number of bytes for covering all the information in header and question sections. 
		// christine:
		domain = AskedDomainName;
		/*data = new byte[18 + domainName.length()]; */
		// christine: this part creates all the needed bytes to stores all info for 
		// the part of dns packet except the 3 record sections
		int domain_length = domain.length();
		int data_length_prerecord = domain_length+16+2;//domainwww.eee.ca will be bytes3www3eee3ca0:2:one for 0x00 one for the number bef www
		data = new byte[data_length_prerecord];
		
		Random rand = new Random();
		byte[] b1 = new byte[1];
		rand.nextBytes(b1);
		byte[] b2 = new byte[1];
		rand.nextBytes(b2);
		data[0] = b1[0]; 
		data[1] = b2[0]; 
		
	
		//!!!Bytes 2,3: Flags, need to change later
		//data[2] = Byte.parseByte("00000000", 2); data[3] = Byte.parseByte("00000000", 2);  //Parse the string into binary

		// This part is to store the flags
		//data[2] = 0x01 is to indicate that we desire recursion
		data[2] = 0x01; 
		data[3] = 0x00;
		// Number of questions is 1; number of resource record in answer
		// section; number of resource record in name server section; 
		// number of resource record in additional section is all 0 
		data[4] = 0x00; 
		data[5] = 0x01;
		data[6] = 0x00; 
		data[7] = 0x00; 
		data[8] = 0x00; 
		data[9] = 0x00; 
		data[10] = 0x00; 
		data[11] = 0x00;
		
		
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
					data[seqNumber-1] = (byte)(int)(seqCount);
					seqNumber += seqCount+1 ;
					seqCount=0;
					
					}
				else
				{
				
					data[seqNumber+seqCount] = (byte)AskedDomainName.charAt(i);
					seqCount++;
				}
					
			}
		
		data[seqNumber-1] = (byte)(seqCount);
		// To end a domain name always with a byte 0x00
		data[13 + domain_length] = 0x00; 
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
		data[14 + domain_length] = 0x00; 
		if (TYPEtemp == 1)
		{
			data[15 + domain_length] = 0x01; 	
		}else if(TYPEtemp == 2)
		{
			data[15 + domain_length] = 0x02; 
		}
		else if(TYPEtemp == 15)
		{
			data[15 + domain_length] = 0x0f; 
		}
		 //Always type A
		// christine: 16-bit code specifying the class of the query
		// 0x01 representing an Internet address
		data[16 + domain_length] = 0x00; 
		data[17 + domain_length] = 0x01;  //Always 1
		/*
		readRecords();*/
		// christine try to erase readrecord think not needed
	}	
	
/*	private void readRecords(){
		//Alocates arrays
		recAns = new DnsRR[this.noAnswers()];
		recAuth = new DnsRR[this.noAuthoritive()];
		recAdd = new DnsRR[this.noAditional()];
		*/
		/*private void readRecords(){*/// christine replaced
		public DnsPacket(byte[] bytesAllPacket, String AskedDomainName, long Starttime, int TriesCount){
			
		//Christine: Create necessary of array for store 3 types of
		// Resource records
		// Store the concerned domain name
		// Store the whole byte packet into variable : data
			this.domain = AskedDomainName;
			this.data = bytesAllPacket;
		this.recAns = new DnsRR[this.noAnswers()];
		this.recAuth = new DnsRR[this.noAuthoritive()];
		this.recAdd = new DnsRR[this.noAditional()];
		
		
		//christine try to create error code
		if (errorCode() == 0)
		{
			
		}
		else if (errorCode() == 1)
		{
			System.out.println("ERRORCODE = Format error: the name server was unable to interpret the query");
			System.exit(0); 

		}
		else if (errorCode() == 2)
		{
			System.out.println("ERRORCODE = Server failure: the name server was unable to process this query due to a problem with the name server");
			System.exit(0); 

		}
		else if (errorCode() == 3)
		{
			System.out.println("ERRORCODE = NOTFOUND");
			System.exit(0); 

		}
		else if (errorCode() == 4)
		{
			System.out.println("ERRORCODE = Not implemented: the name server does not support the requested kind of query");
			System.exit(0); 

		}
		else if (errorCode() == 5)
		{
			System.out.println("ERRORCODE = Refused: the name server refuses to perform the requested operation for policy reasons");
			System.exit(0); 

		}
		//christine
		
		/*String rrName = "";
		int rrType;  	//two octets containing one of the RR TYPE codes.
		int rrClass; 	//two octets containing one of the RR CLASS codes.
		int rrTTL;		//a 32 bit signed integer that specifies the time interval for caching  
		
		byte[] rrData;  //For type A and AAAA recs
		String stData = "";  //For CNAME and NS
		
		//START:
		int newRecordStartIndex = 18 + domain.length();
		int dataLen = 0;
		int name_LengthInBytes = 0;*/
		
		
		//Loop though all Answer Records
		String rrName = "";
		int rrType;  	//two octets containing one of the RR TYPE codes.
		int rrClass; 	//two octets containing one of the RR CLASS codes.
		int rrTTL;		//a 32 bit signed integer that specifies the time interval for caching  
		int dataLen;
		
		byte[] rrData;  //For type A and AAAA recs
		String stData = "";  //For CNAME and NS
		
		//START:
		
		//christine newRecordStartIndex become newRecordStartIndex
		int domain_length = domain.length();
		//christine: newRecordStartIndex is know the first byte index after all pre RR: first RR place to extract
		int newRecordStartIndex = domain_length+16+2;
		int name_LengthInBytes=0;
	
		/*int dataLen = 0;
		int name_LengthInBytes = 0;*/// deplaced put up and put inside!
		
		/*System.out.println("first or send sendoack "+(int)BitMath.byteToShort(data,4));
		System.out.println("first or send sendoack "+(int)BitMath.byteToShort(data,6));
		*///by us no need
		
		// christine
		
		// christine
		if (noAnswers()> 0)
			{	
			System.out.println("Response received after " + (System.nanoTime() - Starttime)/Math.pow(10, 9) + " seconds "+TriesCount+" retries");
			
			System.out.println(" *** Answer Section ("+noAnswers()+") records ***");
			}
		
		for(int i=0;i<noAnswers();i++){
			//!!! Need to look at why
/*			if(BitMath.rrNameIsRoot(data[newRecordStartIndex])){
				rrName = "<Root>";	//Set the name as root
				name_LengthInBytes = 1;
				System.out.println(" BINGOOOOO");
			} else {
				System.out.println(" BINGOOOOO22");
				//!!!Debug to see what is going on. 
				//System.out.println(hostnametoString(newRecordStartIndex));
				rrName  = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex)); //if not then name is a pointer
				//Ststem.our.println(rrName);
				name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
			}	*/
			
			//!!! Need to look at why
			/*		if(BitMath.rrNameIsRoot(data[curPos])){
						rrName = "<Root>";	//Set the name as root
						nameLen = 1;
					} else {
						//!!!Debug to see what is going on. 
						// christine suggest to delete the if and else
						//System.out.println(hostnametoString(curPos));
						rrName  = BitMath.addrseqtodots(hostnametoString(curPos)); //if not then name is a pointer
						//Ststem.our.println(rrName);
						nameLen = hostnametoStringLen(data, curPos);
					}	*///christine:namelen = name_LengthInBytes
					
					//christine replace root part:
					rrName  = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex));
					name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
		
		
			//!!! Need to change the name and algorithm
					//christinein dnsprimer is all after NAME in RR
			rrType  = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes);
			rrClass = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+2);
			rrTTL   = BitMath.byte4tointeger(data,newRecordStartIndex+name_LengthInBytes+4);
			dataLen = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+8);
			
			
			//!!! christine applied to next two section if else loop copie pasted 
			if(rrType==DnsRR.TYPE_NS_RECORD){
				stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				
				recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
				// christine for MXrecord preference
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());
			}else if(rrType==DnsRR.TYPE_A_RECORD){
				
				rrData =  new byte[dataLen];
				for(int j=0;j<dataLen;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				String new_rrData = BitMath.bytesTowholeIPString(rrData);//christine it is moved from RNSRR constructor for A record
				recAns[i] = new DnsRR(rrType,rrClass,new_rrData,rrTTL,rrName);
				
				
				// christine
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());
			} else if( rrType==DnsRR.TYPE_MX_RECORD){
				stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));
				
				recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
				// christine for MXrecord preference
				
				// christine for MXrecord preference
				int preference = (int)BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+10);
				recAns[i].RecordDetailsPrint();
				System.out.print(isAuthOfDomain());
				System.out.println("	"+ "preference:"+ preference);
				
			}//christine
				else if(rrType==DnsRR.TYPE_CNAME_RECORD){
					
					stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					
					recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
					
					/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
							
					recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
					// christine for MXrecord preference
					recAns[i].RecordDetailsPrint();
			
				System.out.println(isAuthOfDomain());
				}
				
				//christine
				else { //All other records , SOA
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
					/*rrData =  new byte[dataLen];
				for(int j=0;j<dataLen;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,rrData);
				// christine
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());*///christine comment out
			}
			
			
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+dataLen;
		}
		
		//christine
		if (noAuthoritive()> 0)
		{	
		System.out.println(" *** Authoritive Section ("+noAuthoritive()+") records ***");
		}
		//Loop though all Authoritative Records
		for(int i=0;i<noAuthoritive();i++){
			//!!! Need to look at why
			/*			if(BitMath.rrNameIsRoot(data[newRecordStartIndex])){
							rrName = "<Root>";	//Set the name as root
							name_LengthInBytes = 1;
							System.out.println(" BINGOOOOO");
						} else {
							System.out.println(" BINGOOOOO22");
							//!!!Debug to see what is going on. 
							//System.out.println(hostnametoString(newRecordStartIndex));
							rrName  = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex)); //if not then name is a pointer
							//Ststem.our.println(rrName);
							name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
						}	*/
						
						//!!! Need to look at why
						/*		if(BitMath.rrNameIsRoot(data[curPos])){
									rrName = "<Root>";	//Set the name as root
									nameLen = 1;
								} else {
									//!!!Debug to see what is going on. 
									// christine suggest to delete the if and else
									//System.out.println(hostnametoString(curPos));
									rrName  = BitMath.addrseqtodots(hostnametoString(curPos)); //if not then name is a pointer
									//Ststem.our.println(rrName);
									nameLen = hostnametoStringLen(data, curPos);
								}	*///christine:namelen = name_LengthInBytes
								
								//christine replace root part:
								rrName  = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex));
								name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
					
					
			
			rrType  = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes);
			rrClass = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+2);
			rrTTL   = BitMath.byte4tointeger(data,newRecordStartIndex+name_LengthInBytes+4);
			dataLen = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+8);
			//!!! christine debug data length
			
			//BitSet set = BitSet.valueOf(new byte[]{data[3]});

			//for(int k=0; k< set.length();k++){
				
			//}
			
			//System.out.println(set. + " and " + (byte)data[3]);
			//!!! christine
			//!!!NS Record || MX Record
			//!!!NS Record || MX Record
			//!!! christine applied to next two section if else loop copie pasted 
			if(rrType==DnsRR.TYPE_NS_RECORD){
				stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				
				recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
				// christine for MXrecord preference
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());
			}else if(rrType==DnsRR.TYPE_A_RECORD){
				
				rrData =  new byte[dataLen];
				for(int j=0;j<dataLen;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				String new_rrData = BitMath.bytesTowholeIPString(rrData);//christine it is moved from RNSRR constructor for A record
				recAns[i] = new DnsRR(rrType,rrClass,new_rrData,rrTTL,rrName);
				
				
				// christine
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());
			} else if( rrType==DnsRR.TYPE_MX_RECORD){
				stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));
				
				recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
				// christine for MXrecord preference
				
				// christine for MXrecord preference
				int preference = (int)BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+10);
				recAns[i].RecordDetailsPrint();
				System.out.print(isAuthOfDomain());
				System.out.println("	"+ "preference:"+ preference);
				
			}//christine
				else if(rrType==DnsRR.TYPE_CNAME_RECORD){
					
					stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					
					recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
					
					/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
							
					recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
					// christine for MXrecord preference
					recAns[i].RecordDetailsPrint();
			
				System.out.println(isAuthOfDomain());
				}
				
				//christine
				else { //All other records , SOA
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
					/*rrData =  new byte[dataLen];
				for(int j=0;j<dataLen;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,rrData);
				// christine
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());*///christine comment out
			}
			
			
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+dataLen;
		}
		
		// christine
		if (noAditional()> 0)
			{	
			System.out.println(" *** Additional Section ("+noAditional()+") records ***");
			}
		
		//Loop though all Aditional Records
		for(int i=0;i<noAditional();i++){	
			//!!! Need to look at why
			/*			if(BitMath.rrNameIsRoot(data[newRecordStartIndex])){
							rrName = "<Root>";	//Set the name as root
							name_LengthInBytes = 1;
							System.out.println(" BINGOOOOO");
						} else {
							System.out.println(" BINGOOOOO22");
							//!!!Debug to see what is going on. 
							//System.out.println(hostnametoString(newRecordStartIndex));
							rrName  = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex)); //if not then name is a pointer
							//Ststem.our.println(rrName);
							name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
						}	*/
						
						//!!! Need to look at why
						/*		if(BitMath.rrNameIsRoot(data[curPos])){
									rrName = "<Root>";	//Set the name as root
									nameLen = 1;
								} else {
									//!!!Debug to see what is going on. 
									// christine suggest to delete the if and else
									//System.out.println(hostnametoString(curPos));
									rrName  = BitMath.addrseqtodots(hostnametoString(curPos)); //if not then name is a pointer
									//Ststem.our.println(rrName);
									nameLen = hostnametoStringLen(data, curPos);
								}	*///christine:namelen = name_LengthInBytes
								
								//christine replace root part:
								rrName  = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex));
								name_LengthInBytes = hostnametoStringLen(data, newRecordStartIndex);
					
					
			
			rrType  = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes);
			rrClass = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+2);
			rrTTL   = BitMath.byte4tointeger(data,newRecordStartIndex+name_LengthInBytes+4);
			dataLen = BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+8);
			
			//!!!NS Record || MX Record
			//!!! christine applied to next two section if else loop copie pasted 
			if(rrType==DnsRR.TYPE_NS_RECORD){
				stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
				
				recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
				// christine for MXrecord preference
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());
			}else if(rrType==DnsRR.TYPE_A_RECORD){
				
				rrData =  new byte[dataLen];// chrisine: "de re de re" is 132 345 456 321
				for(int j=0;j<dataLen;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				String new_rrData = BitMath.bytesTowholeIPString(rrData);//christine it is moved from RNSRR constructor for A record
				recAns[i] = new DnsRR(rrType,rrClass,new_rrData,rrTTL,rrName);
				
				
				// christine
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());
			} else if( rrType==DnsRR.TYPE_MX_RECORD){
				stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+12));
				
				recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
				
				/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
						
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
				// christine for MXrecord preference
				
				// christine for MXrecord preference
				int preference = (int)BitMath.byte2tointeger(data,newRecordStartIndex+name_LengthInBytes+10);
				recAns[i].RecordDetailsPrint();
				System.out.print(isAuthOfDomain());
				System.out.println("	"+ "preference:"+ preference);
				
			}//christine
				else if(rrType==DnsRR.TYPE_CNAME_RECORD){
				
					stData = BitMath.addrseqtodots(hostnametoString(newRecordStartIndex+name_LengthInBytes+10));
					
					recAns[i] = new DnsRR(rrType,rrClass,rrTTL,stData,rrName);
					
					/*stData = hostnametoString(newRecordStartIndex+name_LengthInBytes+10);
							
					recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,stData);*/
					// christine for MXrecord preference
					recAns[i].RecordDetailsPrint();
			
				System.out.println(isAuthOfDomain());
				}
				
				//christine
				else { //All other records , SOA
					System.out.println("ERROR	Incorrect input syntax: the domain name syntax does not allow the server to return a valid answer");//christine debug and change it to incorrect by myself
					return;
					/*rrData =  new byte[dataLen];
				for(int j=0;j<dataLen;j++) rrData[j] = data[newRecordStartIndex+name_LengthInBytes+10+j];		
				
				recAns[i] = new DnsRR(rrName,rrType,rrClass,rrTTL,rrData);
				// christine
				recAns[i].RecordDetailsPrint();
				System.out.println(isAuthOfDomain());*///christine comment out
			}
			
		
			newRecordStartIndex = newRecordStartIndex+name_LengthInBytes+10+dataLen;
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
	public byte[] packet(){
		return data;
	}
	

	/**
	 * Packet Return functions
	 */  
/*	public boolean isResponse(){
		return BitMath.byteToBitset(data[2]).get(7);
	}//christinee remove
	public boolean isTruncated(){
		return BitMath.byteToBitset(data[2]).get(1);
	}*///christinee remove
	public boolean isAuthOfDomain(){
		return BitMath.byteToBitset(data[2]).get(2);
	}
	/*public boolean isRecursAvail(){
		return BitMath.byteToBitset(data[3]).get(7);
	}*/	//christinee remove
/*	public boolean isError(){
		return (BitMath.byteToBitset(data[3]).get(0) 
			 || BitMath.byteToBitset(data[3]).get(1) 
			 || BitMath.byteToBitset(data[3]).get(2)
			 || BitMath.byteToBitset(data[3]).get(3));
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
	public int errorCode(){
		BitSet bs = BitMath.byteToBitset(data[3]);
		BitSet bsNew = new BitSet();
		
		bsNew.set(0, bs.get(0));
		bsNew.set(1, bs.get(1));
		bsNew.set(2, bs.get(2));
		bsNew.set(3, bs.get(3));

		return (int)BitMath.bitstobyte(bsNew);
	}
	
	/**
	 * Return number of records for each section
	 */  
	public int noAnswers(){
		//6,7                      		    
		return (int)BitMath.byte2tointeger(data,6);
	}
	public int noAuthoritive(){
		//8,9
		return (int)BitMath.byte2tointeger(data,8);
	}
	public int noAditional(){
		//10,11
		return (int)BitMath.byte2tointeger(data,10);
	}

	
	/**
	 * These methods return DNS_records
	 * @param i The index of the record
	 * @param i The index of the record
	 * @return DNS_record The coresponding DNS record.
	 */  
	public DnsRR getRRAns(int i){
		return recAns[i];
	}
	public DnsRR getRRAuth(int i){
		return recAuth[i];
	}
	public DnsRR getRRAdd(int i){
		return recAdd[i];
	}
	

	/**
	 * This method will check weather a specific byte is a pointer to a label in the packet.
	 * @param b The byte that needs to be checked
	 * @return boolean True if the byte is a pointer to another label.
	 */
    private boolean BoolPointer (byte b){
    	BitSet bits = BitMath.byteToBitset(b);
    	
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
    private int DecodePointer(byte[] b,int offset){   //byte b, byte b2){
    	if(!BoolPointer(b[offset])) return -1; //checks byte is a pointer
    	byte out[] = new byte[2];
    	
    	/*						b                          b2
    	 * 0  	1  	2  	3  	4  	5  	6  	7  	8  	9	10  11  12  13  14  15
		   1 	1 	                       OFFSET
    	 */   	
    	
    	//Pads the 11s and returns new byte
    	BitSet bits  = BitMath.byteToBitset(b[offset]);
    	bits.set(6, false); 
    	bits.set(7, false);
    	out[0] = BitMath.bitstobyte(bits);
    	out[1] = b[offset+1];
    	
    	return BitMath.byte2tointeger(out,0);
    }

	/**
	 * This method will recursivly using the above two functions to return a full label
	 * @param offset This is the start of the label
	 * @return String A string of the label read out
	 */        
    private String hostnametoString(int offset){
    	String out = "";
    	
    	while(data[offset]!= 0x00){
    		if(!BoolPointer(data[offset])){
    			out += (char)data[offset];
    			offset++;
    		}else{
    			out += hostnametoString(DecodePointer(data,offset));
    			return out;
    		}
    	}
    	
    	return out;
    }
	
	/**
	 * This function returns the full length (in sequential bytes) of any record label
	 * This is used for knowing the offset from the name which is 
	 * used to pull all other information form the record
	 * NOTE: For full length use hostnametoString(offset).length
	 */    
    private int hostnametoStringLen(byte[] in, int offset){
    	//Loop until 0 byte encountered return count
    	int out = 0;
    	
    	//Loop till 0 is found (start of type
    	while(in[offset] != 0x00){
    		out++;
    		offset++;
    	}
    	
    	return out;
    }

	
	
}

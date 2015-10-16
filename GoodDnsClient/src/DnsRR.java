
public class DnsRR {


	
	//Field	
	/*	1public static final int TYPE_A_RECORD = 1;		//IP adress
		public static final int TYPE_NS_RECORD = 2;		//Name server for label
		public static final int TYPE_CNAME_RECORD = 5;		//Cononical Name of domain
		public static final int TYPE_MX_RECORD = 15;	//!!!Mail server Name 
		public static final int TYPE_SOA_RECORD = 6;	//No such Domain
	*/	
		
		// 1 christine
		
		public static final int TYPE_A_RECORD = 1;		//IP adress
		public static final int TYPE_NS_RECORD = 2;		//Name server for label
		public static final int TYPE_MX_RECORD = 15;		//Cononical Name of domain
		public static final int TYPE_CNAME_RECORD = 5;	//!!!Mail server Name 
		//public static final int RTYPE_NONE = 6;	//No such Domain
/*//Field	
	public static final int TYPE_A_RECORD = 1;		//IP adress
	public static final int TYPE_NS_RECORD = 2;		//Name server for label
	public static final int TYPE_CNAME_RECORD = 5;		//Cononical Name of domain
	public static final int TYPE_MX_RECORD = 15;	//!!!Mail server Name 
	public static final int TYPE_SOA_RECORD = 6;	//No such Domain
	*/
	
	private String rrName = "";		//an owner name, domain name to which this record pertains.
	private int rrType;  			//16-bit, same as QTYPE can take 
	private int rrClass; 			//16-bit, should be 0*0001
	private int rrTTL;				//!!!a 32 bit signed integer that specifies the time interval that the resource record may be cached   
	private int rrLength; 			//16-bit, length of the RDATA field
	
	private String rrString;  		//!!!a variable length string of octets that describes the resource. The format of this information varies according to the TYPE and CLASS of the resource record.
//	private boolean tried; 			//!!!Set if record has been tried but unresponsive/errorous (w/o SOA)
//Constructor
	//Constructor
	//Type-A
	/*public DnsRR(String recName, int recType, int recClass, int recTTL, byte[] recData){
		this.rrName = recName;
		this.rrType = recType;
		this.rrClass = recClass;
		this.rrTTL = recTTL;
		this.rrString = BitMath.bytesToIPString(recData);   //!!!change the name
		this.tried =  false;
	}
 
	
	//Other Types
	public DnsRR(String recName, int recType, int recClass, int recTTL, String recData){
		rrName = recName;
		rrType = recType;
		rrClass = recClass;
		rrTTL = recTTL;	
		rrString = BitMath.convStops(recData);    //!!!change the name it conver 3www3eee2ca to www.eee.ca
		tried = false;
	}*/
	
	// Function to make an instance of record (type MX or NS or CNAME)
	public DnsRR(int recType, int recClass, int recTTL, String recData, String recName)
		{
		this.rrType = recType;
		this.rrClass = recClass;
		this.rrTTL = recTTL;
		this.rrString = recData;
	/*	rrString = BitMath.convStops(recData);  */  //chrisrine: move the conv in DNSpacket
		this.rrName = recName;
		//this.tried =  false;
		
		}
	
	// Function to make an instance of record (typeA)
	public DnsRR( int recType, int recClass, String recData,int recTTL, String recName)
		{
		this.rrType = recType;
		this.rrClass = recClass;
		
		/*	this.rrString = BitMath.bytesToIPString(recData); */  //chrisrine: move the conv in DNSpacket  
		this.rrString = recData;
		this.rrTTL = recTTL;
		this.rrName = recName;
		//this.tried =  false;
	}
	
	
//Methods
	/*public String getStringData(){
		return rrString;
	}
		
	public int getType(){
		return rrType;
	}*/
	public int getType(){
		return rrType;
	}
	
	// Function getter that allow to get the value of the resource record
	public String getStringData(){
		return rrString;
	}
	
	
	/*public String getName(){
		return rrName;
	}*/
	
	/*//Used to keep track if a record has been used ie tried (but didnt respond or responded error)
	public void setTried(){
		tried = true;
	}
	
	public boolean isTried(){
		return tried;
	}
*/
	
	//?????????????????????????DEBUG CODE:://christine
	public void printRecord(){
		if ( rrType == TYPE_A_RECORD)
			{
			System.out.print("IP:	");
			System.out.print(rrString+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(rrTTL+"	"+"It is authoritative?: ");
			}
		else if ( rrType == TYPE_MX_RECORD)
		{
			System.out.print("MX:	");
			System.out.print(rrString+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(rrTTL+"	"+"It is authoritative?: ");
				}
		else if ( rrType == TYPE_CNAME_RECORD)
		{
			System.out.print("CNAME:	");
			System.out.print(rrString+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(rrTTL+"	"+"It is authoritative?: ");
				}
		else if ( rrType == TYPE_NS_RECORD)
		{
			System.out.print("NS:	");
			System.out.print(rrString+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(rrTTL+"	"+"It is authoritative?: ");
			//System.out.println("rrTTL:" + rrTTL);
			//System.out.print("rrData: "); for(int i=0;i<rrData.length;i++) System.out.print((char)rrData[i]);
			//System.out.println();
			//System.out.println("rrString:" + rrString);
			//System.out.println("==================");
			//christine
				}
	
	
		}//done record christine
}

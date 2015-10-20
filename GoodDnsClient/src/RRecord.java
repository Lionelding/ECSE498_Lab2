
public class RRecord {


	
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
	
	private String recordname = "";		//an owner name, domain name to which this record pertains.
	private int recordtype;  			//16-bit, same as QTYPE can take 
	private int recordclass; 			//16-bit, should be 0*0001
	private int recordTTL;				//!!!a 32 bit signed integer that specifies the time interval that the resource record may be cached   
	private int recordlength; 			//16-bit, length of the RDATA field
	
	private String recorddatainstring;  		//!!!a variable length string of octets that describes the resource. The format of this information varies according to the TYPE and CLASS of the resource record.
//	private boolean tried; 			//!!!Set if record has been tried but unresponsive/errorous (w/o SOA)
//Constructor
	//Constructor
	//Type-A
	/*public DnsRR(String rName, int rType, int rClass, int rTTL, byte[] recData){
		this.rrName = rName;
		this.rrType = rType;
		this.rrClass = rClass;
		this.rrTTL = rTTL;
		this.rrString = BitMath.bytesToIPString(recData);   //!!!change the name
		this.tried =  false;
	}
 
	
	//Other Types
	public DnsRR(String rName, int rType, int rClass, int rTTL, String recData){
		rrName = rName;
		rrType = rType;
		rrClass = rClass;
		rrTTL = rTTL;	
		rrString = BitMath.convStops(recData);    //!!!change the name it conver 3www3eee2ca to www.eee.ca
		tried = false;
	}*/
	
	// Function to make an instance of record (type MX or NS or CNAME)
	public RRecord(int rType, int rClass, int rTTL, String recData, String rName)
		{
		this.recordtype = rType;
		this.recordclass = rClass;
		this.recordTTL = rTTL;
		this.recorddatainstring = recData;
	/*	rrString = BitMath.convStops(recData);  */  //chrisrine: move the conv in DNSpacket
		this.recordname = rName;
		//this.tried =  false;
		
		}
	
	// Function to make an instance of record (typeA)
	public RRecord( int rType, int rClass, String recData,int rTTL, String rName)
		{
		this.recordtype = rType;
		this.recordclass = rClass;
		
		/*	this.rrString = BitMath.bytesToIPString(recData); */  //chrisrine: move the conv in DNSpacket  
		this.recorddatainstring = recData;
		this.recordTTL = rTTL;
		this.recordname = rName;
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
		return recordtype;
	}
	
	// Function getter that allow to get the value of the resource record
	public String getStringData(){
		return recorddatainstring;
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
	public void RecordDetailsPrint(){
		if ( recordtype == TYPE_A_RECORD)
			{
			System.out.print("IP:	");
			System.out.print(recorddatainstring+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
			}
		else if ( recordtype == TYPE_MX_RECORD)
		{
			System.out.print("MX:	");
			System.out.print(recorddatainstring+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
				}
		else if ( recordtype == TYPE_CNAME_RECORD)
		{
			System.out.print("CNAME:	");
			System.out.print(recorddatainstring+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
				}
		else if ( recordtype == TYPE_NS_RECORD)
		{
			System.out.print("NS:	");
			System.out.print(recorddatainstring+"    ");
			//System.out.print("	secods can cache:	");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
			//System.out.println("rrTTL:" + rrTTL);
			//System.out.print("rrData: "); for(int i=0;i<rrData.length;i++) System.out.print((char)rrData[i]);
			//System.out.println();
			//System.out.println("rrString:" + rrString);
			//System.out.println("==================");
			//christine
				}
	
	
		}//done record christine
}

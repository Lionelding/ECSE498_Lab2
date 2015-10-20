
public class RRecord {

		public static final int TYPE_A_RECORD = 1;		//A record type
		public static final int TYPE_NS_RECORD = 2;		//NS record type
		public static final int TYPE_MX_RECORD = 15;		//MX Name type
		public static final int TYPE_CNAME_RECORD = 5;	//Conanical Name type

	
	private String recordname = "";		//String to store name field of RR
	private int recordtype;  			//Store the type of RR
	private int recordclass; 			//Store the class of RR
	private int recordTTL;				//Store the TTL of RR   
	private int recordlength; 			//Store the length of value in RR
	
	private String recorddatainstring;  		//Store the value of answer in RR

//Constructor

	
	// Method to import the answer in the format of RR (All other than A type)
	public RRecord(int rType, int rClass, int rTTL, String recData, String rName)
		{
		this.recordtype = rType;
		this.recordclass = rClass;
		this.recordTTL = rTTL;
		this.recorddatainstring = recData;
		this.recordname = rName;
		}
	
	// Method to make an instance of record (typeA)
	public RRecord( int rType, int rClass, String recData,int rTTL, String rName)
		{
		this.recordtype = rType;
		this.recordclass = rClass;
		this.recorddatainstring = recData;
		this.recordTTL = rTTL;
		this.recordname = rName;
	}
	
	
//Methods

	public int getType(){
		return recordtype;
	}
	
	// Method getter that allow to get the value of the resource record
	public String getStringData(){
		return recorddatainstring;
	}
	

	// Method to print the detailed information about RR
	public void RecordDetailsPrint(){
		if ( recordtype == TYPE_A_RECORD)
			{
			System.out.print("IP:	");
			System.out.print(recorddatainstring+"    ");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
			}
		else if ( recordtype == TYPE_MX_RECORD)
		{
			System.out.print("MX:	");
			System.out.print(recorddatainstring+"    ");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
				}
		else if ( recordtype == TYPE_CNAME_RECORD)
		{
			System.out.print("CNAME:	");
			System.out.print(recorddatainstring+"    ");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
				}
		else if ( recordtype == TYPE_NS_RECORD)
		{
			System.out.print("NS:	");
			System.out.print(recorddatainstring+"    ");
			System.out.print(recordTTL+"	"+"It is authoritative?: ");
				}
	
	
		}
}

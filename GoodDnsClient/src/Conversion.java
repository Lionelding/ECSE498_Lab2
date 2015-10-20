import java.util.BitSet;

// This class provides all the data type and data handling modules that allow easy data manipulation in other classes  
public class Conversion {
	
	
// This is used to convert 4 bytes in sequence into an integer number
public static int byte4tointeger(byte[] bytes, int index) {
	int a = 0;
	a= (bytes[index+0]<<24)&0xff000000|
	       (bytes[index+1]<<16)&0x00ff0000|
	       (bytes[index+2]<< 8)&0x0000ff00|
	       (bytes[index+3]<< 0)&0x000000ff;
		  
	return a;
	} 
		
	

// This allows to convert a single byte to a 8bits bitset
public static BitSet byteToBitset(byte b) {  
	BitSet set = BitSet.valueOf(new byte[] { b });
	return set;  
	}  	    
    
    
// This allows to convert 2 bytes in sequence into a single integer number
public static int byte2tointeger(byte[] bytes, int offset){
	
    int a= (bytes[offset+0]<< 8)&0x0000ff00|
    (bytes[offset+1]<< 0)&0x000000ff;
	   return a;
}
    
 
// This allows to convert a single bitset (8 bits) into a single byte
public static byte bitstobyte(BitSet bits) {
	byte[] a = new byte[1];
    	
    for(int i = 0; i<bits.length(); i++)
    	{
        if(bits.get(i) == true)
            a[0] |= (1 <<i) ;            
    	}
    return a[0];
    }

    
// This allows to convert one single byte into an integer number 
public static int onebytetointeger(byte b) {
	return (int) b & 0xFF;
	}


// This allows to take a string that presents a domain
// such as 3www6mcgill2ca (as digit/letters/digit/letters...format) and convert into a string in letter/dots/letter/dots...format(www.mcgill.ca)
public static String addrseqtodots(String nameWithFrontSeqNum){
	byte[] out = new byte[nameWithFrontSeqNum.length()];
	for(int i=0;i<nameWithFrontSeqNum.length();i++){
  		//if it is everything that can be presented in a domain name (other than dots)
		if((Character.isDigit(nameWithFrontSeqNum.charAt(i))) || 
  			(Character.isLetter(nameWithFrontSeqNum.charAt(i)))||
  			nameWithFrontSeqNum.charAt(i)=='?'  || 
  			nameWithFrontSeqNum.charAt(i)=='-'||
  			nameWithFrontSeqNum.charAt(i)=='@'){
  				out[i] = (byte)(nameWithFrontSeqNum.charAt(i));
  			} 
		//if it is a dot
		else { 
  				out[i] = (byte)'.';
  			}
  		}
  	// Print out the converted format but start by skipping the first substring because it is an extra and useless dot
	String shortenString = new String(out).substring(1);// create a string by decoding byte and then just take the whole without the leftmost string
  	return shortenString;
  	}
    		
    		
// This allows to convert an array of data into a single string that indicates an IP address with dots format
public static String bytesTowholeIPString (byte[] byteofpureIPdigits){
	String wholeIPwithdots = "";
		
	int number0 =  onebytetointeger(byteofpureIPdigits[0]);
	int number1 =  onebytetointeger(byteofpureIPdigits[1]);
	int number2 =  onebytetointeger(byteofpureIPdigits[2]);
	int number3 =  onebytetointeger(byteofpureIPdigits[3]);

	String string0 = String.valueOf(number0);
	String string1 = String.valueOf(number1);
	String string2 = String.valueOf(number2);
	String string3 = String.valueOf(number3);
    	
	wholeIPwithdots = string0 + "."+string1+"."+string2+ "."+ string3;
	
	return wholeIPwithdots;
	}
}
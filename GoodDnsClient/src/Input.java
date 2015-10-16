
public class Input {

	public String[] temp;
	public int timeout=4000; 	//!!!Timeout after 4 sec
	public int maxretries = 3;
	public int port=53;			//Default Port is 53;
	public String Dnsserver;
	public int type=1;
	public String askfor;
	
	
	public Input(String[] args){
		this.temp= args;
		parse();
	}
	
	public void parse(){
		for(int i=0;i<temp.length;i++){
			if (temp[i].contains("@")){
				Dnsserver=temp[i].substring(1);
			}
			if (temp[i].contains(".")&&!temp[i].contains("@")){
				askfor=temp[i];
			}
			if (temp[i].contains("-t")){
				timeout=Integer.parseInt(temp[i+1])*1000;
			}
			if (temp[i].contains("-r")){
				maxretries=Integer.parseInt(temp[i+1]);
			}
			if (temp[i].contains("-p")){
				port=Integer.parseInt(temp[i+1]);
			}
			if (temp[i].contains("-mx")){
				type=15;  //need to work on
			}
			if (temp[i].contains("-ns")){
				type=2;   //need to work on 
			}
		}
	}
	
	public int getTimeout(){
		System.out.println(timeout);
		return timeout;
	}
	
	public int getMaxretries(){
		return maxretries;
	}
	public int getPort(){
		System.out.println(port);
		return port;
	}
	public String getDnsserver(){
		System.out.println(Dnsserver);
		return Dnsserver;
	}
	public String getName(){
		System.out.print(askfor);
		return askfor;
	}
	public int getType(){
		System.out.println(type);
		return type;
	}
	
	public void output(){
		System.out.println("DnsClient sending request for " + this.getName()+'\n');
		System.out.println("Server: "+'\n');
		System.out.println("Request type: " + type +'\n');
		System.out.println("Response received after "+"seconds ("+"retries)");  //!!!
		System.out.println("***Answer Section ( "+" records)***"); 
		System.out.println("IP"+'\t');
		
		
	}
}

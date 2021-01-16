package verifier;

import java.util.Set;


public class Edge {
	public device from;
	public String fport;
	public device to;
	public Set<Integer> predicate;
	public Edge(device s1,device s2,String p)
	{
		this.from=s1;
		this.to=s2;
		this.fport=p;
	}
}


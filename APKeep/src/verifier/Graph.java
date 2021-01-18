package verifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;



public class Graph {
	public Set<device> V;
	public Set<Edge> E;
	public Map<Edge, Set<Integer>> A;
	public Graph() {
		this(new HashSet<>(), new HashSet<>(), new HashMap<>());
	}
	public Graph(Set<device> v,Set<Edge> e,Map<Edge, Set<Integer>> a) {
		this.A = a;
		this.E = e;
		this.V = v;
	}
	public void printGraph()
	{
		int count = 0;
		System.out.println("----------------------DFG---------------------");
		System.out.print("device:	");
		for(device v:V)
			System.out.print(v.name+" ");
		System.out.print("\nedge:");
		for(Entry<Edge, Set<Integer>> entry:A.entrySet())
		{
			count++;
			System.out.print("	("+entry.getKey().from.name+","+entry.getKey().to.name+","+entry.getKey().fport+")"+"{"+entry.getValue()+"} \n");
		}
		System.out.println("count:"+count);
	}
	public void clear()
	{
		this.A.clear();
		this.E.clear();
		this.V.clear();
	}
}

package builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PPM {
	private ArrayList<Element> elements;
	public Map<String, Set<Integer>> Pred;//port->predicate
	public Map<Integer, Set<String>> Port;//predicate->port
	public PPM() {
		this.elements = new ArrayList<>();
		this.Pred = new HashMap<>();
		this.Port = new HashMap<>();
	}
	public void creatFWelement() {
		FWelement e = new FWelement();
		for(String vlan:e.getPort_list()) {
			Pred.put(vlan, new HashSet<>());
		}
		elements.add(e);
		Pred.get("default").add(1);
		Port.put(1, new HashSet<>());
		Port.get(1).add("default");
	}
	/**
	 * @param n the device's name
	 * @param v vlan set
	 */
	public void creatFWelement(String n,Set<String> v) {
		FWelement e = new FWelement(n,v);
		for(String vlan:e.getPort_list()) {
			Pred.put(vlan, new HashSet<>());
		}
		elements.add(e);
		Pred.get("default").add(1);
		Port.put(1, new HashSet<>());
		Port.get(1).add("default");
		
	}
	public void creatACLelement() {
		ACLelement e = new ACLelement();
		elements.add(e);
		for(String acl:e.getPort_list()) {
			Pred.put(acl, new HashSet<>());
		}
		elements.add(e);
		Pred.get(e.getName()+"_permit").add(1);
		if(Port.get(1)==null)
			Port.put(1, new HashSet<>());
		Port.get(1).add(e.getName()+"_permit");		
	}
	/**
	 * @param n acl group name
	 */
	public void creatACLelement(String n) {
		ACLelement e = new ACLelement(n);
		for(String acl:e.getPort_list()) {
			Pred.put(acl, new HashSet<>());
		}
		elements.add(e);
		Pred.get(e.getName()+"_permit").add(1);
		if(Port.get(1)==null)
			Port.put(1, new HashSet<>());
		Port.get(1).add(e.getName()+"_permit");		
	}
	public void creatNATelement() {
		NATelement e = new NATelement();
		elements.add(e);
	}
	public Element getElement(String n) {
		for(Element e:this.elements)
		{
			if(e.getName().equals(n))
				return e;
		}
		return null;
	}
}

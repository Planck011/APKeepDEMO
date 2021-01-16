package builder;

import java.util.HashSet;
import java.util.Set;

import verifier.Port;

public class FWelement implements Element{
	private final String type = "Faward";
	private String name;
	private Set<String> port_list;
	public FWelement() {
		// TODO Auto-generated constructor stub
		this.port_list = new HashSet<>();
		this.name = "FW";	
		port_list.add("default");
	}
	public FWelement(String sname, Set<String> vlan) {
		this.name = sname+"FW";
		this.port_list = new HashSet<>();
		this.port_list.addAll(vlan);
		this.port_list.add("default");
	}
	public Set<String> getPort_list() {
		return port_list;
	}
	@Override
	public String getRuleType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
}
